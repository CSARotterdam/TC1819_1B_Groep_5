package com.hr.techlabapp.Networking;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Connection {
    private final String TAG = "TL.Networking-Connection";

    /**
     * Sends a request to the server.
     * @param request A JSONObject containing the request that needs to be sent.
     * @return A JSONObject containing the server's response to the request.
     */
    static JSONObject Send(JSONObject request){
        HttpURLConnection connection;
        JSONObject response;
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
            response = new JSONObject(sb.toString());

        } catch (IOException e){
            throw new RuntimeException(e);
        } catch (JSONException e){
            throw new RuntimeException(e);
        }

        return response;
    }
}

/**
 * Thrown when the client receives an unexpected exception. Examples include responses
 * that the client doesn't know how to process.
 */
class UnexpectedServerResponseException extends Exception{
    public UnexpectedServerResponseException(){
        super();
    }

    public UnexpectedServerResponseException(String message) {
        super(message);
    }
}