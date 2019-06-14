package com.hr.techlabapp.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.hr.techlabapp.Activities.NavHostActivity;
import com.hr.techlabapp.R;

import java.util.Locale;
import java.util.Objects;

public class Settings extends PreferenceFragmentCompat {

    @Override
    public void onAttach(@NonNull Context context) {
        ((NavHostActivity)context).currentFragment = this;
        super.onAttach(context);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        Preference clearLogin = getPreferenceScreen().findPreference("clear_saved_login");
        clearLogin.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(getResources().getString(R.string.clear_login_confirmation))
                        .setCancelable(false)
                        .setPositiveButton(R.string.continue_action, new DialogInterface.OnClickListener() {
	                        @Override
	                        public void onClick(DialogInterface dialog, int which) {
		                        SharedPreferences savedlogin = Objects.requireNonNull(getContext()).getSharedPreferences("savedlogin", 0);
		                        SharedPreferences.Editor editor = savedlogin.edit();
		                        editor.remove("username");
		                        editor.remove("password");
		                        editor.apply();
		                        Toast.makeText(getContext(), R.string.clear_login_complete, Toast.LENGTH_SHORT).show();
	                        }
                        })
                        .setNegativeButton(R.string.cancel, null);
                AlertDialog alert = builder.create();
                alert.show();
                return false;
            }
        });
    }

    //Code for static preferences. These are the ones that don't change a setting, but instead go to another fragment
    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        switch(preference.getKey()){
            case "contact":
                Navigation.findNavController(Objects.requireNonNull(getView())).navigate(R.id.action_settings2_to_contactActivity);
            case "tos":
                Toast toast = Toast.makeText(getContext(), "Nobody here but us chickens!", Toast.LENGTH_SHORT);
                toast.show();
        }

        return super.onPreferenceTreeClick(preference);
    }
}
