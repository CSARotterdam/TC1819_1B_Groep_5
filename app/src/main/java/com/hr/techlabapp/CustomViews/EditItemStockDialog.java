package com.hr.techlabapp.CustomViews;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.hr.techlabapp.Networking.Exceptions;
import com.hr.techlabapp.Networking.Product;
import com.hr.techlabapp.Networking.ProductItem;
import com.hr.techlabapp.R;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Objects;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

public class EditItemStockDialog extends DialogFragment {
	private static final String PRODUCT_NAME_KEY = "ProductName";
	private static final String PRODUCT_DESCRIPTION_KEY = "ProductDescription";
	private static final String PRODUCT_IMAGE_KEY = "ProductImage";
	private static final String PRODUCT_IMAGE_ID_KEY = "ProductImageID";
	private static final String PRODUCT_ID_KEY = "ProductId";
	private static final String PRODUCT_MANUFACTURER_KEY = "ProductManufacturer";
	private static final String PRODUCT_CATEGORY_KEY = "ProductCategory";
	private static final String PRODUCT_AVAILABILITY_KEY = "ProductAvailability";
	private Product product;
	public Context context;

	public View view;

	@SuppressLint("SetTextI18n")
	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

		product = new Product(
				getArguments().getString(PRODUCT_ID_KEY),
				getArguments().getString(PRODUCT_MANUFACTURER_KEY),
				getArguments().getString(PRODUCT_CATEGORY_KEY),
				(HashMap<String, String>) getArguments().getSerializable(PRODUCT_NAME_KEY),
				(HashMap<String, String>) getArguments().getSerializable(PRODUCT_DESCRIPTION_KEY),
				getArguments().getString(PRODUCT_IMAGE_ID_KEY),
				(Bitmap) getArguments().getParcelable(PRODUCT_IMAGE_KEY));

		// makes a new builder
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// sets the title to R.string.delete_item_title with %s replaced with the id of the product
		builder.setTitle(getResources().getString(R.string.edit_stock_for,product.getName())); //TODO Translate
		// sets the layout
		view = Objects.requireNonNull(getActivity()).getLayoutInflater().inflate(R.layout.edit_item_stock_layout, null);
		builder.setView(view);

		TextView count = view.findViewById(R.id.availabilityCount);
		count.setText(GridItem.Availability.get(product.ID).get("inStock")+"");

		// sets buttons and listeners
		builder.setPositiveButton(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.i("test", product.ID);
				new deleteProduct().executeOnExecutor(THREAD_POOL_EXECUTOR, product.ID);
			}
		});
		builder.setNegativeButton(getResources().getString(R.string.cancel),null);
		// creates the dialog

		EditText countField = view.findViewById(R.id.stockAmount);
		countField.setText(Objects.requireNonNull(GridItem.Availability.get(product.ID)).get("total")+"");

		return builder.create();
	}

	@SuppressLint("StaticFieldLeak")
	private class deleteProduct extends AsyncTask<String, Void, String>{
		private ProgressDialog dialog;
		public View v;

		@Override
		protected  void onPreExecute(){
			dialog = new ProgressDialog(context);
			dialog.setMessage(getResources().getString(R.string.loading));
			dialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			CheckBox box = Objects.requireNonNull(view).findViewById(R.id.deleteProductCheckbox);
			if(box.isChecked()){
				try{
					Product.deleteProduct(params[0]);
				} catch (JSONException e){
					return context.getResources().getString(R.string.unexpected_error);
				} catch (Exceptions.CannotDelete e){
					return context.getResources().getString(R.string.product_removal_failed_loaned_out);
				} catch (Exceptions.NetworkingException e){
					return context.getResources().getString(R.string.unexpected_error);
				}
				return context.getResources().getString(R.string.product_removal_success);
			} else {
				EditText countField = view.findViewById(R.id.stockAmount);
				String countFieldContents = countField.getText().toString();
				if(countFieldContents.equals("")){
					return context.getResources().getString(R.string.no_stock_count_given);
				}
				int count = Integer.parseInt(countFieldContents);
				int availability = GridItem.Availability.get(product.ID).get("total");
				if(count > availability){
					try {
						ProductItem.addProductItem(params[0], count-availability);
					} catch (JSONException e){
						return context.getResources().getString(R.string.unexpected_error);
					} catch (Exceptions.InvalidArguments e){
						return context.getResources().getString(R.string.too_many_products);
					} catch (Exceptions.NetworkingException e){
						return context.getResources().getString(R.string.unexpected_error);
					}
					return context.getResources().getString(R.string.stock_increased);
				} else if(count < availability){
					try {
						Product.deleteProductItems(params[0], availability-count);
					} catch (JSONException e) {
						return context.getResources().getString(R.string.unexpected_error);
					} catch (Exceptions.CannotDelete e){
						return context.getResources().getString(R.string.product_removal_failed_loaned_out);
					} catch (Exceptions.NetworkingException e){
						return context.getResources().getString(R.string.unexpected_error);
					}
					return context.getResources().getString(R.string.stock_lowered);
				}
				return null;
			}
		}

		@Override
		protected void onPostExecute(String message){
			dialog.hide();
			if(message != null){
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setMessage(message)
						.setCancelable(false)
						.setPositiveButton(context.getResources().getString(R.string.OK), null);
				AlertDialog alert = builder.create();
				alert.show();
			}
		}
	}
}
