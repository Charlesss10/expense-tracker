package com.charles;

import java.sql.Date;

public class UserAccount {
    private String firstName;
    private String lastName;
    private String username;
    private Date birthday;
    private String password;
    private String email;
    private int accountId;

    public UserAccount(String firstName, String lastName, String username, Date birthday, String password,
            String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.birthday = birthday;
        this.password = password;
        this.email = email;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getBirthday() {
        return this.birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAccountId() {
        return this.accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }
}