package com.hr.techlabapp.Fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hr.techlabapp.Activities.NavHostActivity;
import com.hr.techlabapp.AppConfig;
import com.hr.techlabapp.ItemAdapter;
import com.hr.techlabapp.Networking.LoanItem;
import com.hr.techlabapp.Networking.ProductItem;
import com.hr.techlabapp.R;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.hr.techlabapp.AppConfig.currentUser;

public class MyItemsFragment extends Fragment {

	private RecyclerView recyclerView;

	public MyItemsFragment() {
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
		return inflater.inflate(R.layout.fragment_my_items, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		recyclerView = getView().findViewById(R.id.recycler_view);
		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		new GetLoanItems().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

	}

	class GetLoanItems extends AsyncTask<Void,Void, ProductItem[]>{

		@Override
		protected ProductItem[] doInBackground(Void... voids) {
			try{
				List<LoanItem> loanItems = LoanItem.getLoans(null, currentUser.username, null, null, null);
				ArrayList<Integer> productItemIdList = new ArrayList<>();
				for(LoanItem loanItem: loanItems)
					productItemIdList.add(loanItem.productItemID);
				Integer[] productItemIds = new Integer[productItemIdList.size()];
				productItemIdList.toArray(productItemIds);
				HashMap<String, List<ProductItem>> HMSLPI = ProductItem.getProductItems(null, productItemIds);
				ArrayList<ProductItem> res = new ArrayList<>();
				for(List<ProductItem> productItems: HMSLPI.values())
					res.addAll(productItems);
				ProductItem[] resArray = new ProductItem[res.size()];
				res.toArray(resArray);
				return resArray;
			}
			catch (JSONException ex){
				return null;
			}
		}

		@Override
		protected void onPostExecute(ProductItem[] productItems) {
			recyclerView.setAdapter(new ItemAdapter(productItems));
		}
	}
}
