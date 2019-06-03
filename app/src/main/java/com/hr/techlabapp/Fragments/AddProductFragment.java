package com.hr.techlabapp.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.hr.techlabapp.Networking.Product;
import com.hr.techlabapp.Networking.ProductCategory;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.hr.techlabapp.Networking.Authentication;
import com.hr.techlabapp.Networking.Exceptions;
import com.hr.techlabapp.Networking.Product;
import com.hr.techlabapp.R;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddProductFragment extends Fragment {
	public Context context;

	private Spinner cat;

	Button addProduct;
	Button addImage;

	public AddProductFragment() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_add_product, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		cat = getView().findViewById(R.id.product_cat);
	}

	class getProductCategories extends AsyncTask<Void,Void, List<ProductCategory>>{
		@Override
		protected List<ProductCategory> doInBackground(Void... voids) {
			try{
				return ProductCategory.getProductCategories(new String[]{"en"});
			}
			catch (JSONException ex){
				return new ArrayList<ProductCategory>();
			}
		}
		@Override
		protected void onPostExecute(List<ProductCategory> productCategories) {
			context = getView().getContext();

			addProduct = getView().findViewById(R.id.add_product);
			addProduct.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					EditText productIDField = (EditText) getView().findViewById(R.id.product_id);
					String productID = productIDField.getText().toString();
					if (productID.length() == 0) {
						Toast toast = Toast.makeText(context, "Product ID is required!", Toast.LENGTH_SHORT);
						toast.show();
						return;
					}

					EditText productNameField = (EditText) getView().findViewById(R.id.product_name);
					String productName = productNameField.getText().toString();
					if (productName.length() == 0) {
						Toast toast = Toast.makeText(context, "Product name is required!", Toast.LENGTH_SHORT);
						toast.show();
						return;
					}

					EditText manufacturerField = (EditText) getView().findViewById(R.id.product_man);
					String manufacturer = manufacturerField.getText().toString();
					if (manufacturer.length() == 0) {
						manufacturer = "Unknown.";
					}

					Spinner categoryField = (Spinner) getView().findViewById(R.id.product_cat);
					String category = manufacturerField.getText().toString();
					if (category.length() == 0) {
						category = null;
					}

					EditText descriptionField = getView().findViewById(R.id.product_des);
					String description = descriptionField.getText().toString();
					if (description.length() == 0) {
						description = null;
					}

					HashMap<String, String> name = new HashMap<>();
					name.put("en", productName);
					HashMap<String, String> desc = new HashMap<>();
					desc.put("en", description);

					Product product = new Product(productID, manufacturer, category, name, desc);
					new AddProductActivity().execute(product);
				}
			});
		}
	}

	public class AddProductActivity extends AsyncTask<Product, Void, Integer>{
		private ProgressDialog dialog;

		protected void onPreExecute(){
			dialog = new ProgressDialog(getContext());
			dialog.setMessage("Working...");
			dialog.show();
		}
		protected Integer doInBackground(Product... params){
			try{
				Product.addProduct(params[0]);
			} catch (JSONException e){
				return -1;
			} catch (Exceptions.AlreadyExists e){
				return  1;
			} catch (Exceptions.NoSuchProductCategory e){
				return 2;
			}

			return 0;
		}
		protected void onPostExecute(Integer result){
			dialog.hide();
			String message;
			switch(result){
				case 0:
					message = "Product added successfully.";
				case 1:
					message = "A product already exists with this ID.";
				case 2:
					message = "The selected category does not exist.";
				default:
					message = "An unexpected error occurred.";
			}

			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setMessage(message)
				.setCancelable(false)
				.setPositiveButton("OK", null);
			AlertDialog alert = builder.create();
			alert.show();
		}
	}
}
