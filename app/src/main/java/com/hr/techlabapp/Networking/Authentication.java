package com.hr.techlabapp.Networking;

import android.util.Log;

import com.hr.techlabapp.Fragments.loginFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Authentication {
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
        String hash = getPasswordHash(username, password);
        return auth(username, hash);
    }

    /**
     * Given a username and password hash, will attempt to authenticate the client.
     * @param username A string containing a username.
     * @param hash A string containining a salted hash of the username.
     * @return A boolean that defines whether the login was successful.
     */
    public static boolean auth(String username, String hash){
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
            JSONObject responseData = Connection.Send(request);
            loginResult = responseData.getBoolean("loginSuccesful");
            loginFragment.currentUser = new User(username, hash, responseData.getLong("token"), responseData.getInt("permissionLevel"));
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
    public static int registerUser(String username, String password) throws JSONException {
        //Hash password
        String hash = getPasswordHash(username, password);

        //Create JSON object
        JSONObject request;
        request = new JSONObject()
            .put("requestType", "registerUser")
            .put("requestData", new JSONObject()
                .put("username", username)
                .put("password", hash)
            );

        JSONObject responseData = Connection.Send(request);
        Boolean registerUserSuccessful = responseData.getBoolean("registerUserSuccessful");

        if(registerUserSuccessful){
            loginFragment.currentUser = new User(username, hash, responseData.getLong("token"), responseData.getInt("permissionLevel"));
            return 0;
        } else {
            String reason;
            try{
                 reason = responseData.getString("reason");
            } catch (JSONException e){
                throw new RuntimeException(e);
            }
            if(reason.equals("AlreadyExists")){
                return 1;
            } else if (reason.equals("InvalidPassword")){
                return 2;
            }
        }
        return -1;
    }

    /*
    * Attempts to end the current session. Fails if the client isn't authenticated.
    * This cannot be run on the UI thread. Use AsyncTask.
    * @return A boolean that shows whether the logout was successful.
     */
    public static boolean logout() throws Exceptions.UnexpectedServerResponse {
        //Create JSON object
        JSONObject request;
        try {
            request = new JSONObject()
                    .put("requestType", "logout")
                    .put("requestData", new JSONObject()
                            .put("username", loginFragment.currentUser.username)
                            .put("token", loginFragment.currentUser.token)
                    );
        } catch (JSONException e) {
            return false;
        }

        //Send request and wait for response
        Boolean logoutSuccessful;
        String reason = null;
        JSONObject requestData;
        try {
            JSONObject responseData = Connection.Send(request);
            logoutSuccessful = responseData.getBoolean("success");
            reason = responseData.getString("reason");
        } catch (JSONException e) {
            return false;
        }

        //If the logout was successful, clear the currentUser and return true.
        //If the logout was unsuccesful, authenticate and try again.
        if(logoutSuccessful){
            loginFragment.currentUser = null;
            return true;
        } else if(reason == "Invalid or expired token"){
            if(auth(loginFragment.currentUser.username, loginFragment.currentUser.hash)){
                return logout();
            } else {
                loginFragment.currentUser = null;
                return true;
            }
        } else {
            throw new Exceptions.UnexpectedServerResponse();
        }
    }

    /**
     * Creates a salted hash of a password, using a username.
     * @param username A string containing a username.
     * @param password A string containing a password.
     * @return A salted hash of the password.
     */
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