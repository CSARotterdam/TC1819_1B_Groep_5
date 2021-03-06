package com.hr.techlabapp.Fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;

import androidx.navigation.Navigation;

import com.hr.techlabapp.Activities.NavHostActivity;
import com.hr.techlabapp.AppConfig;
import com.hr.techlabapp.CustomViews.EditItemStockDialog;
import com.hr.techlabapp.Networking.Product;
import com.hr.techlabapp.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.util.HashMap;

import static com.hr.techlabapp.AppConfig.currentUser;
import static com.hr.techlabapp.Networking.User.ADMIN;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductInfoFragment extends Fragment {
	public static final String PRODUCT_NAME_KEY = "ProductName";
	public static final String PRODUCT_DESCRIPTION_KEY = "ProductDescription";
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
	private TextView des;

	private Button borrow;
	private Button delete;
	private Button edit;

	public ProductInfoFragment() {
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
				(HashMap<String, String>) getArguments().getSerializable(PRODUCT_DESCRIPTION_KEY),
				getArguments().getString(PRODUCT_IMAGE_ID_KEY),
				(Bitmap) getArguments().getParcelable(PRODUCT_IMAGE_KEY));
		image = getView().findViewById(R.id.image);
		if(product.image != null)
			image.setImageBitmap(product.image);
		else
			new loadImage().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		// sets the string values
		name = getView().findViewById(R.id.name_val);
		name.setText(product.getName(AppConfig.getLanguage()));
		id = getView().findViewById(R.id.product_id_val);
		id.setText(product.ID);
		// TODO: add this
		man = getView().findViewById(R.id.product_man_val);
		man.setText(product.manufacturer);
		cat = getView().findViewById(R.id.category_val);
		cat.setText(product.categoryID);
		stock = getView().findViewById(R.id.availability_val);
		stock.setText(((HashMap<String,Integer>) getArguments().getSerializable(PRODUCT_AVAILABILITY_KEY)).get("available").toString());
		des = getView().findViewById(R.id.description_val);
		des.setText(product.getDescription());
		// sets the onClickListener
		borrow = getView().findViewById(R.id.borrow);
		borrow.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_productInfoFragment_to_createLoanFragment,getArguments()));
		edit = getView().findViewById(R.id.edit_product);
		edit.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_productInfoFragment_to_editProductFragment,getArguments()));
		// sets the onClickListener
		delete = getView().findViewById(R.id.delete);
		delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// makes a new delete dialog
				EditItemStockDialog dialog = new EditItemStockDialog();
				dialog.context = getContext();
				// sets the args
				dialog.setArguments(getArguments());
				// shows the dialog
				assert getFragmentManager() != null;
				dialog.show(getFragmentManager(),String.format(getResources().getString(R.string.edit_stock_for),product.getName()));
			}
		});
		if(currentUser.permissionLevel != ADMIN) {
			delete.setVisibility(View.GONE);
			edit.setVisibility(View.GONE);
			Space space = getView().findViewById(R.id.middlespacer);
			space.setVisibility(View.GONE);
			space = getView().findViewById(R.id.space2);
			space.setVisibility(View.GONE);
		}
	}

	@SuppressLint("StaticFieldLeak")
	class loadImage extends AsyncTask <Void,Void,Bitmap>{

		@Override
		protected Bitmap doInBackground(Void... voids) {
			try{
				return product.getImage();
			}
			catch (JSONException ex){
				return null;
			}
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			image.setImageBitmap(bitmap);
		}
	}
}
