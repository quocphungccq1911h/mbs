package com.ecommerce.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor   // Lombok sẽ tạo constructor với full field
@NoArgsConstructor    // Cần để Jackson/MapStruct vẫn hoạt động
public class UserResponse {
    private Long id;
    private String username;
    private String fullName;
    private String userStatus;
    private String email;
}
