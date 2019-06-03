package com.hr.techlabapp.CustomViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hr.techlabapp.Networking.Product;
import com.hr.techlabapp.R;

import org.json.JSONException;

import java.util.HashMap;

public class GridItem extends ConstraintLayout {

	private Product product;
	private boolean ImageLoaded = false;

	public static HashMap<String,HashMap<String, Integer>> Availability;
	public static HashMap<String, Bitmap> Images = new HashMap<>();

	private ImageView image;
	private TextView name;
	private TextView availability;
	private ProgressBar progress;

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
		image.setVisibility(View.INVISIBLE);
		// makes the progress bar you see when there is no image
		progress = new ProgressBar(context);
		progress.setId(R.id.progress);
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
		addView(progress);
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
		image.setVisibility(View.INVISIBLE);
		progress = new ProgressBar(context);
		progress.setId(R.id.progress);
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
		addView(progress);
		addView(image);
		addView(availability);
		addView(name);
		SetConstraints();
	}

	@RequiresApi(15)
	private void Init15(Context context) {
		image = new ImageView(context);
		image.setId(R.id.image);
		image.setVisibility(View.INVISIBLE);
		progress = new ProgressBar(context);
		progress.setId(R.id.progress);
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
		addView(progress);
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
					Bitmap im;
					try{
						im = product.getImage();
					}
					catch (JSONException ex){
						im = BitmapFactory.decodeResource(getResources(),R.drawable.cuteaf);
					}
					Images.put(product.imageId,im);
					int imh = im.getHeight();
					int imw = im.getWidth();
					float aspectRatio = (float)imw / imh;
					// sets the new img width and height depending of the aspect ratio of the image
					// TODO: should use attributes
					int nimw = imh > imw ? (int)(dptopx(100) * aspectRatio) : dptopx(125);
					int nimh = imw > imh ? (int)(dptopx(125) * aspectRatio) : dptopx(100);
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
			// bcz progress.setVisibility(INVISIBLE) doesn't work
			removeView(progress);
		}
	}

	private void setValues() {
		// sets the values
		this.name.setText(product.getName("en"));
		new setAvailability().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		/*		this.availability.setText(getResources().getString(R.string.availability,
				Availability.get(product.ID).get("inStock"),
				Availability.get(product.ID).get("total")));*/
	}

	class setAvailability extends AsyncTask<Void,Void, HashMap<String,Integer>>{
		@Override
		protected HashMap<String, Integer> doInBackground(Void... voids) {
			while(Availability == null){}
			return Availability.get(product.ID);
		}

		@Override
		protected void onPostExecute(HashMap<String, Integer> av) {
			availability.setText(getResources().getString(R.string.availability, av.get("inStock"), av.get("total")));
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

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}
}
