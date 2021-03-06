package com.hr.techlabapp.Networking;

import android.util.Log;

import com.hr.techlabapp.AppConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;

import static com.hr.techlabapp.Networking.Authentication.auth;

class Connection {
    private static final String TAG = "TL.Networking-Conn.";
    private static int current = 0;

    /**
     * Sends a request to the server.
     * @param request A JSONObject containing the request that needs to be sent.
     * @return A JSONObject containing the server's response to the request.
     */
    static Object Send(JSONObject request){
        HttpURLConnection connection;
        Object responseData;
        String address = AppConfig.serverAddress;

        try {
            int curr = current;
            current++;
            //Connect to server
            URL url = new URL("http://" + address + "/");
            Log.e("Send"+curr, "Connecting to server...");
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
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

            //Receive data;
            Log.e("Send"+curr, "Receiving data...");
            DataInputStream inStream;
            try{
                 inStream = new DataInputStream(connection.getInputStream());
            } catch (IOException e){
                Log.e("Connection.Send()", e.getMessage(), e);
                throw new Exceptions.NetworkingException();
            }

            BufferedReader d = new BufferedReader(new InputStreamReader(inStream));
            StringBuilder sb = new StringBuilder();
            String s;
            //noinspection ConstantOnRightSideOfComparison
            while ((s = d.readLine()) != null) {
                sb.append(s);
            }
            Log.e("Send"+curr, "Created response json...");
            JSONObject response = new JSONObject(sb.toString());
            connection.disconnect();
            String reason = response.optString("reason");
            String message = response.optString("message");

            switch (reason) {
                case "ExpiredToken":
                    Log.i(TAG, "Token expired; fetching new token...");
                    if (auth(AppConfig.currentUser.username, AppConfig.currentUser.hash)) {
                        Log.i(TAG, "Reauthorized.");
                        request.put("token", AppConfig.currentUser.token);
                        return Send(request);
                    } else {
                        Log.i(TAG, "Failed to authenticate.");
                        AppConfig.currentUser = null;
                        throw new Exceptions.TokenRenewalException();
                    }
                case "Exception":
                    throw new Exceptions.NetworkingException(message);
                default: // This one uses reflection
                    if (reason.equals("null")) break;
                    try {
                        // Get all custom exception classes and find one that matches the response reason
                        Class<?>[] classes = Exceptions.class.getClasses();
                        for (Class<?> c : classes) {
                            if (c.getSimpleName().equals(reason)) {
                                Log.i("Exeption", message);
                                throw (Exceptions.NetworkingException)
                                c.getConstructor(String.class).newInstance(message);
                            }
                        }
                    }
                    // Thanks, java reflection
                    catch (NoSuchMethodException ignored){ }
                    catch (IllegalAccessException ignored) { }
                    catch (InstantiationException ignored) { }
                    catch (InvocationTargetException ignored) { }
                    // Fallback to unexpected response
                    throw new Exceptions.UnexpectedServerResponse(message);
            }
            responseData = response.opt("responseData");
        } catch (IOException e){
            Log.e("Connection.Send()", e.getMessage(), e);
            throw new Exceptions.NetworkingException(e);
        } catch (JSONException e) {
            Log.e("Connection.Send()", e.getMessage(), e);
            throw new Exceptions.NetworkingException(e);
        }
        return responseData;
    }
}

