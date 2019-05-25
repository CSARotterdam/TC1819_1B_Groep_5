package com.hr.techlabapp.Networking;

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
    public static boolean LoginUser(String username, String password){
        String hash = getPasswordHash(username, password);
        return auth(username, hash);
    }

    /**
     * Given a username and password hash, will attempt to authenticate the client.
     * @param username A string containing a username.
     * @param hash A string containining a salted hash of the username.
     * @return A boolean that defines whether the login was successful.
     */
    static boolean auth(String username, String hash){
        //Create JSON object
        try {
            JSONObject request = new JSONObject()
                .put("requestType", "login")
                .put("requestData", new JSONObject()
                   .put("username", username)
                   .put("password", hash)
           );

            JSONObject responseData = (JSONObject) Connection.Send(request);
            loginFragment.currentUser = new User(username, hash, responseData.getLong("token"), responseData.getInt("permissionLevel"));
        } catch (Exceptions.InvalidLogin e){
            return false;
        } catch (JSONException e){
            throw new RuntimeException(e);
        }
        return true;
    }

    /**
     * Given a username and password, will attempt to register a user.
     * If user registration is successful, the client will be automatically authenticated.
     * This cannot be run on the UI thread. Use AsyncTask.
     *
     * @param username A string containing the username.
     * @param password A string containing the password.
    **/
    public static void registerUser(String username, String password) throws JSONException {
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

        Connection.Send(request);
    }

    /*
    * Attempts to end the current session. Fails if the client isn't authenticated.
    * This cannot be run on the UI thread. Use AsyncTask.
     */
    public static void logout() throws JSONException {
        //Create JSON object;
        JSONObject request = new JSONObject()
            .put("requestType", "logout")
            .put("requestData", new JSONObject()
                .put("username", loginFragment.currentUser.username)
                .put("token", loginFragment.currentUser.token)
            );

        Connection.Send(request);
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
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            hash = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hash;
    }
}