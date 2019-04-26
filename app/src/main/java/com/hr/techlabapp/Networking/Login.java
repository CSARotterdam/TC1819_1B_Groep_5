package com.hr.techlabapp.Networking;

import android.util.JsonReader;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Login {
    public static final String TAG = "TL.Networking-Login";

    /**
     * Given a username and password, will attempt to authenticate the client.
     * This cannot be run on the UI thread. Use AsyncTask.
     * @param username A string containing the username.
     * @param password  A string containing the password.
     * @return  A boolean that defines whether the login was successful.
     */
    public static boolean LoginUser(String username, String password){

        //Hash password
        String hash = "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(username.getBytes());
            byte[] bytes = md.digest(password.getBytes());

            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++){
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            hash = sb.toString();
        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }

        //Get system time, to use as request ID;
        String ID = String.valueOf(System.currentTimeMillis() / 1000);

        //Create JSON object
        JSONObject request;
        try {
            request = new JSONObject()
                .put("requestType", "login")
                .put("requestID", ID)
                .put("requestData", new JSONObject()
                    .put("username", username)
                    .put("password", hash)
                );
        } catch (JSONException e){
            throw new RuntimeException(e);
        }

        //Send request to server + receive response
        Boolean loginResult;
        try {
            JSONObject response = Connection.Send(request);
            JSONObject requestData = (JSONObject) response.get("requestData");
            Log.i(TAG, requestData.toString());
            loginResult = requestData.getBoolean("loginSuccesful");
        } catch (JSONException e){
            throw new RuntimeException(e);
        }

        return loginResult;
    }
}
