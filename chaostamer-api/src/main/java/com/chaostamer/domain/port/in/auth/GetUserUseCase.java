package com.chaostamer.domain.port.in.auth;

import com.chaostamer.domain.model.User;

public interface GetUserUseCase {
    User getUser(String username);
}
