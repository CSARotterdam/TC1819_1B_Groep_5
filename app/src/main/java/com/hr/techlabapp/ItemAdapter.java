package com.hr.techlabapp;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hr.techlabapp.CustomViews.ProductItemView;
import com.hr.techlabapp.CustomViews.cGrid;
import com.hr.techlabapp.Networking.LoanItem;
import com.hr.techlabapp.Networking.Product;
import com.hr.techlabapp.Networking.ProductItem;

import org.json.JSONException;

import java.util.HashMap;
import java.util.concurrent.ThreadPoolExecutor;

import static com.hr.techlabapp.Fragments.ProductInfoFragment.PRODUCT_IMAGE_KEY;
import static com.hr.techlabapp.Fragments.ProductInfoFragment.PRODUCT_NAME_KEY;


public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

	private LoanItem[] dataSet;


	@Override
	public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).
				inflate(R.layout.my_item_recycler_item_layout,parent,false);
		return new ItemViewHolder(v);
	}

	@Override
	public void onBindViewHolder(@NonNull ItemViewHolder holder, final int position) {
		new SetLoanItem().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,new Pair<>(holder,position));
	}

	class SetLoanItem extends AsyncTask<Pair<ItemViewHolder, Integer>, Void, Void>{
		@Override
		protected Void doInBackground(Pair<ItemViewHolder, Integer>... pairs) {
			ProductItemView productItemView = pairs[0].first.getProductItemView();
			try{
				productItemView.setLoanAsync(dataSet[pairs[0].second]);
			}
			catch (JSONException ex){}
			return null;
		}
	}

	@Override
	public int getItemCount() {
		return dataSet.length;
	}

	class ItemViewHolder extends RecyclerView.ViewHolder {
		final private ProductItemView productItemView;

		ItemViewHolder(View itemView) {
			super(itemView);
			productItemView = itemView.findViewById(R.id.product_item_view);
		}

		public ProductItemView getProductItemView() {
			return productItemView;
		}
	}

	public ItemAdapter(LoanItem[] dataSet){
		this.dataSet = dataSet;
	}
}