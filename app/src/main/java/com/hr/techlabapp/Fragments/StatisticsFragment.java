package com.hr.techlabapp.Fragments;


import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.hr.techlabapp.Activities.NavHostActivity;
import com.hr.techlabapp.CustomViews.GridItem;
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

	public StatisticsFragment() {
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
		return inflater.inflate(R.layout.fragment_statistics, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		// fills in the data
		//noinspection ConstantConditions
		ProductChart = getView().findViewById(R.id.product_chart);
		List<BarEntry> entries = new ArrayList<>();
		HashMap<String,HashMap<String,Integer>> ava = (HashMap<String,HashMap<String,Integer>>) getArguments().getSerializable("availability");
		int i = 0;
		for(Product p: (ArrayList<Product>)getArguments().getSerializable("products")){
			entries.add(new BarEntry(i,new float[] { ava.get(p.ID).get("inStock"), ava.get(p.ID).get("total") }));
			i++;
		}
		BarData Data = new BarData(new BarDataSet(entries,"products"));
		Data.setBarWidth(0.9f);
		ProductChart.setData(Data);
		ProductChart.setDrawValueAboveBar(true);
		ProductChart.setMaxVisibleValueCount(20);
		ProductChart.setDrawGridBackground(false);

		ValueFormatter xAxisFormatter = new IndexAxisValueFormatter();

		XAxis xAxis = ProductChart.getXAxis();
		xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
		xAxis.mAxisMinimum = 0;
		xAxis.setValueFormatter(xAxisFormatter);
	}
}
