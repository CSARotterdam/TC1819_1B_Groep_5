package com.hr.techlabapp.Activitys;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import com.hr.techlabapp.R;

public class DeleteItemDialog extends DialogFragment {

	String ID;

	@Override
	public void setArguments(@Nullable Bundle args) {
		super.setArguments(args);
		try{
			//gets the id of the product
			ID = (String) args.get("ID");
		}catch (NullPointerException ex){
			ID = "";
		}
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
		// decides how to make the dialog
		return Build.VERSION.SDK_INT >= 21 ? CreateDialog21(savedInstanceState) : CreateDialog15(savedInstanceState);
	}

	@RequiresApi(21)
	private Dialog CreateDialog21(@Nullable Bundle savedInstanceState){
		// makes a new builder
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// sets the title to R.string.delete_item_title with %s replaced with the id of the product
		builder.setTitle(getResources().getString(R.string.delete_item_title,ID));
		// sets the layout
		builder.setView(R.layout.delete_dialog_layout);
		// sets buttons and listeners
		builder.setPositiveButton("OK", null);
		builder.setNegativeButton("CANCEL",null);
		// creates the dialog
		return builder.create();
	}
	@RequiresApi(15)
	private Dialog CreateDialog15(@Nullable Bundle savedInstanceState){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getResources().getString(R.string.delete_item_title,ID));

		// makes a layoutInflater
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// inflates the view with the layout
		View v = inflater.inflate(R.layout.delete_dialog_layout,null);
		// sets the view
		builder.setView(v);

		builder.setPositiveButton("OK", null);
		builder.setNegativeButton("CANCEL",null);
		return builder.create();
	}
}
