package com.fooditsolutions.util.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * present in both AuthenticationService and CentralServer2023API, used to make user object to handle authentication.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private int id;
    private String userName;
    private String email;
    private String password;
    private String role;
    private String sessionKey;
}
