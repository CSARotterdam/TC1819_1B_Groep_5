package com.hr.techlabapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import androidx.navigation.Navigation;

public class MainActivity extends AppCompatActivity {


	//TODO: navGraph navigation
	Button Login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		Login = findViewById(R.id.login);
		Login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//goes to the product list
				startActivity(new Intent(MainActivity.this,ProductList.class));
			}
		});
    }
}
