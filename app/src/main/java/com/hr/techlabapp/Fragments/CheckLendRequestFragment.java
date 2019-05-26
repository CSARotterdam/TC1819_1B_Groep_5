package com.hr.techlabapp.Fragments;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
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

import com.hr.techlabapp.CustomViews.UserHistoryDialog;
import com.hr.techlabapp.Networking.Product;
import com.hr.techlabapp.Networking.Statistics;
import com.hr.techlabapp.R;

import org.json.JSONException;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;

import static com.hr.techlabapp.Fragments.ProductInfoFragment.PRODUCT_CATEGORY_KEY;
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
				(Bitmap) getArguments().getParcelable(PRODUCT_IMAGE_KEY),
				getArguments().getString(PRODUCT_IMAGE_ID_KEY));

		image = getView().findViewById(R.id.image);
		image.setImageBitmap(product.image);
		//sets the value
		username = getView().findViewById(R.id.username);
		username.setText(getResources().getString(R.string.username_id,"Gijs","Puelinckx",95895));
		requestDate = getView().findViewById(R.id.request_date);
		Object Date = Build.VERSION.SDK_INT >= 26 ? LocalDate.now(): Calendar.getInstance().getTime();
		requestDate.setText(getResources().getString(R.string.date_of_request,Date,Date,Date));
		name = getView().findViewById(R.id.name);
		name.setText(product.getName());
		amount = getView().findViewById(R.id.amount);
		// TODO: get availability from api
		try {
			amount.setText(getResources().getString(R.string.amount_value,
					Statistics.getProductAvailability(product.ID).get(product.ID).get("inStock")));
		}
		catch (JSONException ex){
			amount.setText(getResources().getString(R.string.amount_value,0));
		}
		//sets the on click the event
		userHistory = getView().findViewById(R.id.user_history);
		userHistory.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//the dialog to show the user history
				UserHistoryDialog dialog = new UserHistoryDialog();
				//makes the arguments needed for the dialog
				Bundle args = new Bundle();
				args.putInt("ID",958956);
				//sets the arguments
				dialog.setArguments(args);
				//shows the dialog
				dialog.show(getFragmentManager(),String.format("user history %07d",958956));
			}
		});
	}
}