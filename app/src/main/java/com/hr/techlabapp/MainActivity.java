package com.hr.techlabapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "TL-MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Networking.thread.run();
    }

    public void loginButton(View view){
        new Login().execute();
    }


    public class Login extends AsyncTask<String, Void, Boolean>{
        private ProgressDialog dialog;

        protected void onPreExecute(){
            dialog = new ProgressDialog(getApplicationContext());
            dialog.setMessage("Logging in...");
            dialog.show();
        }
        protected Boolean doInBackground(String... params){
            return Networking.login(params);
        }
        protected void onPostExecute(){
            dialog.dismiss();
        }
    }
}

