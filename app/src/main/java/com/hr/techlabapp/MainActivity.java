package com.hr.techlabapp;

import android.content.Intent;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hr.techlabapp.Networking.Login;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "TL-MainActivity";
    public Context context;


	//TODO: navGraph navigation
	Button LoginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LoginButton = findViewById(R.id.login);
        LoginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//goes to the product list
				startActivity(new Intent(MainActivity.this,ProductList.class));
			}
		});
        this.context = getApplicationContext();
    }

    public void loginButton(View view){
        EditText usernameField = (EditText)findViewById(R.id.UsernameField);
        String username = usernameField.getText().toString();
        EditText passwordField = (EditText)findViewById((R.id.PasswordField));
        String password = passwordField.getText().toString();
        new LoginActivity().execute(username, password);
    }


    public class LoginActivity extends AsyncTask<String, Void, Boolean>{
        private ProgressDialog dialog;

        protected void onPreExecute(){
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Logging in...");
            dialog.show();
        }
        protected Boolean doInBackground(String... params){
            return Login.LoginUser(params[0], params[1]);
        }
        protected void onPostExecute(Boolean result){
            dialog.dismiss();
            Toast msgToast;
            if(result){
                msgToast = Toast.makeText(context, "Logged in!", Toast.LENGTH_SHORT);
            } else {
                msgToast = Toast.makeText(context, "Login failed!", Toast.LENGTH_SHORT);
            }
            msgToast.show();
        }
    }
}

