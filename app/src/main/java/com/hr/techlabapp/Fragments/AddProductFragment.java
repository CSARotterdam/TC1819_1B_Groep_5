package com.hr.techlabapp.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.hr.techlabapp.Networking.Exceptions;
import com.hr.techlabapp.Networking.Product;
import com.hr.techlabapp.Networking.ProductCategory;
import com.hr.techlabapp.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddProductFragment extends Fragment {
	private static final int PICK_PHOTO = 2;
	private static final int TAKE_PICTURE = 1;
	private static final int CAMERA_PERMISSION = 3;
	public Context context;
	private Bitmap image = null;

	private ArrayList<ProductCategory> ProductCategories = new ArrayList<>();

	Button addProduct;
	Button addImage;

	public AddProductFragment() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_add_product, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		//cat = getView().findViewById(R.id.product_cat);

		//noinspection ConstantConditions
		context = getView().getContext();

		new SpinnerActivity().executeOnExecutor(THREAD_POOL_EXECUTOR);

		addProduct = getView().findViewById(R.id.add_product);
		addProduct.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			//Get product ID. Cancel if not set.
			@SuppressWarnings("ConstantConditions")
			EditText productIDField = getView().findViewById(R.id.product_id);
			String productID = productIDField.getText().toString();
			if (productID.length() == 0) {
				Toast toast = Toast.makeText(context, getResources().getText(R.string.product_id_required), Toast.LENGTH_SHORT);
				toast.show();
				return;
			}

			//Get product name. Cancel if not set.
			EditText productNameField = getView().findViewById(R.id.product_name);
			String productName = productNameField.getText().toString();
			if (productName.length() == 0) {
				Toast toast = Toast.makeText(context, getResources().getText(R.string.product_name_required), Toast.LENGTH_SHORT);
				toast.show();
				return;
			}

			//Get manufacterer. If none was set, default to Unknown
			EditText manufacturerField = getView().findViewById(R.id.product_man);
			String manufacturer = manufacturerField.getText().toString();
			if (manufacturer.length() == 0) {
				manufacturer = "Unknown";
			}

			//Get category. If none was set (which shouldn't be possible), default to uncategorized.
			Spinner categoryField = getView().findViewById(R.id.product_cat);
			String selectedCategory = categoryField.getSelectedItem().toString();
			String categoryID = "uncategorized";
			for(ProductCategory cat : ProductCategories){
				//noinspection ConstantConditions
				if(cat.name.get("en").equals(selectedCategory)){
					categoryID = cat.categoryID;
					break;
				}
			}

			//Get description. If none was set, default to null.
			EditText descriptionField = getView().findViewById(R.id.product_des);
			String description = descriptionField.getText().toString();
			if (description.length() == 0) {
				description = null;
			}

			//Create language + description items
			//TODO Add language support
			HashMap<String, String> name = new HashMap<>();
			name.put("en", productName);
			HashMap<String, String> desc = new HashMap<>();
			desc.put("en", description);

			//Create a product. Different constructor depending on whether an image was selected or not.
			Product product;
			if(image == null){
				product = new Product(productID, manufacturer, categoryID, name, desc);
			} else {
				product = new Product(productID, manufacturer, categoryID, name, desc, productID+"_image", image);
			}
			new AddProductActivity().executeOnExecutor(THREAD_POOL_EXECUTOR, product);
			}
		});

		addImage = getView().findViewById(R.id.add_image);
		addImage.setOnClickListener(new View.OnClickListener() {
			AlertDialog alert;
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setMessage("Select an image from where?") //TODO Find better text. Also language support.
						.setCancelable(true)
						.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								alert.dismiss();
								Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
								if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
									requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
								} else if (takePicture.resolveActivity(context.getPackageManager()) != null) {
									startActivityForResult(takePicture, TAKE_PICTURE);
								}
							}
						})
						.setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								alert.dismiss();
								Intent pickPhoto = new Intent(Intent.ACTION_PICK);
								pickPhoto.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
								String[] formats = {"image/jpeg", "image/png", "image/jpg", "image/gif", "image/bmp", "image/webp", "image/heif"};
								pickPhoto.putExtra("EXTRA_MIME_TYPES", formats);
								startActivityForResult(Intent.createChooser(pickPhoto, "Select Picture"), PICK_PHOTO);
							}
						})
						.setNeutralButton("Cancel", null);
				alert = builder.create();
				alert.show();
			}
		});
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		Log.i("tag", "hello world");
		if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
			Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			if (takePicture.resolveActivity(context.getPackageManager()) != null) {
				startActivityForResult(takePicture, TAKE_PICTURE);
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		switch(requestCode){
			case TAKE_PICTURE:
				if(resultCode == RESULT_OK){
					Bundle extras = data.getExtras();
					assert extras != null;
					image = (Bitmap) extras.get("data");
				}
				break;
			case PICK_PHOTO:
				if(resultCode == RESULT_OK){
					Uri imageUri = data.getData();
					try {
						image = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
		}
	}

	@SuppressLint("StaticFieldLeak")
	public class SpinnerActivity extends AsyncTask<Void, Void, List<ProductCategory>>{
		private ProgressDialog dialog;

		@Override
		protected  void onPreExecute(){
			dialog = new ProgressDialog(context);
			dialog.setMessage(getResources().getString(R.string.loading));
			dialog.setCancelable(false);
			dialog.show();
		}

		@Override
		protected List<ProductCategory> doInBackground(Void... voids) {
			try{
				return ProductCategory.getProductCategories(new String[]{"en"});
			}
			catch (JSONException ex){
				return new ArrayList<>();
			}
		}

		@Override
		protected void onPostExecute(List<ProductCategory> contents){
			List<String> categories = new ArrayList<>();
			for(ProductCategory cat : contents){
				categories.add(cat.name.get("en"));
				ProductCategories.add(cat);
			}

			ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, categories);
			@SuppressWarnings("ConstantConditions")
			Spinner dropdown = getView().findViewById(R.id.product_cat);
			dropdown.setAdapter(adapter);
			dialog.dismiss();
		}
	}

	@SuppressLint("StaticFieldLeak")
	public class AddProductActivity extends AsyncTask<Product, Void, Integer>{
		private ProgressDialog dialog;

		protected void onPreExecute(){
			dialog = new ProgressDialog(context);
			dialog.setMessage(getResources().getString(R.string.loading));
			dialog.show();
		}
		protected Integer doInBackground(Product... params){
			try{
				Product.addProduct(params[0]);
			} catch (JSONException e){
				return -1;
			} catch (Exceptions.AlreadyExists e){
				return  1;
			} catch (Exceptions.NoSuchProductCategory e){
				return 2;
			}

			return 0;
		}
		protected void onPostExecute(Integer result){
			dialog.hide();
			String message;
			switch(result){
				case 0:
					message = getResources().getString(R.string.product_added_successfully);
					break;
				case 1:
					message = getResources().getString(R.string.product_already_exists);
					break;
				case 2:
					message = getResources().getString(R.string.product_category_doesnt_exist);
					break;
				default:
					message = getResources().getString(R.string.unexpected_error);
					break;
			}

			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setMessage(message)
				.setCancelable(false)
				.setPositiveButton(getResources().getString(R.string.OK), null);
			AlertDialog alert = builder.create();
			alert.show();
		}
	}
}
