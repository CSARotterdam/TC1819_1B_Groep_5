package com.hr.techlabapp.Fragments;


import android.os.Binder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.hr.techlabapp.Classes.Product;
import com.hr.techlabapp.CustomViews.GridItem;
import com.hr.techlabapp.CustomViews.ListItem;
import com.hr.techlabapp.CustomViews.cGrid;
import com.hr.techlabapp.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
// gets rid of the useless/usefull warnings
@SuppressWarnings("all")
public class ProductListFragment extends Fragment {

	private cGrid Products;

	private Button addProduct;
	private Button statistics;

	private ScrollView scrollView;

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
			Products.AddProduct(new Product("Arduino","des","man","ID","cat",8,5));

		scrollView = getView().findViewById(R.id.scrollView);
		scrollView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(Products.isList())
					for(ConstraintLayout Cl: Products.getItems())
						((ListItem)Cl).Scrolled();
				else
					for(ConstraintLayout Cl: Products.getItems())
						((GridItem)Cl).Scrolled();
				return false;
			}
		});
		// sets the itemClicked
		Products.setItemClicked(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Product p = v instanceof GridItem? ((GridItem) v).getProduct(): ((ListItem)v).getProduct();
				Bundle b = new Bundle();
				b.putString(ProductInfoFragment.ProductArgumentKey,p.getProductID());
				Navigation.findNavController(getView()).navigate(R.id.action_productListFragment_to_productInfoFragment,b);
			}
		});

		addProduct = getView().findViewById(R.id.add_product);
		addProduct.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_productListFragment_to_addProductFragment));

		statistics = getView().findViewById(R.id.statistics);
		statistics.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_productListFragment_to_statisticsFragment));
	}
}
