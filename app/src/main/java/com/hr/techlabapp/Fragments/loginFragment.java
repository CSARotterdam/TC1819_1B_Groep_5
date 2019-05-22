package com.hr.techlabapp.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.navigation.Navigation;

import com.hr.techlabapp.Networking.Authentication;
import com.hr.techlabapp.Networking.Exceptions;
import com.hr.techlabapp.Networking.Product;
import com.hr.techlabapp.Networking.ProductCategory;
import com.hr.techlabapp.Networking.User;
import com.hr.techlabapp.R;

import org.json.JSONException;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class loginFragment extends Fragment {
	private static final String TAG = "TL-Login";
	public static User currentUser;
	public Context context;

	Button LoginButton;
	Button RegisterButton;
	Button TestButton;

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
		this.context = getView().getContext();

		LoginButton = getView().findViewById(R.id.login);
		LoginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText usernameField = (EditText)getView().findViewById(R.id.username);
				String username = usernameField.getText().toString();
				EditText passwordField = (EditText)getView().findViewById((R.id.password));
				String password = passwordField.getText().toString();
				new  LoginActivity().execute(username, password);
			}
		});

		RegisterButton = getView().findViewById(R.id.register);
		RegisterButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText usernameField = (EditText)getView().findViewById(R.id.username);
				String username = usernameField.getText().toString();
				EditText passwordField = (EditText)getView().findViewById((R.id.password));
				String password = passwordField.getText().toString();
				new  RegisterActivity().execute(username, password);
			}
		});

		TestButton = getView().findViewById(R.id.button);
		TestButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new  testActivity().execute();
			}
		});
	}

	public class LoginActivity extends AsyncTask<String, Void, Boolean> {
		private ProgressDialog dialog;

		protected void onPreExecute(){
			dialog = new ProgressDialog(getContext());
			dialog.setMessage("Logging in...");
			dialog.show();
		}
		protected Boolean doInBackground(String... params){
			return Authentication.LoginUser(params[0], params[1]);
		}
		protected void onPostExecute(Boolean result){
			dialog.dismiss();
			Toast msgToast;
			if(result){
				msgToast = Toast.makeText(context, "Logged in!", Toast.LENGTH_SHORT);
				Navigation.findNavController(getView()).navigate(R.id.action_loginFragment_to_productListFragment);
			} else {
				msgToast = Toast.makeText(context, "Login failed!", Toast.LENGTH_SHORT);
			}
			msgToast.show();
		}
	}

	public void registerButton(View view){
		EditText usernameField = (EditText)getView().findViewById(R.id.username);
		String username = usernameField.getText().toString();
		EditText passwordField = (EditText)getView().findViewById((R.id.password));
		String password = passwordField.getText().toString();
		new  RegisterActivity().execute(username, password);
	}

	public class RegisterActivity extends AsyncTask<String, Void, Integer> {
		private ProgressDialog dialog;

		protected void onPreExecute(){
			dialog = new ProgressDialog(getContext());
			dialog.setMessage("Registering...");
			dialog.show();
		}
		protected Integer doInBackground(String... params){
			try {
				Log.i(TAG, "register");
				try {
					Authentication.registerUser(params[0], params[1]);
					return 0;
				}catch (Exceptions.AlreadyExists _){
					return 1;
				}catch (Exceptions.InvalidPassword _){
					return 2;
				} catch (Exceptions.NetworkingException _){
					return -1;
				}
			} catch (JSONException e){
				throw new RuntimeException(e);
			}
		}
		protected void onPostExecute(Integer result){
			dialog.dismiss();
			Toast msgToast;
			if(result.equals(0)){
				msgToast = Toast.makeText(context, "Registered!", Toast.LENGTH_SHORT);
				Navigation.findNavController(getView()).navigate(R.id.action_loginFragment_to_productListFragment);
			} else if(result.equals(1)){
				msgToast = Toast.makeText(context, "A user already exists with this name.", Toast.LENGTH_SHORT);
			} else if(result.equals(2)){
				msgToast = Toast.makeText(context, "That password is not valid!", Toast.LENGTH_SHORT);
			} else {
				msgToast = Toast.makeText(context, "An unexpected error occured. Please try again later.", Toast.LENGTH_LONG);
			}
			msgToast.show();
		}
	}


	public void testButton(View view){
		new  RegisterActivity().execute();
	}
	public class testActivity extends AsyncTask<String, Void, Integer> {
		private ProgressDialog dialog;

		protected void onPreExecute(){
			dialog = new ProgressDialog(getContext());
			dialog.setMessage("...");
			dialog.show();
		}
		protected Integer doInBackground(String... params){
			try {
				Authentication.LoginUser("test", "password");

				HashMap<String, String> name = new HashMap<>();
				name.put("en", "two lizards");

				ProductCategory.deleteProductCategory("test");
			} catch (Exception e){
				throw new RuntimeException(e);
			}
			return 1;
		}
		protected void onPostExecute(Integer result){
			dialog.dismiss();
		}
	}
}
