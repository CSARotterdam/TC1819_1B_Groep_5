package com.hr.techlabapp;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Networking{
    private static final String TAG = "TL-Networking";

    private static BlockingQueue<JSONObject> inQueue = new LinkedBlockingDeque<JSONObject>();
    private static BlockingQueue<JSONObject> outQueue = new LinkedBlockingDeque<JSONObject>();

    public static Thread thread = new Thread(){
        public void run() {
            String address = "192.168.178.9"; //TODO: How will we even get the right address without hardcoding it?
            HttpURLConnection urlConnection;
            try {
                URL url = new URL("http://" + address + "/");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.connect();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            while(true){
                try{
                    JSONObject request = inQueue.take();
                    DataOutputStream outSteam = new DataOutputStream(urlConnection.getOutputStream());
                    outSteam.writeBytes(request.toString());
                    outSteam.flush();
                    outSteam.close();

                    DataInputStream inStream = new DataInputStream(urlConnection.getInputStream());
                    BufferedReader d = new BufferedReader(new InputStreamReader(inStream));
                    StringBuffer sb = new StringBuffer();
                    String s = "";
                    while((s = d.readLine()) != null) {
                        sb.append(s);
                    }
                    final JSONObject jsonOut = new JSONObject(sb.toString());
                    outQueue.add(jsonOut);

                } catch (InterruptedException e){
                    throw new RuntimeException(e);
                } catch (IOException e){
                    throw new RuntimeException(e);
                } catch (JSONException e){
                    throw new RuntimeException(e);
                }

            }
        }
    };

    public static boolean login(String... params){
        String ID = String.valueOf(System.currentTimeMillis() / 1000);

        //Hash password
        String hash;
        try {
            //Hash password
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(params[1].getBytes());
            hash = md.digest().toString();
        } catch(NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        //Create JSON object
        JSONObject request;
        try {
            request = new JSONObject()
                    .put("requestType", "login")
                    .put("requestID", ID)
                    .put("requestData", new JSONObject()
                            .put("username", params[0])
                            .put("password", hash)
                    );

        } catch (JSONException e){
            throw new RuntimeException(e);
        }

        //Send request & wait for response
        Boolean loginResult;
        try{
            inQueue.add(request);
            JSONObject response = new JSONObject();
            Log.i(TAG, "Waiting for login request to complete.");
            while(true) {
                response = outQueue.peek();
                if(response == null){
                    continue;
                }

                if ( response.get("requestID").equals(ID)) {
                    Log.i(TAG, "Received request.");
                    try {
                        response = outQueue.take();
                        break;
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            JSONObject requestData = (JSONObject)response.get("requestData");
            Log.i(TAG, requestData.toString());
            loginResult = requestData.getBoolean("loginSuccesful");
        }catch(JSONException e) {
            throw new RuntimeException(e);
        }

        return loginResult;
    }
}