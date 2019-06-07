package com.hr.techlabapp.Fragments;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.hr.techlabapp.Networking.Product;
import com.hr.techlabapp.Networking.User;
import com.hr.techlabapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatisticsFragment extends Fragment {

	private BarChart ProductChart;
	private BarChart UserChart;

	public StatisticsFragment() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_statistics, container, false);
	}

/*	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		// fills in the data
		//noinspection ConstantConditions
		ProductChart = getView().findViewById(R.id.product_chart);
		UserChart = getView().findViewById(R.id.user_chart);
		List<BarEntry> entries = new ArrayList<>();
		HashMap<String,HashMap<String,Integer>> ava = (HashMap<String, HashMap<String, Integer>>) getArguments().getSerializable("availabilty");
		int i = 0;
		for(Product p: (ArrayList<Product>)getArguments().getSerializable("products")){
			entries.add(new BarEntry(i,new float[] { ava.get(p.ID).get("inStock"), ava.get(p.ID).get("total") }));
			i++;
		}

		topbarStats = getView().findViewById(R.id.topbar_stats);
		topbarStats.setText(getResources().getString(R.string.hour_min,5,18));
	}*/

}
