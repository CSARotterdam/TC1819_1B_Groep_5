package com.hr.techlabapp.CustomViews;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.hr.techlabapp.Fragments.ProductListFragment;
import com.hr.techlabapp.Networking.Exceptions;
import com.hr.techlabapp.Networking.LoanItem;
import com.hr.techlabapp.Networking.Product;
import com.hr.techlabapp.Networking.ProductItem;
import com.hr.techlabapp.R;

import org.json.JSONException;

import java.security.AccessController;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

public class ProductItemView extends CardView {
	private ImageView icon;
	private TextView title;

	private TextView itemId;
	private TextView loanId;
	private TextView startDate;
	private TextView endDate;

	public LoanItem loan;
	public ProductItem item;
	public Product product;

	public ProductItemView(@NonNull Context context) {
		super(context);
		init();
	}
	public ProductItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init();

		// Get custom attributes
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ProductItemView);

		int iconSize = a.getDimensionPixelSize(R.styleable.ProductItemView_iconSize, -1);
		if (iconSize != -1) {
			ViewGroup.LayoutParams iconParams = icon.getLayoutParams();
			iconParams.width = iconSize;
			iconParams.height = iconSize;
		}

		String titleText = a.getString(R.styleable.ProductItemView_title);
		title.setText(titleText != null ? titleText : "Product name");

		a.recycle();
	}
	public ProductItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	/**
	 * Custom initializer for ProductItemView
	 */
	private void init() {
		// Load the ProductItemView xml
		inflate(getContext(), R.layout.productitemview, this);

		// Get views
		this.icon = findViewById(R.id.icon);
		this.title = findViewById(R.id.itemName);

		this.itemId = findViewById(R.id.itemId);
		this.loanId = findViewById(R.id.loanId);
		this.startDate = findViewById(R.id.startDate);
		this.endDate = findViewById(R.id.endDate);
	}

	public void setLoanAsync(LoanItem loanItem) throws JSONException, Exceptions.NetworkingException {
		if (loanItem == null) {
			throw new NullPointerException();
		}

		this.loan = null;

		// Try to get the associated productItem
		item = null;
		HashMap<String, List<ProductItem>> items = ProductItem.getProductItems(null, new Integer[] {loanItem.productItemID});
		if (!items.isEmpty()) {
			item = items.get(items.keySet().toArray()[0]).get(0);
		}

		// End early if no productItem was found
		if (item == null) {
			reset();
			throw new NullPointerException();
		}

		setItemAsync(item, false);

		this.loan = loanItem;
//		 Set stuff on the ui with the collected data
		((Activity)getContext()).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				DateFormat format = SimpleDateFormat.getDateInstance();
				loanId.setText(Integer.valueOf(loan.ID).toString());
				startDate.setText(format.format(loan.start));
				endDate.setText(format.format(loan.end));
			}
		});
		setLoanComponentVisibility(View.VISIBLE);
	}

	public void setItemAsync(ProductItem item) throws JSONException, Exceptions.NetworkingException {
		setItemAsync(item, true);
	}

	private void setItemAsync(ProductItem productItem, boolean reset) throws JSONException, Exceptions.NetworkingException {
		if (productItem == null) {
			throw new NullPointerException();
		}

		if (reset) {
			setLoanComponentVisibility(View.GONE);
			loan = null;
		}

		// Try to get the associated product by searching through productListFragment's product list
		List<Product> products = ProductListFragment.getProducts();
		for (Product p : products) {
			if (p.ID.equals(productItem.productId)) {
//				if (product != null) {
//					product.image.recycle();
//				}
				product = p;
				break;
			}
		}

		// End early if no product was found
		if (product == null) {
			reset();
			throw new NullPointerException();
		}
		// Get product image if it hasn't loaded yet
		if (product.image == null || product.image.isRecycled()) {
			product.getImage();
		}

		// Set stuff on the ui with the collected data
		((Activity)getContext()).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				title.setText(product.getName());
				icon.setImageBitmap(product.image);
				itemId.setText(item.id.toString());
			}
		});
	}

	private void reset() {
		this.loan = null;
		this.item = null;
		this.product = null;
		((Activity)getContext()).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				icon.setImageDrawable(null);
				title.setText(null);
			}
		});
	}

	private void setLoanComponentVisibility(final int visibility) {
		((Activity)getContext()).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				findViewById(R.id.dateLayout).setVisibility(visibility);
				findViewById(R.id.loanText).setVisibility(visibility);
				loanId.setVisibility(visibility);
			}
		});
	}
}
