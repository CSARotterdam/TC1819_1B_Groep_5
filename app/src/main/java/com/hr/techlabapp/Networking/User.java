package com.hr.techlabapp.Networking;

import java.util.HashMap;

public class User {
    public static final int NOT_LOGGED_IN = 0;
    public static final int USER = 1;
    public static final int COLLABORATOR = 2;
    public static final int ADMIN = 3;

    String hash;
    long token;

    public String username;
    public int permissionLevel;

    public User(String username, String hash, long token, int permissionLevel){
        this.username = username;
        this.hash = hash;
        this.token = token;
        this.permissionLevel = permissionLevel;
    }

    protected HashMap<String, Object> getValues() {
        HashMap<String, Object> out = new HashMap<>();
        out.put("username", username);
        out.put("hash", hash);
        out.put("token", token);
        out.put("permissionLevel", permissionLevel);
        return out;
    }
}