package com.hr.techlabapp.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
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
import com.hr.techlabapp.Networking.Statistics;
import com.hr.techlabapp.Networking.User;
import com.hr.techlabapp.R;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class loginFragment extends Fragment {
	private static final String TAG = "TL-Login";
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
		TestButton = getView().findViewById(R.id.testButton);
		TestButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new TestActivity().execute();
			}
		});
	}

	public class TestActivity extends AsyncTask<Void, Void, HashMap<String, HashMap<String, Integer>>>{
		@SafeVarargs
		protected final HashMap<String, HashMap<String, Integer>> doInBackground(Void... voids) {
			Log.i(TAG, "exec");
			HashMap map = new HashMap();
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
			return map;
		}
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

	public class RegisterActivity extends AsyncTask<String, Void, Integer> {
		public static final String TAG = "RegisterAction";

		private ProgressDialog dialog;

		protected void onPreExecute(){
			dialog = new ProgressDialog(getContext());
			dialog.setMessage("Registering...");
			dialog.show();
		}
		protected Integer doInBackground(String... params){
			try {
				try {
					Authentication.registerUser(params[0], params[1]);
					return 0;
				} catch (Exceptions.AlreadyExists e) {
					return 1;
				} catch (Exceptions.InvalidPassword e) {
					return 2;
				} catch (Exceptions.NetworkingException e) {
					return -1;
				}
			} catch (JSONException e){
				// TODO less retarded exception handling
				// I mean sure you can keep arguing it cant happen but lets just
				// handle it properly to avoid stupid problems.
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
}
