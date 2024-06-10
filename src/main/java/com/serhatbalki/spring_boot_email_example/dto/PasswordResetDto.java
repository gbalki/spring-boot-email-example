package com.serhatbalki.spring_boot_email_example.dto;

import com.serhatbalki.spring_boot_email_example.entity.User;
import lombok.Data;

@Data
public class PasswordResetDto {
    private String newPassword;

    public PasswordResetDto(User user) {
        this.setNewPassword(user.getPassword());
    }
}
