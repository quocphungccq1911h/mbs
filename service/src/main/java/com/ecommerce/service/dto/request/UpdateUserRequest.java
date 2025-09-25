package com.ecommerce.service.dto.request;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String fullName;
    private String email;
    private String status;
}
