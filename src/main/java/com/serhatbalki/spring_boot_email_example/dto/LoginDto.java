package com.serhatbalki.spring_boot_email_example.dto;

import lombok.Data;

@Data
public class LoginDto {

    private String email;
    private String password;
}
