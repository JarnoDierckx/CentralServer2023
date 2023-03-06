package com.fooditsolutions.authenticationservice.model;


/**
 * present in both AuthenticationService and CentralServer2023API, used to make user object to handle authentication.
 */
public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private String role;
    private String sessionKey;
    public User(int id,String userName, String email, String password) {
        this.id=id;
        this.name = userName;
        this.email = email;
        this.password = password;
    }

    public User(String userName, String email, String password) {
        this.name = userName;
        this.email = email;
        this.password = password;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String userName) {
        this.name = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
