package com.hr.techlabapp.Activitys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.hr.techlabapp.R;

public class Statistics extends AppCompatActivity {

	private TextView thisWeek;
	private TextView topbarStats;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statistics);
		// fills in the data
		thisWeek = findViewById(R.id.this_week);
		thisWeek.setText(getResources().getString(R.string.hour_min,10,18));

		topbarStats = findViewById(R.id.topbar_stats);
		topbarStats.setText(getResources().getString(R.string.hour_min,5,18));

	}
}
