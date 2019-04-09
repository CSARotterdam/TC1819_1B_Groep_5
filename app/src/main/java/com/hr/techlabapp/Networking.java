package com.hr.techlabapp;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Networking{
    private static final String TAG = "TL-Networking";

    private static BlockingQueue<JSONObject> queue = new LinkedBlockingDeque<JSONObject>();

    public static Thread thread = new Thread(){
        public void run() {
            String address = "localhost"; //TODO: How will we even get the right address without hardcoding it?
            try {
                URL url = new URL("http://" + address + "/");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            while(true){
                try{
                    JSONObject request = queue.take();
                } catch (InterruptedException e){
                    throw new RuntimeException(e);
                }

            }
        }
    };

    public static boolean login(String... params){
        long ID = System.currentTimeMillis() / 1000;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(params[1].getBytes());
            String hash = md.digest().toString();
            Log.i(TAG, hash);

            JSONObject request = new JSONObject()
                    .put("requestType", "login")
                    .put("requestID", ID)
                    .put("requestData", new JSONObject()
                        .put("username", params[0])
                        .put("password", hash)
                    );
            queue.add(request);

        }catch(JSONException e) {
            throw new RuntimeException(e);
        }catch(NoSuchAlgorithmException e){
            throw new RuntimeException(e);
        }
        return true;
    }
}