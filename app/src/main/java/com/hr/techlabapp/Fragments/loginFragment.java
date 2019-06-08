package com.hr.techlabapp.Fragments;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import com.hr.techlabapp.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class loginFragment extends Fragment {
	private static final String TAG = "TL-Login";
	public Context context;

	public loginFragment() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_login, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		this.context = Objects.requireNonNull(getView()).getContext();

		Button loginButton = getView().findViewById(R.id.login);
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText usernameField = Objects.requireNonNull(getView()).findViewById(R.id.username);
				String username = usernameField.getText().toString();
				EditText passwordField = getView().findViewById((R.id.password));
				String password = passwordField.getText().toString();
				new  LoginActivity().execute(username, password);
			}
		});

		Button registerButton = getView().findViewById(R.id.register);
		registerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText usernameField = Objects.requireNonNull(getView()).findViewById(R.id.username);
				String username = usernameField.getText().toString();
				EditText passwordField = getView().findViewById((R.id.password));
				String password = passwordField.getText().toString();
				new  RegisterActivity().execute(username, password);
			}
		});
		Button testButton = getView().findViewById(R.id.testButton);
		testButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new TestActivity().execute();
			}
		});
	}

	@SuppressLint("StaticFieldLeak")
	public class TestActivity extends AsyncTask<Void, Void, Void> {
		protected final Void doInBackground(Void... voids) {
			Log.i(TAG, "exec");
			try {
				Authentication.LoginUser("test", "password");
				Log.i(TAG, "authed");

				ArrayList<String> IDs = new ArrayList<>();
				IDs.add("lizard_image");
				HashMap<String, Bitmap> res = Product.getImages(IDs);
				Log.i(TAG, Boolean.toString(res.containsKey("lizard_image")));

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	@SuppressLint("StaticFieldLeak")
	public class LoginActivity extends AsyncTask<String, Void, String> {
		private ProgressDialog dialog;

		protected void onPreExecute(){
			dialog = new ProgressDialog(getContext());
			dialog.setMessage(getResources().getString(R.string.logging_in));
			dialog.show();
		}
		protected String doInBackground(String... params){
			try{
				if(Authentication.LoginUser(params[0], params[1])){
					return getResources().getString(R.string.login_success);
				} else {
					return getResources().getString(R.string.login_failed);
				}
			}catch (Exceptions.ServerConnectionFailed e){
				return getResources().getString(R.string.unexpected_error);
			}
		}
		protected void onPostExecute(String msg){
			dialog.dismiss();
			if(msg == getResources().getString(R.string.login_success)){
				Navigation.findNavController(Objects.requireNonNull(getView())).navigate(R.id.action_loginFragment_to_productListFragment);
			}
			Toast msgToast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
			msgToast.show();
		}
	}

	@SuppressLint("StaticFieldLeak")
	public class RegisterActivity extends AsyncTask<String, Void, String> {
		private ProgressDialog dialog;

		protected void onPreExecute(){
			dialog = new ProgressDialog(getContext());
			dialog.setMessage(getResources().getString(R.string.loading));
			dialog.show();
		}
		protected String doInBackground(String... params){
			try {
				try {
					Authentication.registerUser(params[0], params[1]);
					return getResources().getString(R.string.registered);
				} catch (Exceptions.AlreadyExists e) {
					return getResources().getString(R.string.user_already_exists);
				} catch (Exceptions.InvalidPassword e) {
					return getResources().getString(R.string.invalid_password);
				} catch (Exceptions.InvalidUsername e){
					return getResources().getString(R.string.invalid_username);
				} catch (Exceptions.NetworkingException e) {
					return getResources().getString(R.string.unexpected_error);
				}
			} catch (JSONException e){
				throw new RuntimeException(e);
			}
		}
		protected void onPostExecute(String message){
			dialog.dismiss();
			Toast msgToast = Toast.makeText(context, message, Toast.LENGTH_LONG);
			msgToast.show();
		}
	}
}
