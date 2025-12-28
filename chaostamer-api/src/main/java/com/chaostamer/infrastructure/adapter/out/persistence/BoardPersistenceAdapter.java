package com.chaostamer.infrastructure.adapter.out.persistence;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.chaostamer.domain.model.Board;
import com.chaostamer.domain.port.out.BoardRepositoryPort;
import com.chaostamer.infrastructure.adapter.out.persistence.entity.BoardEntity;
import com.chaostamer.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.chaostamer.infrastructure.adapter.out.persistence.mapper.BoardPersistenceMapper;
import com.chaostamer.infrastructure.adapter.out.persistence.repository.BoardJpaRepository;
import com.chaostamer.infrastructure.adapter.out.persistence.repository.UserJpaRepository;

import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BoardPersistenceAdapter implements BoardRepositoryPort {
  
  private final BoardJpaRepository boardJpaRepository;
  private final UserJpaRepository userJpaRepository;
  private final BoardPersistenceMapper boardMapper;

  @Override 
  public Board save(Board board) {
    BoardEntity boardEntity;
    if (board.getId() != null) {
      boardEntity = boardJpaRepository.findById(board.getId())
        .orElseThrow(() -> new EntityNotFoundException("No se ha encontrado el tablero"));

      boardEntity.setTitle(board.getTitle());
    }
    else {
      boardEntity = boardMapper.toEntity(board);
      UserEntity owner = userJpaRepository.findById(board.getUserId())
        .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + board.getUserId()));
      boardEntity.setOwner(owner);
    }
    BoardEntity savedEntity = boardJpaRepository.save(boardEntity); // Persist on the database

    return boardMapper.toDomain(savedEntity);
  }

  @Override
  public List<Board> findAllByUserId(Long userId) {
    List <BoardEntity> entities = boardJpaRepository.findAllByOwnerId(userId);

    return entities.stream()
                  .map(boardMapper::toDomain)
                  .collect(Collectors.toList());
  }

  @Override
  public Optional<Board> findById(Long boardId) {
    // Search with JPA and use the mapper to convert
    return boardJpaRepository.findById(boardId)
      .map(boardMapper::toDomain);
  }

  @Override
  public void deleteById(Long boardId) {
    boardJpaRepository.deleteById(boardId);
  }
}
