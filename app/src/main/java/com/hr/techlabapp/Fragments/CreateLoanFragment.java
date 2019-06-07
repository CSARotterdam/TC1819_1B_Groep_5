package com.hr.techlabapp.Fragments;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hr.techlabapp.Networking.Exceptions;
import com.hr.techlabapp.Networking.LoanItem;
import com.hr.techlabapp.Networking.Product;
import com.hr.techlabapp.R;
import com.savvi.rangedatepicker.CalendarPickerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateLoanFragment extends Fragment {
	static Product product;

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
		product = new Product(
				getArguments().getString("ProductId"),
				getArguments().getString("ProductManufacturer"),
				null,
				(HashMap<String, String>) getArguments().getSerializable("ProductName"),
				null,
				null,
				(Bitmap) getArguments().getParcelable("ProductImage"));

		// Setup title text
		TextView title = getView().findViewById(R.id.FrameTitle);
		title.setText(product.getName());

		Calendar pastYear = Calendar.getInstance();
		Calendar nextYear = Calendar.getInstance();
		nextYear.add(Calendar.YEAR,2);

		final CalendarPickerView calendar = getView().findViewById(R.id.calendar_view);

		calendar.init(pastYear.getTime(), nextYear.getTime())
				.inMode(CalendarPickerView.SelectionMode.RANGE)
				.withSelectedDate(new Date());

		calendar.setTypeface(Typeface.SANS_SERIF);

		Button btn = getView().findViewById(R.id.createLoanBtn);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (calendar.getSelectedDates().size() == 0){
					// TODO add popup message saying "please select a date on the calendar"
					Log.i("CreateLoanFragment", "Nothing is selected. Skipping action...");
					return;
				}
				AddLoan_Action action = new AddLoan_Action();
				action.execute(calendar.getSelectedDates());
			}
		});
	}

	class AddLoan_Action extends AsyncTask<Object, Void, Void> {
		static final String TAG = "AddLoan_Action";

		@Override
		protected Void doInBackground(Object... params) {
			if (params.length == 0) return null;
			Log.i(TAG, "Running networking task 'AddLoan'...");

			// Prepare fields
			List<Date> dates;

			// Prepare values
			try {
				dates = (List<Date>) params[0];
			} catch(Exception e){
				// TODO Add popup error handler
				Log.e(TAG, "Failed to prepare values: " + e.getMessage());
				return null;
			}

			// Get min and max dates
			Date minDate = null;
			Date maxDate = null;
			for (Date date : dates) {
				if (minDate == null || minDate.after(date))
					minDate = date;
				if (maxDate == null || maxDate.before(date))
					maxDate = date;
			}

			try {
				LoanItem.addLoan(product, minDate, maxDate);
			} catch (Exceptions.NetworkingException e) {
				Log.e(TAG, "Error while adding loan: " + e.getClass().getSimpleName() + ": " + e.getMessage());
			} catch (JSONException e) {
				Log.e(TAG, "Unexpected error while adding loan: " + e.getClass().getSimpleName() + ": " + e.getMessage());
			}
			return null;
		}
	}
}
