package com.chaostamer.infrastructure.adapter.in.web.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.chaostamer.domain.model.Card;
import com.chaostamer.domain.model.TaskList;
import com.chaostamer.domain.port.in.CreateTaskListUseCase;
import com.chaostamer.infrastructure.adapter.in.web.dto.CreateTaskListRequest;
import com.chaostamer.infrastructure.adapter.in.web.dto.TaskListResponse;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TaskListWebMapper {

    // Inyections
    private final CardWebMapper cardWebMapper;

    // From domain to DTO Response
    public TaskListResponse toResponse(TaskList list) {
        return TaskListResponse.builder()
            .id(list.getId())
            .title(list.getTitle())
            .listOrder(list.getListOrder()) 
            .cards(
                list.getCards() != null ?
                list.getCards().stream()
                    .sorted(Comparator.comparing(Card::getCardOrder))
                    .map(cardWebMapper::toResponse) 
                    .collect(Collectors.toList()) :
                List.of()
            )
            .build();
    }

    // From request to command (CREATE TASKLIST)
    public CreateTaskListUseCase.CreateTaskListCommand toCommand(CreateTaskListRequest request, Long boardId) {
        return new CreateTaskListUseCase.CreateTaskListCommand(request.getTitle(), boardId);
    }
}
