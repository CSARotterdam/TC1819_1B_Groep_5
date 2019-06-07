package com.hr.techlabapp.CustomViews;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.hr.techlabapp.Networking.Exceptions;
import com.hr.techlabapp.Networking.Product;
import com.hr.techlabapp.R;

import org.json.JSONException;

public class DeleteItemDialog extends DialogFragment {

	String ID;

	@Override
	public void setArguments(@Nullable Bundle args) {
		super.setArguments(args);
		try{
			//gets the id of the product
			assert args != null;
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
		builder.setTitle(getResources().getString(R.string.delete_item_title,ID)); //TODO Translate
		// sets the layout
		builder.setView(R.layout.delete_dialog_layout);
		// sets buttons and listeners
		builder.setPositiveButton(getResources().getString(R.string.OK), null);
		builder.setNegativeButton(getResources().getString(R.string.cancel),null);
		// creates the dialog
		return builder.create();
	}
	@RequiresApi(15)
	private Dialog CreateDialog15(@Nullable Bundle savedInstanceState){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getResources().getString(R.string.delete_item_title,ID)); //TODO Translate

		// makes a layoutInflater
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// inflates the view with the layout
		View v = inflater.inflate(R.layout.delete_dialog_layout,null);
		// sets the view
		builder.setView(v);

		builder.setPositiveButton(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				new deleteProduct().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}
		});
		builder.setNegativeButton(getResources().getString(R.string.cancel), null);
		return builder.create();
	}

	@SuppressLint("StaticFieldLeak")
	private class deleteProduct extends AsyncTask<String, Void, Integer>{
		private ProgressDialog dialog;

		@Override
		protected  void onPreExecute(){
			dialog = new ProgressDialog(getContext());
			dialog.setMessage(getResources().getString(R.string.loading));
			dialog.show();
		}

		@Override
		protected Integer doInBackground(String... params) {
			CheckBox box = getView().findViewById(R.id.deleteProductCheckbox);
			if(box.isChecked()){
				try{
					Product.deleteProduct(params[0]);
				} catch (JSONException e){
					return -1;
				} catch (Exceptions.CannotDelete e){
					return 2;
				}
				return 0;
			} else {
				EditText countField = getView().findViewById(R.id.stockAmount);
				int count = Integer.getInteger(countField.getText().toString());
				try {
					Product.deleteProductItems(count);
				} catch (JSONException e) {
					return -1;
				} catch (Exceptions.CannotDelete e){
					return 2;
				}
				return 1;
			}
		}

		@Override
		protected void onPostExecute(Integer result){
			dialog.hide();
			String message;
			switch(result){
				case 0:
					message = getResources().getString(R.string.product_removal_success);
					break;
				case 1:
					message = getResources().getString(R.string.stock_lowered);
					break;
				case 2:
					message = getResources().getString(R.string.product_removal_failed_loaned_out);
					break;
				default:
					message = getResources().getString(R.string.unexpected_error);
					break;
			}

			AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
			builder.setMessage(message)
					.setCancelable(false)
					.setPositiveButton(getResources().getString(R.string.OK), null);
			AlertDialog alert = builder.create();
			alert.show();
		}
	}
}
