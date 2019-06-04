package com.hr.techlabapp.Fragments;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hr.techlabapp.Networking.LoanItem;

import com.hr.techlabapp.R;
import com.savvi.rangedatepicker.CalendarPickerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateLoanFragment extends Fragment {


	public CreateLoanFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_create_loan2, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		Calendar pastYear = Calendar.getInstance();
		Calendar nextYear = Calendar.getInstance();
		nextYear.add(Calendar.YEAR,2);

		CalendarPickerView calendar = (CalendarPickerView) getView().findViewById(R.id.calendar_view);

		calendar.init(pastYear.getTime(), nextYear.getTime())
				.inMode(CalendarPickerView.SelectionMode.RANGE)
				.withSelectedDate(new Date());

		calendar.;
		calendar.setTypeface(Typeface.SANS_SERIF);

		Button btn = getView().findViewById(R.id.stertLen);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
	}

	class Yeet extends AsyncTask<Object, Void, Void> {
		@Override
		protected Void doInBackground(Object... strings) {
			return null;
		}
	}
}
