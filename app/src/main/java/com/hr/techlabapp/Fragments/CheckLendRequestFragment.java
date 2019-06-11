package com.hr.techlabapp.Fragments;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.hr.techlabapp.Activities.NavHostActivity;
import com.hr.techlabapp.CustomViews.UserHistoryDialog;
import com.hr.techlabapp.Networking.Product;
import com.hr.techlabapp.Networking.Statistics;
import com.hr.techlabapp.R;

import org.json.JSONException;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;

import static com.hr.techlabapp.Fragments.ProductInfoFragment.PRODUCT_CATEGORY_KEY;
import static com.hr.techlabapp.Fragments.ProductInfoFragment.PRODUCT_DESCRIPTION_KEY;
import static com.hr.techlabapp.Fragments.ProductInfoFragment.PRODUCT_ID_KEY;
import static com.hr.techlabapp.Fragments.ProductInfoFragment.PRODUCT_IMAGE_KEY;
import static com.hr.techlabapp.Fragments.ProductInfoFragment.PRODUCT_IMAGE_ID_KEY;
import static com.hr.techlabapp.Fragments.ProductInfoFragment.PRODUCT_MANUFACTURER_KEY;
import static com.hr.techlabapp.Fragments.ProductInfoFragment.PRODUCT_NAME_KEY;

/**
 * A simple {@link Fragment} subclass.
 */
public class CheckLendRequestFragment extends Fragment {
	private Product product;

	private ImageView image;

	private TextView username;
	private TextView requestDate;
	private TextView name;
	private TextView amount;

	private Button userHistory;
	
	
	public CheckLendRequestFragment() {
		// Required empty public constructor
	}


	@Override
	public void onAttach(@NonNull Context context) {
		((NavHostActivity)context).currentFragment = this;
		super.onAttach(context);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_check_lend_request, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		// TODO Fix
		product = new Product(
				getArguments().getString(PRODUCT_ID_KEY),
				getArguments().getString(PRODUCT_MANUFACTURER_KEY),
				getArguments().getString(PRODUCT_CATEGORY_KEY),
				(HashMap<String, String>) getArguments().getSerializable(PRODUCT_NAME_KEY),
				(HashMap<String, String>) getArguments().getSerializable(PRODUCT_DESCRIPTION_KEY),
				getArguments().getString(PRODUCT_IMAGE_ID_KEY),
				(Bitmap) getArguments().getParcelable(PRODUCT_IMAGE_KEY));

		image = getView().findViewById(R.id.image);
		image.setImageBitmap(product.image);
		//sets the value
		username = getView().findViewById(R.id.username);
		username.setText(getResources().getString(R.string.username_id,"Gijs","Puelinckx",95895)); //TODO what
		requestDate = getView().findViewById(R.id.request_date);
		Object Date = Build.VERSION.SDK_INT >= 26 ? LocalDate.now(): Calendar.getInstance().getTime();
		requestDate.setText(getResources().getString(R.string.date_of_request,Date,Date,Date));
		name = getView().findViewById(R.id.name);
		name.setText(product.getName());
		amount = getView().findViewById(R.id.stockAmount);
		// TODO: get availability from api
		try {
			amount.setText(getResources().getString(R.string.amount_value,
					Statistics.getProductAvailability(product.ID).get(product.ID).get("inStock")));
		}
		catch (JSONException ex){
			amount.setText(getResources().getString(R.string.amount_value,0));
		}
	}
}