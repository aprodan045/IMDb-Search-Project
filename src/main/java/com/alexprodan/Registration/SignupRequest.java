package com.alexprodan.Registration;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class SignupRequest {
    @NotEmpty(message = "is required")
    @Size(min = 1, message = "is required")
    private String firstName;

    @NotEmpty(message = "is required")
    @Size(min = 1, message = "is required")
    private String lastName;

    @NotEmpty(message = "is required")
    @Size(min = 1, message = "is required")
    @Email(message = "Email is not valid", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    private String email;

    @NotEmpty(message = "is required")
    @Size(min = 1, message = "is required")
    private String password;

    @NotEmpty(message = "is required")
    @Size(min = 1, message = "is required")
    private String role;

}
