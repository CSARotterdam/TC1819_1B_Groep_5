package com.hr.techlabapp.Fragments;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.navigation.Navigation;

import com.hr.techlabapp.CustomViews.DeleteItemDialog;
import com.hr.techlabapp.Networking.Product;
import com.hr.techlabapp.Networking.Statistics;
import com.hr.techlabapp.R;

import org.json.JSONException;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductInfoFragment extends Fragment {
	public static final String PRODUCT_NAME_KEY = "ProductName";
	public static final String PRODUCT_IMAGE_KEY = "ProductImage";
	public static final String PRODUCT_IMAGE_ID_KEY = "ProductImageID";
	public static final String PRODUCT_ID_KEY = "ProductId";
	public static final String PRODUCT_MANUFACTURER_KEY = "ProductManufacturer";
	public static final String PRODUCT_CATEGORY_KEY = "ProductCategory";
	public static final String PRODUCT_AVAILABILITY_KEY = "ProductAvailability";
	private Product product;
	
	private ImageView image;

	private TextView name;
	private TextView id;
	private TextView man;
	private TextView cat;
	private TextView stock;

	private Button borrow;
	private Button delete;

	public ProductInfoFragment() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_product_info, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		product = new Product(
				getArguments().getString(PRODUCT_ID_KEY), 
				getArguments().getString(PRODUCT_MANUFACTURER_KEY),
				getArguments().getString(PRODUCT_CATEGORY_KEY),
				(HashMap<String, String>) getArguments().getSerializable(PRODUCT_NAME_KEY),
				(Bitmap) getArguments().getParcelable(PRODUCT_IMAGE_KEY),
				getArguments().getString(PRODUCT_IMAGE_ID_KEY));
		image = getView().findViewById(R.id.image);
		if(product.image != null)
			image.setImageBitmap(product.image);
		else
			new loadImage().execute();
		// sets the string values
		name = getView().findViewById(R.id.name);
		name.setText(getResources().getString(R.string.product_name_value,product.getName("en")));
		id = getView().findViewById(R.id.product_id);
		id.setText(getResources().getString(R.string.product_id_value, product.ID));
		// TODO: add this
		man = getView().findViewById(R.id.product_man);
		man.setText(getResources().getString(R.string.product_man_value, product.manufacturer));
		cat = getView().findViewById(R.id.category);
		cat.setText(getResources().getString(R.string.product_cat_value, product.categoryID));
		stock = getView().findViewById(R.id.availability);
		stock.setText(getResources().getString(R.string.available_value,
				((HashMap<String,Integer>) getArguments().getSerializable(PRODUCT_AVAILABILITY_KEY)).get("inStock")));
		// sets the onClickListener
		borrow = getView().findViewById(R.id.borrow);
		borrow.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_productInfoFragment_to_createLoanFragment,getArguments()));

		// sets the onClickListener
		delete = getView().findViewById(R.id.delete);
		delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// makes a new delete dialog
				DeleteItemDialog dialog = new DeleteItemDialog();
				// makes the args
				Bundle args = new Bundle();
				args.putCharSequence("ID",product.ID);
				// sets the args
				dialog.setArguments(args);
				// shows the dialog
				dialog.show(getFragmentManager(),String.format("delete item %s",product.ID));
			}
		});
	}	

	class loadImage extends AsyncTask <Void,Void,Bitmap>{

		@Override
		protected Bitmap doInBackground(Void... voids) {
			try{
					return product.getImage();
			}
			catch (JSONException ex){
				return BitmapFactory.decodeResource(getResources(),R.drawable.cuteaf);
			}
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			image.setImageBitmap(bitmap);
		}
	}
}
