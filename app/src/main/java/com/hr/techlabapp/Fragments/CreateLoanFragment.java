package com.hr.techlabapp.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hr.techlabapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateLoanFragment extends Fragment {


	public CreateLoanFragment() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_create_loan2, container, false);
	}

}
