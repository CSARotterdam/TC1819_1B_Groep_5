package com.hr.techlabapp.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hr.techlabapp.Activities.NavHostActivity;
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
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateLoanFragment extends Fragment {
	private static Product product;
	CalendarPickerView calendar;
	Calendar start;
	Calendar end;

	public CreateLoanFragment() {
		// Required empty public constructor
	}


	@Override
	public void onAttach(@NonNull Context context) {
		((NavHostActivity)context).currentFragment = this;
		super.onAttach(context);
	}

	@Override
	public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_create_loan, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		assert getArguments() != null;
		product = new Product(
				getArguments().getString("ProductId"),
				getArguments().getString("ProductManufacturer"),
				null,
				(HashMap<String, String>) getArguments().getSerializable("ProductName"),
				null,
				null,
				(Bitmap) getArguments().getParcelable("ProductImage"));

		// Setup title text
		TextView title = Objects.requireNonNull(getView()).findViewById(R.id.FrameTitle);
		title.setText(product.getName());

		start = Calendar.getInstance();
		end = Calendar.getInstance();
		end.add(Calendar.MONTH,4);

		this.calendar = getView().findViewById(R.id.calendar_view);

		calendar.init(start.getTime(), end.getTime())
				.inMode(CalendarPickerView.SelectionMode.RANGE)
				.withSelectedDate(new Date());
		calendar.setTypeface(Typeface.SANS_SERIF);

		Button btn = getView().findViewById(R.id.createLoanBtn);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (calendar.getSelectedDates().size() == 0){
					Log.i("CreateLoanFragment", "Nothing is selected. Skipping action...");
					return;
				}
				new AddLoan_Action().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, calendar.getSelectedDates());
			}
		});

		refreshCalendar();
	}

	private void refreshCalendar() {
		new GetUnavailableDates_Action().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	@SuppressLint("StaticFieldLeak")
	class GetUnavailableDates_Action extends AsyncTask<Void, Void, Void> {
		static final String TAG = "GetUnavailableDates";
		private ProgressDialog dialog;

		protected void onPreExecute(){
			dialog = new ProgressDialog(getContext());
			dialog.setMessage(getResources().getString(R.string.loading));
			dialog.show();
		}

		@Override
		protected Void doInBackground(Void... voids) {
			Log.i(TAG, "Getting all unavailable dates for " + product.getName() + "...");
			try {
				final List<Date> unavailableDates = LoanItem.getUnavailableDates(
						product.ID,
						new Date(start.getTimeInMillis()),
						new Date(end.getTimeInMillis()));
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						calendar.clearHighlightedDates();
						calendar.highlightDates(unavailableDates);
						dialog.hide();
					}
				});
			} catch (JSONException e){
				Log.e(TAG, "JSONException during getUnavailableDates: " + e.getMessage());
			}
			return null;
		}
	}

	@SuppressLint("StaticFieldLeak")
	class AddLoan_Action extends AsyncTask<Object, Void, String> {
		static final String TAG = "AddLoan_Action";
		private ProgressDialog dialog;
		private boolean reloadCalendar = false;

		protected void onPreExecute(){
			dialog = new ProgressDialog(getContext());
			dialog.setMessage(getResources().getString(R.string.loading));
			dialog.show();
		}

		@SuppressLint({"StringFormatInvalid", "StringFormatMatches"})
		@Override
		protected String doInBackground(Object... params) {
			if (params.length == 0) return null;
			Log.i(TAG, "Running task...");

			// Prepare values
			List<Date> dates;
			try {
				dates = (List<Date>) params[0];
			} catch(Exception e){
				Log.e(TAG, "Failed to prepare values: " + e.getMessage());
				return getResources().getString(R.string.unexpected_error);
			}

			// Get min and max dates
			Date minDate = null;
			Date maxDate = null;
			for (Date date : dates) {
				if (minDate == null || minDate.after(date))
					minDate = new Date(date.getTime());
				if (maxDate == null || maxDate.before(date))
					maxDate = new Date(date.getTime());
			}
			// Add one day in milliseconds to create a range of exactly one day.
			maxDate.setTime(maxDate.getTime() + 86400000);

			LoanItem item;
			try {
				item = LoanItem.addLoan(product, minDate, maxDate);
			} catch (Exceptions.NoItemsForProduct e) {
				return getResources().getString(R.string.no_items_for_product);
			}catch (Exceptions.InvalidArguments e){
				return getResources().getString(R.string.max_loan_duration_exceeded);
			} catch (Exceptions.NetworkingException e) {
				Log.e(TAG, "Error while adding loan: " + e.getClass().getSimpleName() + ": " + e.getMessage());
				return getResources().getString(R.string.unexpected_error);
			} catch (JSONException e) {
				Log.e(TAG, "Unexpected error while adding loan: " + e.getClass().getSimpleName() + ": " + e.getMessage());
				return getResources().getString(R.string.unexpected_error);
			}
			reloadCalendar = true;
			return String.format(getResources().getString(R.string.loan_added_successfully), item.ID);
		}

		protected void onPostExecute(String message){
			dialog.hide();
			if (message == null) return;
			AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
			builder.setMessage(message)
					.setCancelable(false)
					.setPositiveButton(getResources().getString(R.string.OK), null);
			AlertDialog alert = builder.create();
			alert.show();
			if (reloadCalendar) {
				refreshCalendar();
				calendar.clearSelectedDates();
			}
		}
	}
}
