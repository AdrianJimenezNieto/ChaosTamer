package com.taskflow.domain.service;

import com.taskflow.domain.model.Board;
import com.taskflow.domain.model.User;
import com.taskflow.domain.model.Card;
import com.taskflow.domain.model.TaskList;
import com.taskflow.domain.port.in.CreateBoardUseCase;
import com.taskflow.domain.port.in.GetBoardDetailsUseCase;
import com.taskflow.domain.port.in.GetBoardsByOwnerUseCase;
import com.taskflow.domain.port.in.CreateTaskListUseCase;
import com.taskflow.domain.port.in.CreateCardUseCase;
import com.taskflow.domain.port.in.ReorderCardUseCase;
import com.taskflow.domain.port.out.BoardRepositoryPort;
import com.taskflow.domain.port.out.CardRepositoryPort;
import com.taskflow.domain.port.out.TaskListRepositoryPort;
import com.taskflow.domain.port.out.UserRepositoryPort;
import com.taskflow.infrastructure.adapter.in.web.dto.ReorderCardRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService implements 
  CreateBoardUseCase, GetBoardsByOwnerUseCase,
  GetBoardDetailsUseCase, CreateTaskListUseCase,
  CreateCardUseCase, ReorderCardUseCase {
  
  private final BoardRepositoryPort boardRepositoryPort;
  private final UserRepositoryPort userRepositoryPort;
  private final TaskListRepositoryPort taskListRepositoryPort;
  private final CardRepositoryPort cardRepositoryPort;

  @Override
  public Board createBoard(CreateBoardCommand command, String ownerUsername) {
    // Find the owner of the board
    User owner = userRepositoryPort.findByEmail(ownerUsername)
                                    .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado."));
    // Create the Board object
    Board newBoard = Board.builder()
                          .title(command.getTitle())
                          .userId(owner.getId())
                          .build();
    // Save it using the persistence port
    return boardRepositoryPort.save(newBoard);
  }

  @Override
  public List<Board> getBoards(String ownerUsername) {
    // Find the user 
    User owner = userRepositoryPort.findByEmail(ownerUsername)
                                    .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado."));
    // Ask for all of tasklists for that userId
    return boardRepositoryPort.findAllByUserId(owner.getId());
  }

  @Override
  public Board getBoardDetails(Long boardId, String username) {
    // Find the user with the port (security check)
    User user = userRepositoryPort.findByEmail(username)
      .orElseThrow(() -> new EntityNotFoundException());
    // Find the board
    Board board = boardRepositoryPort.findById(boardId)
      .orElseThrow(() -> new EntityNotFoundException());

    // SECURITY CHECK be sure that the board is owned by the user
    if(!board.getUserId().equals(user.getId())) {
      throw new AccessDeniedException("No tienes permiso para ver este tablero");
    }

    // Load the lists of that board
    List<TaskList> lists = taskListRepositoryPort.findAllByBoard(boardId);

    // Load the cards for each taskList
    for (TaskList list : lists) {
      List<Card> cards = cardRepositoryPort.findAllByTaskListId(list.getId());
      list.setCards(cards);
    }

    // Assign the lists to the board
    board.setLists(lists);

    // Return the board
    return board;
  }

  @Override
  public TaskList createTaskList(CreateTaskListCommand command, String ownerUsername) {
    // Find the user (sec check)
    User user = userRepositoryPort.findByEmail(ownerUsername)
      .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

    // Find the board that belongs to
    Board board = boardRepositoryPort.findById(command.getBoardId())
      .orElseThrow(() -> new EntityNotFoundException("Tablero no encontrado"));

    // SECURITY CHECK
    if (!board.getUserId().equals(user.getId())) {
      throw new AccessDeniedException("No tienes permiso para añadir listas a este tablero");
    }

    // Create the domain object
    TaskList newTaskList = TaskList.builder()
      .title(command.getTitle())
      .boardId(command.getBoardId())
      .build();

    // Return using the persistence port
    return taskListRepositoryPort.save(newTaskList);
  }

  @Override
  public Card createCard(CreateCardCommand command, String ownerUsername) {
    // Find the user
    User user = userRepositoryPort.findByEmail(ownerUsername)
      .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
    // Find the tasklist that belongs to
    TaskList list = taskListRepositoryPort.findById(command.getTaskListId())
      .orElseThrow(() -> new EntityNotFoundException("La lista no encontrada"));
    // Find the board (to verify the owner)
    Board board = boardRepositoryPort.findById(list.getBoardId())
      .orElseThrow(() -> new EntityNotFoundException("Tablero no encontrado"));

    // SECURITY CHECK
    if (!board.getUserId().equals(user.getId())) {
      throw new AccessDeniedException("No tienes permiso para crear esta tarjeta.");
    }

    // Existing cards
    List<Card> existingCards = cardRepositoryPort.findAllByTaskListId(command.getTaskListId());

    // New Position
    Integer newPosition = existingCards.size();

    // Create the domain object
    Card newCard = Card.builder()
      .title(command.getTitle())
      .taskListId(command.getTaskListId())
      .cardOrder(newPosition)
      .build();

    // Persist with the repository port
    return cardRepositoryPort.save(newCard);
  }

  @Override
  @Transactional
  public void reorderCards(List<ReorderCardRequest> updates, String ownerUsername) {
    // Get the user
    User user = userRepositoryPort.findByEmail(ownerUsername)
      .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado."));

    // Identify what cards move 
    Set<Long> movedCards = updates.stream()
      .map(ReorderCardRequest::getCardId)
      .collect(Collectors.toSet());

    // Get the affected lists
    Set<Long> affectedListIds = updates.stream()
      .map(ReorderCardRequest::getNewTaskListId)
      .collect(Collectors.toSet());

    for (Long listId : affectedListIds) {
      // Get all the cards in the list 
      List<Card> currentListCards = cardRepositoryPort.findAllByTaskListId(listId);

      // Get the updates for card desined for this list
      List<ReorderCardRequest> listUpdates = updates.stream()
        .filter(u -> u.getNewTaskListId().equals(listId))
        .collect(Collectors.toList());

      Set<Long> allCardIdsInList = new java.util.HashSet<>(currentListCards.stream().map(Card::getId).collect(Collectors.toSet()));
      listUpdates.forEach(u -> allCardIdsInList.add(u.getCardId()));

      List<Card> cardsToUpdate = cardRepositoryPort.findAllByIds(allCardIdsInList);

      // Apply the changes
      for (Card card : cardsToUpdate) {

        verifyUserByCard(user, card);
        
        ReorderCardRequest update = listUpdates.stream()
          .filter(u -> u.getCardId().equals(card.getId()))
          .findFirst()
          .orElse(null);

        if (update != null) {
          card.setTaskListId(update.getNewTaskListId());
          card.setCardOrder(update.getNewCardOrder());
        }
      }
      reindexList(cardsToUpdate, movedCards);
    }
  } 

  private void reindexList(List<Card> cards, Set<Long> movedCardIds) {
    // Controller of the comparator
    HashSet<Long> alreadyCompared = new HashSet<>();
    // Sort them using the comparator
    Comparator<Card> comparator = (Card c1, Card c2) -> {
      // Intended order
      int orderComparison = c1.getCardOrder().compareTo(c2.getCardOrder());

      if (orderComparison != 0) {
        return orderComparison;
      }

      // Tie breaker
      boolean c1WasMoved = movedCardIds.contains(c1.getId());
      boolean c2WasMoved = movedCardIds.contains(c2.getId());

      if ((c1WasMoved && !c2WasMoved) && !(alreadyCompared.contains(c1.getId()) && alreadyCompared.contains(c2.getId()))) {
        alreadyCompared.add(c1.getId());
        alreadyCompared.add(c2.getId());
        if (c1.getId() < c2.getId()){
          return 1;
        }
        return -1;
      }
      if ((!c1WasMoved && c2WasMoved) && !(alreadyCompared.contains(c1.getId()) && alreadyCompared.contains(c2.getId()))) {
        alreadyCompared.add(c1.getId());
        alreadyCompared.add(c2.getId());
        if (c2.getId() < c1.getId()){
          return -1;
        }
        return 1;
      }

      return c2.getId().compareTo(c1.getId());
    };

    cards.sort(comparator);

    for (int i = 0; i < cards.size(); i++) {
      Card card = cards.get(i);

      if (!card.getCardOrder().equals(i)) {
        card.setCardOrder(i);
      }
      cardRepositoryPort.save(card);
    }
  }

  private void verifyUserByCard(User user, Card card) {
  
    TaskList list = taskListRepositoryPort.findById(card.getTaskListId())
      .orElseThrow(() -> new EntityNotFoundException("La lista no encontrada"));
    Board board = boardRepositoryPort.findById(list.getBoardId())
      .orElseThrow(() -> new EntityNotFoundException("Tablero no encontrado"));

    if (!board.getUserId().equals(user.getId())) {
      throw new AccessDeniedException("No tienes permiso para crear esta tarjeta.");
    }
  }
}
