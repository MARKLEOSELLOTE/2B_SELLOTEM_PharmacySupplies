package com.scc.model;

public record User(int user_Id, String full_name, String username, String password, String roles, String status, String date_created) {
    @Override
    public String toString() {
        return String.format("[%d] %s (%s) - Status: %s", user_Id, username, roles, status);
    }
}