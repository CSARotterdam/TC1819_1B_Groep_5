package com.hr.techlabapp.Fragments;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.hr.techlabapp.Activities.NavHostActivity;
import com.hr.techlabapp.Networking.Authentication;
import com.hr.techlabapp.Networking.Exceptions;
import com.hr.techlabapp.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class loginFragment extends Fragment {
	public Context context;

	public loginFragment() {
		// Required empty public constructor
	}

	@Override
	public void onAttach(@NonNull Context context) {
		((NavHostActivity)context).currentFragment = this;
		super.onAttach(context);
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

		//Get saved username/password, if any;
		SharedPreferences savedlogin = Objects.requireNonNull(getContext()).getSharedPreferences("savedlogin", 0);
		String username = savedlogin.getString("username", "");
		String password = savedlogin.getString("password", "");

		//If we have saved data, automatically check the remember_login checkbox
		assert password != null;
		assert username != null;
		if(!username.equals("") && !password.equals("")){
			CheckBox rememberlogin = getView().findViewById(R.id.remember_login);
			rememberlogin.setChecked(true);
		}

		//Set username/password values to edittext boxes
		EditText usernameField = getView().findViewById(R.id.username);
		usernameField.setText(username);
		EditText passwordField = getView().findViewById(R.id.password);
		passwordField.setText(password);

		Button loginButton = getView().findViewById(R.id.login);
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText usernameField = Objects.requireNonNull(getView()).findViewById(R.id.username);
				String username = usernameField.getText().toString();
				EditText passwordField = getView().findViewById((R.id.password));
				String password = passwordField.getText().toString();
				new  LoginActivity().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, username, password);
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
				new  RegisterActivity().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, username, password);
			}
		});
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
			if(msg.equals(getResources().getString(R.string.login_success))){

				CheckBox rememberlogin = Objects.requireNonNull(getView()).findViewById(R.id.remember_login);

				SharedPreferences savedlogin = Objects.requireNonNull(getContext()).getSharedPreferences("savedlogin", 0);
				SharedPreferences.Editor editor = savedlogin.edit();
				if(rememberlogin.isChecked()){
					EditText usernameField = getView().findViewById(R.id.username);
					editor.putString("username", usernameField.getText().toString());
					EditText passwordField = getView().findViewById(R.id.password);
					editor.putString("password", passwordField.getText().toString());
				} else {
					editor.remove("username");
					editor.remove("password");
				}
				editor.apply();

				//Hide keyboard if it is open
				final InputMethodManager inputManager = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
				if(inputManager.isAcceptingText()){
					inputManager.hideSoftInputFromWindow(Objects.requireNonNull(getActivity().getCurrentFocus()).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				}

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
