package com.hr.techlabapp.Networking;

import android.util.Log;

import com.hr.techlabapp.Fragments.loginFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.hr.techlabapp.Networking.Authentication.auth;

public class Connection {
    private static final String TAG = "TL.Networking-Conn.";

    /**
     * Sends a request to the server.
     * @param request A JSONObject containing the request that needs to be sent.
     * @return A JSONObject containing the server's response to the request.
     */
    static Object Send(JSONObject request){
        HttpURLConnection connection;
        Object responseData;
        String address = "192.168.2.101"; //TODO: How will we even get the right address without hardcoding it?

        try {
            //Connect to server
            URL url = new URL("http://" + address + "/");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();

            //Send data
            DataOutputStream outSteam = new DataOutputStream(connection.getOutputStream());
            outSteam.writeBytes(request.toString());
            outSteam.flush();
            outSteam.close();

            //Receive data
            DataInputStream inStream = new DataInputStream(connection.getInputStream());
            BufferedReader d = new BufferedReader(new InputStreamReader(inStream));
            StringBuffer sb = new StringBuffer();
            String s;
            while ((s = d.readLine()) != null) {
                sb.append(s);
            }
            JSONObject response = new JSONObject(sb.toString());
            Log.i(TAG, response.toString());
            JSONObject responseBody = (JSONObject) response.get("body");
            String reason = responseBody.optString("reason");
            String message = responseBody.optString("message");

            switch (reason) {
                case "ExpiredToken":
                    if(auth(loginFragment.currentUser.username, loginFragment.currentUser.hash)){
                        Log.i(TAG, "Token expired; fetching new token...");
                        request.put("token", loginFragment.currentUser.token);
                        return Send(request);
                    } else {
                        loginFragment.currentUser = null;
                        throw new Exceptions.TokenRenewalException();
                    }
                case "AccessDenied": throw new Exceptions.AccessDenied(message);
                case "InvalidLogin": throw new Exceptions.InvalidLogin(message);
                case "InvalidRequestType": throw new Exceptions.InvalidRequestType(message);
                case "NoSuchProduct": throw new Exceptions.NoSuchProduct(message);
                case "NoSuchProductCategory": throw new Exceptions.NoSuchProductCategory(message);
                case "NoSuchUser": throw new Exceptions.AlreadyExists(message);
                case "AlreadyExists": throw new Exceptions.AlreadyExists(message);
                case "MissingArguments": throw new Exceptions.MissingArgument(message);
                case "ServerError": throw new Exceptions.ServerError(message);
                case "InvalidArguments": throw new Exceptions.InvalidArguments(message);
                case "Exception": throw new Exceptions.NetworkingException(message);
                case "": throw new Exceptions.UnexpectedServerResponse();
            }
            responseData = responseBody.opt("responseData");
        } catch (IOException e){
            Log.e("Connection.Send()", e.getMessage(), e);
            throw new Exceptions.NetworkingException(e);
        } catch (JSONException e){
            Log.e("Connection.Send()", e.getMessage(), e);
            throw new Exceptions.NetworkingException(e);
        }
        return responseData;
    }
}

