package com.hr.techlabapp.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hr.techlabapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatisticsFragment extends Fragment {

	private TextView thisWeek;
	private TextView topbarStats;
	
	public StatisticsFragment() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_statistics, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		// fills in the data
		thisWeek = getView().findViewById(R.id.this_week);
		thisWeek.setText(getResources().getString(R.string.hour_min,10,18));

		topbarStats = getView().findViewById(R.id.topbar_stats);
		topbarStats.setText(getResources().getString(R.string.hour_min,5,18));
	}
}
