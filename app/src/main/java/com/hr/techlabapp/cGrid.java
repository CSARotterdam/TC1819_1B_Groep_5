package com.hr.techlabapp;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class cGrid extends LinearLayout {
	// The List of Products
	private ArrayList<Product> Products = new ArrayList<>();
	// The Rows For the Grid
	private ArrayList<LinearLayout> Rows = new ArrayList<>();
	// If the view is a list or a grid
	private boolean List = false;

	public cGrid(Context context) {
		super(context);
		init(context, null);
	}

	public cGrid(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public cGrid(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	@RequiresApi(21)
	public cGrid(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context, attrs);
	}

	// the initializer for the grid
	private void init(Context context, AttributeSet attrs) {
		// set the rows to the middle
		setGravity(Gravity.CENTER);
		// makes it stack Vertical
		setOrientation(VERTICAL);
		// Makes the First row and adds it the Rows ArrayList
		LinearLayout L1 = new LinearLayout(context);
		L1.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		L1.setOrientation(HORIZONTAL);
		addView(L1);
		Rows.add(L1);
		// Starts adding the products
		Populate();
	}

	// Check which version of populate should be called
	private void Populate() {
		if (Build.VERSION.SDK_INT >= 21)
			Populate21();
		else
			Populate15();
	}

	@RequiresApi(15)
	private void Populate15() {
		if (!List)
			for (Product p : Products) {
				GridItem GI = new GridItem(getContext());
				// Makes the parameters for height width
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dptopx(100), dptopx(125));
				int margin = dptopx(5);
				lp.setMargins(margin, margin, margin, margin);
				// Sets the parameters
				GI.setLayoutParams(lp);
				// sets The Product
				GI.setProduct(p);
				// checks if the row is full and makes a new one if it is
				// and adds the product
				if (Rows.get(Rows.size() - 1).getChildCount() == GetAmountInRow()) {
					LinearLayout L1 = new LinearLayout(getContext());
					L1.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
					L1.setOrientation(HORIZONTAL);
					L1.addView(GI);
					addView(L1);
					Rows.add(L1);
				} else {
					Rows.get(Rows.size() - 1).addView(GI);
				}
			}
		else
			for (Product p : Products) {
				ListItem LI = new ListItem(getContext());
				// makes the paramters
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, dptopx(125));
				int margin = dptopx(5);
				lp.setMargins(0, margin, 0, margin);
				// sets the parameters
				LI.setLayoutParams(lp);
				// sets the product
				LI.setProduct(p);
				addView(LI);
			}
	}

	// mostly the same as Populate15
	@RequiresApi(21)
	private void Populate21() {
		if (!List)
			for (Product p : Products) {
				GridItem GI = new GridItem(getContext());
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dptopx(100), dptopx(125));
				int margin = dptopx(5);
				lp.setMargins(margin, margin, margin, margin);
				GI.setLayoutParams(lp);
				GI.setProduct(p);
				// sets the elevation for shadows
				/// if only it worked
				GI.setElevation(dptopx(5));
				GI.setTranslationZ(dptopx(2));
				if (Rows.get(Rows.size() - 1).getChildCount() == GetAmountInRow()) {
					LinearLayout L1 = new LinearLayout(getContext());
					L1.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
					L1.setOrientation(HORIZONTAL);
					L1.addView(GI);
					addView(L1);
					Rows.add(L1);
				} else {
					Rows.get(Rows.size() - 1).addView(GI);
				}
			}
		else
			for (Product p : Products) {
				ListItem LI = new ListItem(getContext());
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, dptopx(125));
				int margin = dptopx(5);
				lp.setMargins(0, margin, 0, margin);
				LI.setLayoutParams(lp);
				LI.setProduct(p);
				addView(LI);
			}
	}

	private int GetAmountInRow() {
		DisplayMetrics Size = new DisplayMetrics();
		// gets the size of the display
		((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(Size);
		return Size.widthPixels / dptopx(135);
	}

	// adds the product and repopulates the view
	public void AddProduct(Product p) {
		AddProduct(p, true);
	}

	// adds the product and if repopulate is true it repopulates the view
	public void AddProduct(Product p, boolean repopulate) {
		Products.add(p);
		if (repopulate) {
			// clears the view
			for (LinearLayout l : Rows)
				l.removeAllViews();
			removeAllViews();
			Rows.clear();
			if (!List) {
				// makes the first row
				LinearLayout L1 = new LinearLayout(getContext());
				L1.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				L1.setOrientation(HORIZONTAL);
				addView(L1);
				Rows.add(L1);
			}
			// repopulates
			Populate();
		}
	}

	public void setList(boolean list) {
		// checks if the value is changed
		if (list == List) {
			return;
		}
		List = list;

		if (List) {
			// makes the list
			for (LinearLayout l : Rows)
				l.removeAllViews();
			removeAllViews();
			Rows.clear();
			Populate();
		} else {
			// makes the grid
			removeAllViews();
			LinearLayout L1 = new LinearLayout(getContext());
			L1.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			L1.setOrientation(HORIZONTAL);
			addView(L1);
			Rows.add(L1);
			Populate();
		}
	}

	private int dptopx(int dp) {
		// changes a value from dp to px
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
	}
}