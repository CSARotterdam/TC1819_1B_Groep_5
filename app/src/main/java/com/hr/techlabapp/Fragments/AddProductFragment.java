package com.hr.techlabapp.Fragments;


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
import com.hr.techlabapp.R;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddProductFragment extends Fragment {

	private Spinner cat;

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
		}
	}
}
