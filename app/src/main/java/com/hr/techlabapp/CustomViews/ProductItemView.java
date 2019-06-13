package com.hr.techlabapp.CustomViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.hr.techlabapp.R;

public class ProductItemView extends CardView {
	public ImageView icon;
	public TextView title;

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
	}
}
