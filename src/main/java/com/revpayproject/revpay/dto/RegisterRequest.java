package com.revpayproject.revpay.dto;

import jakarta.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private String phoneNumber;

    private List<SecurityQuestionDto> securityQuestions;

}