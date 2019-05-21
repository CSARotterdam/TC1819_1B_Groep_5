package com.hr.techlabapp.Networking;

import java.util.HashMap;

public class User {
    String username;
    String hash;
    long token;
    int permissionLevel;

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