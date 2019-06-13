package com.hr.techlabapp.Fragments;

import android.annotation.SuppressLint;
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
import android.widget.EditText;
import android.widget.ProgressBar;

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
	public static final long UPDATE_DELAY = 500;

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

		loanIdText.addTextChangedListener(new TextWatcher() {
			private final Runnable action = new Runnable() {
				@Override
				public void run() {
					Log.e("AcceptLoanFrag", "Running update action");
					new Update_Action().execute();
				}
			};

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
				if (s.length() == 0) return;
				updateHandler.postDelayed(action, UPDATE_DELAY);
			}
		});
	}

	@SuppressLint("StaticFieldLeak")
	private class Update_Action extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progress.setVisibility(View.VISIBLE);
			getView().requestLayout();
			getView().findViewById(R.id.fucking_retard).requestLayout();
			progress.requestLayout();
		}

		@Override
		protected Void doInBackground(Void... voids) {
			try {
				return backgroundTask();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		private Void backgroundTask() {
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
				// TODO Implement error feedback
				e.printStackTrace();
				return null;
			}

			// End early if no loan was found
			if (loan == null) {
				return null;
			}

			// Try to get the associated productItem
			ProductItem item = null;
			try {
				HashMap<String, List<ProductItem>> items = ProductItem.getProductItems(null, new Integer[] {loan.productItemID});
				if (!items.isEmpty()) {
					item = items.get(items.keySet().toArray()[0]).get(0);
				}
			} catch (JSONException | Exceptions.NetworkingException e) {
				// End early in case of error
				// TODO Implement error feedback
				e.printStackTrace();
				return null;
			}

			// End early if no productItem was found
			if (item == null) {
				return null;
			}

			// Try to get the associated product by searching through productListFragment's product list
			List<Product> products = ProductListFragment.getProducts();
			Product product = null;
			for (Product p : products) {
				if (p.ID.equals(item.productId)) {
					product = p;
					break;
				}
			}

			Log.e("Yeet", "" + product);
			// End early if no product was found
			if (product == null) {
				return null;
			}
			// Get product image if it hasn't loaded yet
			if (product.image == null) {
			    try {
			        product.getImage();
                } catch (JSONException | Exceptions.NetworkingException e) {
			        e.printStackTrace();
                }
            }
			final Product finalProduct = product;

			// Set stuff on the ui with the collected data
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					productItemView.title.setText(finalProduct.getName());
                    productItemView.icon.setImageBitmap(finalProduct.image);
				}
			});

			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			progress.setVisibility(View.INVISIBLE);
			getView().requestLayout();
			getView().findViewById(R.id.fucking_retard).requestLayout();
			progress.requestLayout();
		}
	}

	private class SetImage_Action extends AsyncTask<Void, Void, Void> {
	    // TODO implement this image setter task to replace the concurrent image loading

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }
}