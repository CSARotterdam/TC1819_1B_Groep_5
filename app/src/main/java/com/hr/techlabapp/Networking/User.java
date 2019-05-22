package com.hr.techlabapp.Networking;

import android.support.v4.util.ArrayMap;

import java.util.Map;

public class User extends TechlabData {
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

    protected Map<String, Object> getValues() {
        Map<String, Object> out = new ArrayMap<>();
        out.put("username", username);
        out.put("hash", hash);
        out.put("token", token);
        out.put("permissionLevel", permissionLevel);
        return out;
    }
}