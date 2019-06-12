package com.hr.techlabapp.CustomViews;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Looper;
import androidx.annotation.RequiresApi;
import androidx.annotation.DrawableRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hr.techlabapp.AppConfig;
import com.hr.techlabapp.Networking.Product;
import com.hr.techlabapp.Networking.Statistics;
import com.hr.techlabapp.R;

import org.json.JSONException;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class ListItem extends ConstraintLayout {
	private Product product;
	private boolean ImageLoaded = false;
	private boolean isBusy = false;

	public static HashMap<String,HashMap<String, Integer>> Availability;
	public static HashMap<String, Bitmap> Images = GridItem.Images;

	private ImageView image;
	private TextView name;
	private TextView availability;
	private ProgressBar progress;

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
		else
			Init17(context);
	}

	@RequiresApi(23)
	private void Init23(Context context) {
		image = new ImageView(context);
		image.setId(R.id.image);
		image.setVisibility(View.INVISIBLE);
		// makes the name
		// makes the progress bar you see when there is no image
		progress = new ProgressBar(context);
		progress.setId(R.id.progress);
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
		addView(progress);
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
		image.setVisibility(View.INVISIBLE);
		progress = new ProgressBar(context);
		progress.setId(R.id.progress);
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
		addView(progress);
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
		this.name.setText(product.getName(AppConfig.getLanguage()));
		new setAvailability().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}


	class setAvailability extends AsyncTask<Void,Void, HashMap<String,Integer>>{
		@Override
		protected HashMap<String, Integer> doInBackground(Void... voids) {
			while(Availability == null){}
			return Availability.get(product.ID);
		}

		@Override
		protected void onPostExecute(HashMap<String, Integer> av) {
			availability.setText(getResources().getString(R.string.availability, av.get("available"), av.get("total")));
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		isBusy = true;
		new ShowImage().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}


	public Product getProduct() {
		return product;
	}

	class ShowImage extends AsyncTask<Void, Void, Bitmap> {
		@Override
		protected Bitmap doInBackground(Void... voids) {

			if (isVisibleToUser()) {
				Bitmap im = null;
				if (Images.get(product.imageId) != null)
					im = Images.get(product.imageId);
				else
					try {
						im = product.getImage();
					} catch (JSONException ex) {
						return null;
					} finally {
						Images.put(product.imageId, im);
					}
				int imh = im.getHeight();
				int imw = im.getWidth();
				float aspectRatio = (float) imw / imh;
				// sets the new img width and height depending of the aspect ratio of the image
				// TODO: should use attributes
				int nimw = imh > imw ? (int) (dptopx(100) * aspectRatio) : dptopx(125);
				int nimh = imw > imh ? (int) (dptopx(125) * aspectRatio) : dptopx(100);
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
			progress.setVisibility(INVISIBLE);
		}
	}

	public void setProduct(Product p) {
		this.product = p;
		setValues();
	}

	public void loadImage(){
		if(!ImageLoaded || !isBusy){
			isBusy = true;
			new ShowImage().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
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

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}
}