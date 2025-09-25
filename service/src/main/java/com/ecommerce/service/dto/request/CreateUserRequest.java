package com.ecommerce.service.dto.request;

import lombok.Data;

@Data
public class CreateUserRequest {
    private String username;
    private String password;
    private String fullName;
    private String email;

}
