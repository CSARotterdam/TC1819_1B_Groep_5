package com.hr.techlabapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import android.widget.EditText;
import android.widget.Toast;

import com.hr.techlabapp.Networking.Users;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "TL-MainActivity";
    public static Users.User currentUser = null;
    public Context context;


	//TODO: navGraph navigation
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context = getApplicationContext();
    }

    public void loginButton(View view){
        EditText usernameField = findViewById(R.id.username);
        String username = usernameField.getText().toString();
        EditText passwordField = findViewById((R.id.password));
        String password = passwordField.getText().toString();
        new LoginActivity().execute(username, password);
    }

    @SuppressLint("StaticFieldLeak")
    public class LoginActivity extends AsyncTask<String, Void, Boolean>{
        private ProgressDialog dialog;

        protected void onPreExecute(){
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Logging in...");
            dialog.show();
        }
        protected Boolean doInBackground(String... params){
            return Users.LoginUser(params[0], params[1]);
        }
        @SuppressLint("ShowToast")
        protected void onPostExecute(Boolean result){
            dialog.dismiss();
            Toast msgToast;
            if(result){
                msgToast = Toast.makeText(context, "Logged in!", Toast.LENGTH_SHORT);
                startActivity(new Intent(MainActivity.this,ProductList.class));
            } else {
                msgToast = Toast.makeText(context, "Login failed!", Toast.LENGTH_SHORT);
            }
            msgToast.show();

        }
    }

    //TODO Fix repetition. This stuff is basically identical to the code above.
    public void RegisterButton(View view){
        EditText usernameField = findViewById(R.id.username);
        String username = usernameField.getText().toString();
        EditText passwordField = findViewById((R.id.password));
        String password = passwordField.getText().toString();
        new RegisterUserActivity().execute(username, password);
    }

    @SuppressLint("StaticFieldLeak")
    public class RegisterUserActivity extends AsyncTask<String, Void, Integer>{
        private ProgressDialog dialog;

        protected void onPreExecute(){
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Registering...");
            dialog.show();
        }
        protected Integer doInBackground(String... params){
            return Users.registerUser(params[0], params[1]);
        }
        @SuppressLint("ShowToast")
        protected void onPostExecute(Integer result){
            dialog.dismiss();
            Toast msgToast;
            //TODO Make password/username boxes turn red when they're invalid?
            if(result.equals(0)){
                msgToast = Toast.makeText(context, "Registered!", Toast.LENGTH_SHORT);
                startActivity(new Intent(MainActivity.this,ProductList.class));
            } else if(result.equals(1)){
                msgToast = Toast.makeText(context, "A user already exists with this name!", Toast.LENGTH_LONG);
            } else if(result.equals(2)){
                msgToast = Toast.makeText(context, "Invalid password", Toast.LENGTH_SHORT);
            } else {
                msgToast = Toast.makeText(context, "Registration failed!", Toast.LENGTH_SHORT);
            }
            msgToast.show();
        }
    }
}

