package com.hr.techlabapp.CustomViews;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.hr.techlabapp.R;

public class UserHistoryDialog extends DialogFragment {
	int userID;

	@Override
	public void setArguments(@Nullable Bundle args) {
		super.setArguments(args);
		try{
			// sets the user ID
			userID = (int)args.get("ID");
		}
		catch(NullPointerException ex){
			userID = 0;
		}
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
		// makes a new builder
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// sets the title to R.string.user_history_dialog_title with %07d replaced with the id of the user
		builder.setTitle(getResources().getString(R.string.user_history_dialog_title,userID)); //TODO Translate
		// sets the message
		builder.setMessage(getResources().getString(R.string.user_no_borrowed_items));
		// creates the dialog
		return builder.create();
	}
}
