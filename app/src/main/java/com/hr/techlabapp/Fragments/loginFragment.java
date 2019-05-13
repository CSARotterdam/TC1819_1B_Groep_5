package com.hr.techlabapp.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hr.techlabapp.Networking.Login;
import com.hr.techlabapp.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class loginFragment extends Fragment {
	private static final String TAG = "TL-MainActivity";
	public Context context;


	//TODO: navGraph navigation
	Button LoginButton;

	public loginFragment() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_login, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		LoginButton = getView().findViewById(R.id.login);
		LoginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//goes to the product list
			}});
		this.context = getView().getContext();

	}

	public void loginButton(View view){
		EditText usernameField = (EditText)getView().findViewById(R.id.username);
		String username = usernameField.getText().toString();
		EditText passwordField = (EditText)getView().findViewById((R.id.password));
		String password = passwordField.getText().toString();
		new  LoginActivity().execute(username, password);
	}


	public class LoginActivity extends AsyncTask<String, Void, Boolean> {
		private ProgressDialog dialog;

		protected void onPreExecute(){
			dialog = new ProgressDialog(getContext());
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
