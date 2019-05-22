package com.hr.techlabapp.Networking;

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
    private final String TAG = "TL.Networking-Connection";

    /**
     * Sends a request to the server.
     * @param request A JSONObject containing the request that needs to be sent.
     * @return A JSONObject containing the server's response to the request.
     */
    static JSONObject Send(JSONObject request){
        HttpURLConnection connection;
        JSONObject responseData;
        String address = "192.168.178.9"; //TODO: How will we even get the right address without hardcoding it?

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
            String s = "";
            while ((s = d.readLine()) != null) {
                sb.append(s);
            }
            JSONObject response = new JSONObject(sb.toString());
            responseData = (JSONObject) response.get("responseData");
            String reason = responseData.getString("reason");

            if(reason.equals("ExpiredToken")) {
                if(auth(loginFragment.currentUser.username, loginFragment.currentUser.hash)){
                    request.put("token", loginFragment.currentUser.token);
                    return Send(request);
                } else {
                    loginFragment.currentUser = null;
                    throw new Exceptions.TokenRenewalException();
                }
            } else if(reason.equals("AccessDenied")){
                throw new Exceptions.AccessDenied();
            } else if(reason.equals("InvalidLogin")) {
                throw new Exceptions.InvalidLogin();
            } else if(reason.equals("NoSuchProduct")) {
                throw new Exceptions.NoSuchProduct();
            } else if(reason.equals("NoSuchProductCategory")) {
                throw new Exceptions.NoSuchProductCategory();
            } else if(reason.equals("NoSuchUser")) {
                throw new Exceptions.NoSuchUser();
            } else if(reason.equals("AlreadyExists")) {
                throw new Exceptions.AlreadyExists();
            } else if(reason.equals("MissingArgument")){
                throw  new Exceptions.MissingArgument(responseData.toString());
            } else if(reason.equals("ServerError")){
                throw  new Exceptions.ServerError(responseData.toString());
            } else if(reason.equals("InvalidArguments")){
                throw  new Exceptions.InvalidArguments(responseData.toString());
            } else if(!responseData.isNull("reason")){
                throw new Exceptions.UnexpectedServerResponse(responseData.toString());
            }

        } catch (IOException e){
            throw new RuntimeException(e);
        } catch (JSONException e){
            throw new RuntimeException(e);
        }

        return responseData;
    }
}

