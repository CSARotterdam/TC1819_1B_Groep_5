package com.hr.techlabapp.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hr.techlabapp.Classes.Product;
import com.hr.techlabapp.CustomViews.cGrid;
import com.hr.techlabapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductListFragment extends Fragment {

	private cGrid Products;

	private Button addProduct;
	private Button statistics;

	public ProductListFragment() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_product_list, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Products = getView().findViewById(R.id.products);

		// adds random products
		for(int i = 0; i  < 30; i++)
			Products.AddProduct(new Product("Arduino","des","man","ID","cat",8,5),false);
		Products.AddProduct(new Product("Arduino","des","man","ID","cat",8,5));

		// sets the itemClicked
		Products.setItemClicked(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
		addProduct = getView().findViewById(R.id.add_product);
		addProduct.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
		statistics = getView().findViewById(R.id.statistics);
		statistics.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
	}
}
