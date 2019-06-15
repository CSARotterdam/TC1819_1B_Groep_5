package com.hr.techlabapp.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hr.techlabapp.Activities.NavHostActivity;
import com.hr.techlabapp.CustomViews.ProductItemView;
import com.hr.techlabapp.Networking.Exceptions;
import com.hr.techlabapp.Networking.LoanItem;
import com.hr.techlabapp.Networking.Product;
import com.hr.techlabapp.Networking.ProductItem;
import com.hr.techlabapp.R;

import org.json.JSONException;

import java.util.HashMap;
import java.util.List;

/**
 * A fragment for accepting loans and taking loaned items back in.
 */
public class AcceptLoanFragment extends Fragment {
	public static final long UPDATE_DELAY = 1000;

	private ProgressBar progress;

	private ProductItemView productItemView;
	private EditText loanIdText;

	public AcceptLoanFragment() {
		// Required empty public constructor
	}

	@Override
	public void onAttach(@NonNull Context context) {
		// This allows the back button to work
		((NavHostActivity)context).currentFragment = this;
		super.onAttach(context);
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

		this.productItemView = getView().findViewById(R.id.productItemCard);
		this.loanIdText = getView().findViewById(R.id.loan_id_text);
		this.progress = getView().findViewById(R.id.loading_bar);

		Button acceptBtn = getView().findViewById(R.id.btn_accept);
		Button declineBtn = getView().findViewById(R.id.btn_decline);

		acceptBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new AcceptDecline_Action().execute(true, !productItemView.loan.isAcquired);
			}
		});
		declineBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new AcceptDecline_Action().execute(false);
			}
		});

		loanIdText.addTextChangedListener(new TextWatcher() {
			private final Runnable action = new Runnable() {
				@Override
				public void run() {
					Log.i("AcceptLoanFrag", "Running update action");
					if (clear) {
						setCardVisibility(View.INVISIBLE);
						return;
					}
					if (productItemView.loan != null && Integer.parseInt(loanIdText.getText().toString()) == productItemView.loan.ID)
						return;
					new Update_Action().execute();
				}
			};
			private boolean clear = false;

			private Handler updateHandler = new Handler();

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				updateHandler.removeCallbacks(action);
			}

			@Override
			public void afterTextChanged(Editable s) {
				clear = s.length() == 0;
				updateHandler.postDelayed(action, UPDATE_DELAY);
			}
		});
	}


	private void showDialog(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setMessage(message)
				.setCancelable(false)
				.setPositiveButton(getResources().getString(R.string.OK), null);
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void toast(String message) {
		Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
	}

	@SuppressLint("StaticFieldLeak")
	private class Update_Action extends AsyncTask<Void, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progress.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(Void... voids) {
			// Get the loan
			LoanItem loan;
			try {
				List<LoanItem> results = LoanItem.getLoans(
						null,
						null,
						Integer.parseInt(loanIdText.getText().toString()),
						null,
						null
					);
				loan = results.size() != 0 ? results.get(0) : null;
			} catch (JSONException | Exceptions.NetworkingException e) {
				// End early in case of error
				e.printStackTrace();
				return getResources().getString(R.string.unexpected_error);
			}

			// End early if no loan was found
			if (loan == null) {
				return getResources().getString(R.string.no_such_loan_id);
			}

			try {
				productItemView.setLoanAsync(loan);
			} catch (JSONException | Exceptions.NetworkingException e) {
				return getResources().getString(R.string.unexpected_error);
			}

			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Button acceptBtn = getView().findViewById(R.id.btn_accept);
					acceptBtn.setText(productItemView.loan.isAcquired ? R.string.takeback : R.string.accept);
				}
			});

			setCardVisibility(View.VISIBLE);

			return null;
		}

		@Override
		protected void onPostExecute(String message) {
			super.onPostExecute(message);

			if (message != null) toast(message);
			progress.setVisibility(View.INVISIBLE);
		}
	}

	private void setCardVisibility(final int visibility) {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				productItemView.setVisibility(visibility);
			}
		});
	}

	private class AcceptDecline_Action extends AsyncTask<Boolean, Void, String> {
		private boolean useToast = false;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progress.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(Boolean... booleans) {
			if (productItemView.loan == null ||
				booleans.length < 1) return null;

			// If set to decline, delete the thing
			if (!booleans[0]) {
				try {
					productItemView.loan.delete();
					setCardVisibility(View.INVISIBLE);
				} catch (JSONException | Exceptions.NetworkingException e) {
					if (e instanceof Exceptions.LoanAlreadyStarted)
						return getResources().getString(R.string.loan_already_started);
					return getResources().getString(R.string.unexpected_error);
				}
				return getResources().getString(R.string.loan_decline_success);
			}

			try {
				productItemView.loan.setAcquired(booleans[1]);
				new Update_Action().execute();
				if (!booleans[1])
					return getResources().getString(R.string.loan_takeback_success);
				return getResources().getString(R.string.loan_accept_success,
						productItemView.loan.userId,
						productItemView.loan.productItemID);
			} catch (JSONException | Exceptions.NetworkingException e) {
				e.printStackTrace();
				useToast = true;
				return getResources().getString(R.string.unexpected_error);
			}
		}

		@Override
		protected void onPostExecute(String message) {
			super.onPostExecute(message);

			if (message != null) {
				if (useToast) toast(message);
				else showDialog(message);
			}
			progress.setVisibility(View.INVISIBLE);
		}
	}
}