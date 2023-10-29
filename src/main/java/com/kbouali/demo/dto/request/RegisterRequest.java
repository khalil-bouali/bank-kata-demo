package com.kbouali.demo.dto.request;

import com.kbouali.demo.util.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    private String firstname;
    private String lastname;
    private String cin;
    private String password;
    private String phoneNumber;
    private Role role;

}
