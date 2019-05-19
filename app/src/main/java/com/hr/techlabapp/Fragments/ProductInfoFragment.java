package com.hr.techlabapp.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.navigation.Navigation;

import com.hr.techlabapp.Activitys.NavHostActivity;
import com.hr.techlabapp.CustomViews.DeleteItemDialog;
import com.hr.techlabapp.Classes.Product;
import com.hr.techlabapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductInfoFragment extends Fragment {
	public static final String ProductArgumentKey = "ProductNameKey";

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
		product = Product.GetProductByID(getArguments().getString(ProductArgumentKey));

		image = getView().findViewById(R.id.image);
		image.setImageBitmap(product.getImage());

		// sets the string values
		name = getView().findViewById(R.id.product_name);
		name.setText(product.getName());
		id = getView().findViewById(R.id.product_id);
		id.setText(product.getProductID());
		man = getView().findViewById(R.id.product_man);
		man.setText(product.getManufacturer());
		cat = getView().findViewById(R.id.product_cat);
		cat.setText(product.getProductCategory());
		stock = getView().findViewById(R.id.product_stock);
		stock.setText(String.valueOf(product.getProductsAvailable()));

		// sets the onClickListener
		borrow = getView().findViewById(R.id.borrow);
		borrow.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_productInfoFragment_to_checkLendRequestFragment,getArguments()));

		// sets the onClickListener
		delete = getView().findViewById(R.id.delete);
		delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// makes a new delete dialog
				DeleteItemDialog dialog = new DeleteItemDialog();
				// makes the args
				Bundle args = new Bundle();
				args.putCharSequence("ID",product.getProductID());
				// sets the args
				dialog.setArguments(args);
				// shows the dialog
				dialog.show(getFragmentManager(),String.format("delete item %s",product.getProductID()));
			}
		});
	}
}
