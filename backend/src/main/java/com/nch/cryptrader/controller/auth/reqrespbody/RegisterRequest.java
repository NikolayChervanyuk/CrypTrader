package com.nch.cryptrader.controller.auth.reqrespbody;

import com.nch.cryptrader.validator.AppEmail;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {

    @NotNull(message = "Email is mandatory")
    @AppEmail
    private String email;

    @NotNull(message = "Username is mandatory")
    @Size(min = 2, max = 40, message = "Username should be between 2 and 40 characters")
    private String username;

    @NotNull(message = "Password is mandatory")
    @Size(max = 72, message = "Password cannot be longer than 72 characters. " +
            "This limit is set because bcrypt uses only the first 72 characters anyway")
    @Size(min = 4, message = "Password cannot be shorter than 4 characters")
    private String password;
}
