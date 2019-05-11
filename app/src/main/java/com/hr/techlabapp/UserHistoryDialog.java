package com.hr.techlabapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

public class UserHistoryDialog extends DialogFragment {
	int userID;

	@Override
	public void setArguments(@Nullable Bundle args) {
		super.setArguments(args);
		try{
			userID = (int)args.get("ID");
		}
		catch(NullPointerException ex){
			userID = 0;
		}
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getResources().getString(R.string.user_history_dialog_title,userID));
		builder.setMessage("This user hasn't borrowed any items.");
		return builder.create();
	}
}
