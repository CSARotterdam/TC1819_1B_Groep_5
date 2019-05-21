package com.hr.techlabapp.CustomViews;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.DrawableRes;
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

import com.hr.techlabapp.Classes.Product;
import com.hr.techlabapp.R;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class ListItem extends ConstraintLayout {
	private Product product;
	private boolean ImageLoaded = false;

	@DrawableRes
	private final static int[] images = new int[] { R.drawable.arduino, R.drawable.cuteaf };

	private final static Random r = new Random();
	private ImageView image;
	private TextView name;
	private TextView availability;

	public ListItem(Context context) {
		super(context);
		Init(context);
	}

	public ListItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		Init(context);
	}

	public ListItem(Context context, AttributeSet attrs, int defStyleAttr) {
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
		image = new ImageView(context);
		image.setId(R.id.image);
		// makes the name
		name = new TextView(context);
		name.setId(R.id.name);
		name.setTextAlignment(TEXT_ALIGNMENT_CENTER);
		name.setTextColor(context.getColor(R.color.ListTextColor));
		name.setLayoutParams(
				new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		// makes the availability
		availability = new TextView(context);
		availability.setId(R.id.availability);
		availability.setTextAlignment(TEXT_ALIGNMENT_CENTER);
		availability.setTextColor(context.getColor(R.color.ListTextColor));
		availability.setLayoutParams(
				new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		// adds the views
		addView(image);
		addView(availability);
		addView(name);
		// makes the layouts
		SetConstraints();
	}

	@RequiresApi(17)
	private void Init17(Context context) {
		image = new ImageView(context);
		image.setId(R.id.image);
		name = new TextView(context);
		name.setId(R.id.name);
		name.setTextAlignment(TEXT_ALIGNMENT_CENTER);
		name.setTextColor(ContextCompat.getColor(context, R.color.ListTextColor));
		name.setLayoutParams(
				new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		availability = new TextView(context);
		availability.setId(R.id.availability);
		availability.setTextAlignment(TEXT_ALIGNMENT_CENTER);
		availability.setTextColor(ContextCompat.getColor(context, R.color.ListTextColor));
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
		name.setTextColor(ContextCompat.getColor(context, R.color.ListTextColor));
		name.setLayoutParams(
				new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		availability = new TextView(context);
		availability.setId(R.id.availability);
		availability.setTextColor(ContextCompat.getColor(context, R.color.ListTextColor));
		availability.setLayoutParams(
				new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		addView(image);
		addView(availability);
		addView(name);
		SetConstraints();
	}

	private void SetConstraints() {
		// makes the layout like listitem.xml
		ConstraintSet CSS = new ConstraintSet();
		CSS.clone(getContext(), R.layout.listitem);
		setConstraintSet(CSS);
	}

	private void setValues() {
		// sets the values
		this.name.setText(product.getName());
		this.availability.setText(getResources().getString(R.string.availability, product.getProductsAvailable(),
				product.getProductCount()));
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		new ShowImage().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	public Product getProduct() {
		return product;
	}

	class ShowImage extends AsyncTask<Void, Void, Bitmap> {
		@Override
		protected Bitmap doInBackground(Void... voids) {
			while (!ImageLoaded)
				if (isVisibleToUser()) {
					// checks if the image isn't already loaded and visible to the user
					Log.i("Background", "doInBackground: Begin");
					// gets a random image
					// TODO: make it not random
					Bitmap im = BitmapFactory.decodeResource(getResources(), images[r.nextInt(images.length)]);
					// sets the image of the product
					product.setImage(im);
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
			Log.i("Background", "doInBackground: End");
		}
	}

	public void setProduct(Product p) {
		this.product = p;
		setValues();
	}

	private boolean isVisibleToUser() {
		Rect scrollBounds = new Rect();
		View parent = (View) getParent();
		while (!(parent instanceof ScrollView))
			parent = (View) parent.getParent();
		parent.getHitRect(scrollBounds);
		return getLocalVisibleRect(scrollBounds);
	}

	private int dptopx(int dp) {
		// changes a value from dp to px
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
	}
}