package com.hr.techlabapp.Networking;

import android.util.JsonReader;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Users {
    public static final String TAG = "TL.Networking-Login";

    /**
     * Given a username and password, will attempt to authenticate the client.
     * This cannot be run on the UI thread. Use AsyncTask.
     *
     * @param username A string containing the username.
     * @param password A string containing the password.
     * @return A boolean that defines whether the login was successful.
     */
    public static boolean LoginUser(String username, String password) {

        //Hash password
        String hash = getPasswordHash(username, password);

        //Create JSON object
        JSONObject request;
        try {
            request = new JSONObject()
                    .put("requestType", "login")
                    .put("requestData", new JSONObject()
                            .put("username", username)
                            .put("password", hash)
                    );
        } catch (JSONException e) {
            return false;
        }

        //Send request to server + receive response
        Boolean loginResult;
        try {
            JSONObject response = Connection.Send(request);
            JSONObject requestData = (JSONObject) response.get("requestData");
            Log.i(TAG, requestData.toString());
            loginResult = requestData.getBoolean("loginSuccesful");
        } catch (JSONException e) {
            return false;
        }

        return loginResult;
    }

    /**
     * Given a username and password, will attempt to register a user.
     * If user registration is successful, the client will be automatically authenticated.
     * This cannot be run on the UI thread. Use AsyncTask.
     *
     * @param username A string containing the username.
     * @param password A string containing the password.
     * @return An int, which shows the result of the registration attempt;
     *      -1. A clientside exception was thrown.
     *      0. Registration successful.
     *      1. A user already exists with the specified username.
     *      2. The specified password is not valid.
     */
    public static int registerUser(String username, String password) {
        //Hash password
        String hash = getPasswordHash(username, password);

        //Create JSON object
        JSONObject request;
        try {
            request = new JSONObject()
                    .put("requestType", "registerUser")
                    .put("requestData", new JSONObject()
                            .put("username", username)
                            .put("password", hash)
                    );
        } catch (JSONException e) {
            return -1;
        }

        Boolean registerUserSuccessful;
        JSONObject requestData;
        try {
            JSONObject response = Connection.Send(request);
            requestData = (JSONObject) response.get("requestData");
            Log.i(TAG, requestData.toString());
            registerUserSuccessful = requestData.getBoolean("registerUserSuccessful");
        } catch (JSONException e) {
            return -1;
        }

        if(registerUserSuccessful){
            return 0;
        } else {
            String reason;
            try{
                 reason = requestData.getString("reason");
            } catch (JSONException e){
                throw new RuntimeException(e);
            }
            if(reason.equals("User already exists.")){
                return 1;
            } else if (reason.equals("Password not valid.")){
                return 2;
            }
        }
        return -1;
    }

    private static String getPasswordHash(String username, String password){
        String hash = "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(username.getBytes());
            byte[] bytes = md.digest(password.getBytes());

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            hash = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hash;
    }
}