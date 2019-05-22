package com.hr.techlabapp.Fragments;


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

import com.hr.techlabapp.Classes.Product;
import com.hr.techlabapp.CustomViews.UserHistoryDialog;
import com.hr.techlabapp.R;

import java.time.LocalDate;
import java.util.Calendar;

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
		image = getView().findViewById(R.id.image);
		product = Product.GetProductByID(getArguments().getString(ProductInfoFragment.ProductArgumentKey));
		image.setImageBitmap(product.getImage());

		//sets the value
		username = getView().findViewById(R.id.username);
		username.setText(getResources().getString(R.string.username_id,"Gijs","Puelinckx",958956));
		requestDate = getView().findViewById(R.id.request_date);
		requestDate.setText(getResources().getString(R.string.date_of_request, Build.VERSION.SDK_INT >= 26 ? LocalDate.now(): Calendar.getInstance().getTime(), Build.VERSION.SDK_INT >= 26 ? LocalDate.now(): Calendar.getInstance().getTime(),Build.VERSION.SDK_INT >= 26 ? LocalDate.now(): Calendar.getInstance().getTime()));
		name = getView().findViewById(R.id.name);
		name.setText(product.getName());
		amount = getView().findViewById(R.id.amount);
		amount.setText(getResources().getString(R.string.amount_value,product.getProductsAvailable()));

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
