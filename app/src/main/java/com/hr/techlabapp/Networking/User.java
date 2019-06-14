package com.hr.techlabapp.Networking;

import com.hr.techlabapp.AppConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
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

    public static ArrayList<User> getUsers(Integer permissionLevel) throws JSONException{
        return getUsers(null, permissionLevel);
    }

    public static ArrayList<User> getUsers(String username) throws JSONException{
        return getUsers(username, null);
    }

    public static ArrayList<User> getUsers(String username, Integer permissionLevel) throws JSONException{
        //Create JSON object;
        JSONObject request = new JSONObject()
                .put("username", AppConfig.currentUser.username)
                .put("token", AppConfig.currentUser.token)
                .put("requestType", "logout")
                .put("requestData", new JSONObject()
                        .put("username", username)
                        .put("permission", permissionLevel)
                );

        JSONArray response = (JSONArray)Connection.Send(request);
        ArrayList<User> users = new ArrayList<>();
        for (int i=0; i < response.length(); i++) {
            JSONObject data = response.getJSONObject(i);
            users.add(new User(data.getString("username"), null, 0, data.getInt("permission")));
        }
        return users;
    }
}