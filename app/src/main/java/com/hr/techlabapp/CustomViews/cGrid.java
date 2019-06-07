package com.hr.techlabapp.CustomViews;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.hr.techlabapp.Networking.Product;

import java.util.ArrayList;

public class cGrid extends LinearLayout {
	// The List of Products
	private ArrayList<Product> Products = new ArrayList<>();
	// The Rows For the Grid
	private ArrayList<LinearLayout> Rows = new ArrayList<>();
	// If the view is a list or a grid
	private boolean List = false;

	private OnClickListener itemClicked = new OnClickListener() {
		@Override
		public void onClick(View v) {

		}
	};

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
				// sets itemClicked Event
				GI.setOnClickListener(itemClicked);
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
				// sets itemClicked Event
				LI.setOnClickListener(itemClicked);
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
				// sets itemClicked Event
				GI.setOnClickListener(itemClicked);
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
				// sets itemClicked Event
				LI.setOnClickListener(itemClicked);
				addView(LI);
			}
	}

	private int GetAmountInRow() {
		DisplayMetrics Size = new DisplayMetrics();
		// gets the size of the display
		((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(Size);
		return Size.widthPixels / dptopx(135);
	}

	// adds the product and if repopulate is true it repopulates the view
	public void AddProduct(Product p) {
		Products.add(p);
		if (!List) {
			GridItem GI = new GridItem(getContext());
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dptopx(100), dptopx(125));
			int margin = dptopx(5);
			lp.setMargins(margin, margin, margin, margin);
			GI.setLayoutParams(lp);
			GI.setProduct(p);
			// sets itemClicked Event
			GI.setOnClickListener(itemClicked);
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
		else {
			ListItem LI = new ListItem(getContext());
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, dptopx(125));
			int margin = dptopx(5);
			lp.setMargins(0, margin, 0, margin);
			LI.setLayoutParams(lp);
			LI.setProduct(p);
			// sets itemClicked Event
			LI.setOnClickListener(itemClicked);
			addView(LI);
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

	//sets the item clicked event for each item
	public void setItemClicked(OnClickListener itemClicked) {
		this.itemClicked = itemClicked;
		if(List)
			for(int i = 0; i < getChildCount(); i++)
				getChildAt(i).setOnClickListener(itemClicked);
		else
			for(LinearLayout Row: Rows)
				for (int i = 0; i < Row.getChildCount(); i++)
					Row.getChildAt(i).setOnClickListener(itemClicked);

	}

	// get all the items
	public ArrayList<ConstraintLayout> getItems(){
		ArrayList<ConstraintLayout> res = new ArrayList<>();
		if(List)
			for(int i = 0; i < getChildCount(); i++)
				res.add((ListItem)getChildAt(i));
		else
			for(LinearLayout r: Rows)
				for(int i = 0; i < r.getChildCount(); i++ )
					res.add((GridItem)r.getChildAt(i));
		return res;
	}
	
	public boolean isList() {return List;}

	private int dptopx(int dp) {
		// changes a value from dp to px
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
	}
}