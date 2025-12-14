package com.taskflow.domain.port.in;

import com.taskflow.domain.model.User;

public interface GetUserUseCase {
    User getUser(String username);
}
