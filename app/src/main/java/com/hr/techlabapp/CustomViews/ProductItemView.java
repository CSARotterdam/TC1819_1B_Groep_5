package com.hr.techlabapp.CustomViews;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.hr.techlabapp.R;

public class ProductItemView extends CardView {
	public ProductItemView(@NonNull Context context) {
		super(context);
		init();
	}
	public ProductItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init();
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
	}
}
