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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.hr.techlabapp.Activities.NavHostActivity;
import com.hr.techlabapp.AppConfig;
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
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProductFragment extends Fragment {
	private static final String PRODUCT_NAME_KEY = "ProductName";
	private static final String PRODUCT_DESCRIPTION_KEY = "ProductDescription";
	private static final String PRODUCT_IMAGE_KEY = "ProductImage";
	private static final String PRODUCT_IMAGE_ID_KEY = "ProductImageID";
	private static final String PRODUCT_ID_KEY = "ProductId";
	private static final String PRODUCT_MANUFACTURER_KEY = "ProductManufacturer";
	private static final String PRODUCT_CATEGORY_KEY = "ProductCategory";
	private static final String PRODUCT_AVAILABILITY_KEY = "ProductAvailability";

	private static final int PICK_PHOTO = 2;
	private static final int TAKE_PICTURE = 1;
	private static final int CAMERA_PERMISSION = 3;
	public Context context;
	private Bitmap image = null;

	private Product product;

	private ArrayList<ProductCategory> ProductCategories = new ArrayList<>();

	public EditProductFragment() {
		// Required empty public constructor
	}

	@Override
	public void onAttach(@NonNull Context context) {
		((NavHostActivity)context).currentFragment = this;
		super.onAttach(context);
	}

	@Override
	public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_edit_product, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		//cat = getView().findViewById(R.id.product_cat);

		//noinspection ConstantConditions
		context = getView().getContext();

		 product = new Product(
				getArguments().getString(PRODUCT_ID_KEY),
				getArguments().getString(PRODUCT_MANUFACTURER_KEY),
				getArguments().getString(PRODUCT_CATEGORY_KEY),
				(HashMap<String, String>) getArguments().getSerializable(PRODUCT_NAME_KEY),
				(HashMap<String, String>) getArguments().getSerializable(PRODUCT_DESCRIPTION_KEY),
				getArguments().getString(PRODUCT_IMAGE_ID_KEY),
				(Bitmap) getArguments().getParcelable(PRODUCT_IMAGE_KEY));

		EditText productIDField = getView().findViewById(R.id.product_id);
		productIDField.setText(product.ID);
		EditText productNameField = getView().findViewById(R.id.product_name);
		productNameField.setText(product.name.get("en"));
		EditText manufacturerField = getView().findViewById(R.id.product_man);
		manufacturerField.setText(product.manufacturer);
		EditText descriptionField = getView().findViewById(R.id.product_des);
		descriptionField.setText(product.description.get("en"));

		new SpinnerActivity().executeOnExecutor(THREAD_POOL_EXECUTOR, product);

		Button addProduct = getView().findViewById(R.id.add_product);
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
				} else if(productID.length() > 50){
					Toast toast = Toast.makeText(context, getResources().getText(R.string.product_id_too_long), Toast.LENGTH_SHORT);
					toast.show();
					return;
				} else {
					product.ID = productID;
				}

				//Get product name. Cancel if not set.
				EditText productNameField = getView().findViewById(R.id.product_name);
				String productName = productNameField.getText().toString();
				if (productName.length() == 0) {
					Toast toast = Toast.makeText(context, getResources().getText(R.string.product_name_required), Toast.LENGTH_SHORT);
					toast.show();
					return;
				} else {
					product.name.put("en", productName);
				}

				//Get manufacterer. If none was set, default to Unknown
				EditText manufacturerField = getView().findViewById(R.id.product_man);
				String manufacturer = manufacturerField.getText().toString();
				if (manufacturer.length() == 0) {
					manufacturer = "Unknown";
				}
				product.manufacturer = manufacturer;

				//Get category.
				Spinner categoryField = getView().findViewById(R.id.product_cat);
				String selectedCategory = categoryField.getSelectedItem().toString();
				for (ProductCategory cat : ProductCategories) {
					Log.i("tag", cat.categoryID);
					Log.i("tag", cat.name.get("en"));
					Log.i("tag", selectedCategory);
					Log.i("tag", cat.name.get("en").equals(selectedCategory)+"");
					if (Objects.equals(cat.name.get("en"), selectedCategory)) {
						product.categoryID = cat.categoryID;
						Log.i("test", product.categoryID);
						break;
					}
				}

				//Get description. If none was set, default to null.
				EditText descriptionField = getView().findViewById(R.id.product_des);
				String description = descriptionField.getText().toString();
				if (description.length() == 0) {
					description = null;
				}
				product.description.put("en", description);

				//Get image. If none was set, skip
				if (image != null) {
					product.image = image;
				}

				new EditProductActivity().executeOnExecutor(THREAD_POOL_EXECUTOR, product);
			}
		});

		Button addImage = getView().findViewById(R.id.add_image);
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
								pickPhoto.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
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
	public class SpinnerActivity extends AsyncTask<Product, Void, Pair<Integer, ArrayAdapter<String>>>{
		private ProgressDialog dialog;

		@Override
		protected  void onPreExecute(){
			dialog = new ProgressDialog(context);
			dialog.setMessage(getResources().getString(R.string.loading));
			dialog.setCancelable(false);
			dialog.show();
		}

		@Override
		protected Pair<Integer, ArrayAdapter<String>> doInBackground(Product... products) {
			List<ProductCategory> contents;
			try{
				contents = ProductCategory.getProductCategories(new String[]{AppConfig.getLanguage()});
			}
			catch (JSONException ex){
				contents = new ArrayList<>();
			}

			List<String> categories = new ArrayList<>();
			int selection = -1;
			for(int i = 0; i < contents.size(); i++){
				if(contents.get(i).categoryID.equals(products[0].categoryID)){
					selection = i;
				}
				categories.add(contents.get(i).name.get("en"));
				ProductCategories.add(contents.get(i));
			}
			ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, categories);

			return new Pair<>(selection, adapter);
		}

		@Override
		protected void onPostExecute(Pair<Integer, ArrayAdapter<String>> result){
			Spinner dropdown = Objects.requireNonNull(getView()).findViewById(R.id.product_cat);
			Log.i("tag", result.first+"");
			dropdown.setAdapter(result.second);
			dropdown.setSelection(result.first);
			dialog.dismiss();
		}
	}

	@SuppressLint("StaticFieldLeak")
	public class EditProductActivity extends AsyncTask<Product, Void, String>{
		private ProgressDialog dialog;

		protected void onPreExecute(){
			dialog = new ProgressDialog(context);
			dialog.setMessage(getResources().getString(R.string.loading));
			dialog.show();
		}
		protected String doInBackground(Product... params){
			try{
				Product.updateProduct(params[0]);
			} catch (JSONException e){
				return getResources().getString(R.string.unexpected_error);
			} catch (Exceptions.AlreadyExists e){
				return  getResources().getString(R.string.product_already_exists);
			} catch (Exceptions.NoSuchProductCategory e) {
				return getResources().getString(R.string.product_category_doesnt_exist);
			} catch (Exceptions.NetworkingException e){
				return getResources().getString(R.string.unexpected_error);
			}

			return getResources().getString(R.string.product_edited_successfully);
		}

		protected void onPostExecute(String message){
			dialog.hide();
			Toast.makeText(context, message, Toast.LENGTH_LONG).show();
			Navigation.createNavigateOnClickListener(R.id.action_editProductFragment_to_productInfoFragment,getArguments());
		}
	}
}
