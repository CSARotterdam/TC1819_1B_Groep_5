package com.hr.techlabapp.CustomViews;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hr.techlabapp.Networking.Product;
import com.hr.techlabapp.Networking.Statistics;
import com.hr.techlabapp.R;

import org.json.JSONException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ThreadPoolExecutor;

public class GridItem extends ConstraintLayout {

	private Product product;
	private boolean ImageLoaded = false;

	private ImageView image;
	private TextView name;
	private TextView availability;

	public GridItem(Context context) {
		super(context);
		Init(context);
	}

	public GridItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		Init(context);
	}

	public GridItem(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		Init(context);
	}

	// Check which version of Init should be called
	private void Init(Context context) {
		if (Build.VERSION.SDK_INT >= 23)
			Init23(context);
		else if (Build.VERSION.SDK_INT >= 17)
			Init17(context);
		else
			Init15(context);
	}

	@RequiresApi(23)
	private void Init23(Context context) {
		// makes an image and sets its image to the image of the product
		image = new ImageView(context);
		image.setId(R.id.image);
		// makes the name
		name = new TextView(context);
		name.setId(R.id.name);
		name.setTextAlignment(TEXT_ALIGNMENT_CENTER);
		name.setTextColor(context.getColor(R.color.textColor));
		name.setBackgroundColor(context.getColor(R.color.textBackground));
		name.setLayoutParams(
				new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		// makes the availability
		availability = new TextView(context);
		availability.setId(R.id.availability);
		availability.setTextAlignment(TEXT_ALIGNMENT_CENTER);
		availability.setTextColor(context.getColor(R.color.textColor));
		availability.setBackgroundColor(context.getColor(R.color.textBackground));
		availability.setLayoutParams(
				new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		// adds the views
		addView(image);
		addView(availability);
		addView(name);
		// makes the layout
		SetConstraints();
	}

	@RequiresApi(17)
	private void Init17(Context context) {
		image = new ImageView(context);
		image.setId(R.id.image);
		name = new TextView(context);
		name.setId(R.id.name);
		name.setTextAlignment(TEXT_ALIGNMENT_CENTER);
		name.setTextColor(ContextCompat.getColor(context, R.color.textColor));
		name.setLayoutParams(
				new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		name.setBackgroundColor(ContextCompat.getColor(context, R.color.textBackground));
		availability = new TextView(context);
		availability.setId(R.id.availability);
		availability.setTextAlignment(TEXT_ALIGNMENT_CENTER);
		availability.setTextColor(ContextCompat.getColor(context, R.color.textColor));
		availability.setBackgroundColor(ContextCompat.getColor(context, R.color.textBackground));
		availability.setLayoutParams(
				new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		addView(image);
		addView(availability);
		addView(name);
		SetConstraints();
	}

	@RequiresApi(15)
	private void Init15(Context context) {
		image = new ImageView(context);
		image.setId(R.id.image);
		name = new TextView(context);
		name.setId(R.id.name);
		name.setTextColor(ContextCompat.getColor(context, R.color.textColor));
		name.setLayoutParams(
				new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		name.setBackgroundColor(ContextCompat.getColor(context, R.color.textBackground));
		availability = new TextView(context);
		availability.setId(R.id.availability);
		availability.setTextColor(ContextCompat.getColor(context, R.color.textColor));
		availability.setLayoutParams(
				new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		availability.setBackgroundColor(ContextCompat.getColor(context, R.color.textBackground));
		addView(image);
		addView(availability);
		addView(name);
		SetConstraints();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		new ShowImage().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	private void SetConstraints() {
		// make the layout like griditem.xml
		ConstraintSet CSS = new ConstraintSet();
		CSS.clone(getContext(), R.layout.griditem);
		setConstraintSet(CSS);
	}

	class ShowImage extends AsyncTask<Void, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(Void... voids) {
			while (!ImageLoaded)
				if (isVisibleToUser()) {
					// checks if the image isn't already loaded and visible to the user
					// gets a random image
					Bitmap im = product.image;
					int imh = im.getHeight();
					int imw = im.getWidth();
					int aspectRatio = imw / imh;
					// sets the new img width and height depending of the aspect ratio of the image
					// TODO: should use attributes
					int nimw = imh > imw ? dptopx(100) * aspectRatio : dptopx(125);
					int nimh = imw > imh ? dptopx(125) * aspectRatio : dptopx(100);
					// Scales the bitmap
					ImageLoaded = true;
					return Bitmap.createScaledBitmap(im, nimw, nimh, false);
				}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap aVoid) {
			super.onPostExecute(aVoid);
			if (aVoid == null)
				return;
			image.setImageBitmap(aVoid);
			image.setScaleType(ImageView.ScaleType.CENTER_CROP);
		}
	}

	private void setValues() {
		// sets the values
		this.name.setText(product.getName());
		// TODO: get availability from API
		try {
			HashMap<String, Integer> Av = Statistics.getProductAvailability(product.ID).get(product.ID);
			this.availability.setText(getResources().getString(R.string.availability, Av.get("inStock"), Av.get("total")));
		}
		catch (JSONException ex){
			this.availability.setText(getResources().getString(R.string.availability,0,0));
		}
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product p) {
		this.product = p;
		setValues();
	}

	// gets if the view is visible to the user
	private boolean isVisibleToUser() {
		Rect scrollBounds = new Rect();
		// gets the scrollview
		View parent = (View) getParent();
		while (!(parent instanceof ScrollView))
			parent = (View) parent.getParent();
		// sets the visible Rect to scrollBounds
		parent.getHitRect(scrollBounds);
		// check's if the view is in the visible rect
		return getLocalVisibleRect(scrollBounds);
	}

	private int dptopx(int dp) {
		// changes a value from dp to px
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
	}
}
