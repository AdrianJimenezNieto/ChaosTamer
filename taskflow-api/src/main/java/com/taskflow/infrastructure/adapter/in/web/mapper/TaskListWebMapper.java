package com.taskflow.infrastructure.adapter.in.web.mapper;

import com.taskflow.domain.model.TaskList;
import com.taskflow.domain.model.Card;
import com.taskflow.domain.port.in.CreateTaskListUseCase;
import com.taskflow.infrastructure.adapter.in.web.dto.CreateTaskListRequest;
import com.taskflow.infrastructure.adapter.in.web.dto.TaskListResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
