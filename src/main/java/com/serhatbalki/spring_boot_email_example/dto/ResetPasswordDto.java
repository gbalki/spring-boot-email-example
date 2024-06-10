package com.serhatbalki.spring_boot_email_example.dto;

import lombok.Data;

@Data
public class ResetPasswordDto {

    private String passwordResetToken;

    private String password;
}
