package com.hr.techlabapp.Fragments;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hr.techlabapp.Networking.Product;
import com.hr.techlabapp.R;

import java.util.HashMap;

import static com.hr.techlabapp.Fragments.ProductInfoFragment.*;

/**
 * A fragment for accepting loans and taking loaned items back in.
 */
public class AcceptLoanFragment extends Fragment {

	public AcceptLoanFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_accept_loan, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}
}