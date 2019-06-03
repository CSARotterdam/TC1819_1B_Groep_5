package com.hr.techlabapp.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.hr.techlabapp.Networking.Product;
import com.hr.techlabapp.Networking.ProductCategory;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hr.techlabapp.Networking.Exceptions;
import com.hr.techlabapp.R;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddProductFragment extends Fragment {
	private static final int PICK_PHOTO = 2;
	private static final int TAKE_PICTURE = 1;
	public Context context;
	private Bitmap image = null;

	private ArrayList<ProductCategory> ProductCategories = new ArrayList<>();

	Button addProduct;
	Button addImage;

	public AddProductFragment() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_add_product, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		//cat = getView().findViewById(R.id.product_cat);

		context = getView().getContext();

		new SpinnerActivity().execute();

		addProduct = getView().findViewById(R.id.add_product);
		addProduct.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			//Get product ID. Cancel if not set.
			EditText productIDField = (EditText) getView().findViewById(R.id.product_id);
			String productID = productIDField.getText().toString();
			if (productID.length() == 0) {
				Toast toast = Toast.makeText(context, "Product ID is required!", Toast.LENGTH_SHORT);
				toast.show();
				return;
			}

			//Get product name. Cancel if not set.
			EditText productNameField = (EditText) getView().findViewById(R.id.product_name);
			String productName = productNameField.getText().toString();
			if (productName.length() == 0) {
				Toast toast = Toast.makeText(context, "Product name is required!", Toast.LENGTH_SHORT);
				toast.show();
				return;
			}

			//Get manufacterer. If none was set, default to Unknown
			EditText manufacturerField = (EditText) getView().findViewById(R.id.product_man);
			String manufacturer = manufacturerField.getText().toString();
			if (manufacturer.length() == 0) {
				manufacturer = "Unknown";
			}

			//Get category. If none was set (which shouldn't be possible), default to uncategorized.
			Spinner categoryField = (Spinner) getView().findViewById(R.id.product_cat);
			String selectedCategory = categoryField.getSelectedItem().toString();
			String categoryID = "uncategorized";
			for(ProductCategory cat : ProductCategories){
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
			new AddProductActivity().execute(product);
			}
		});

		addImage = getView().findViewById(R.id.add_image);
		addImage.setOnClickListener(new View.OnClickListener() {
			public AlertDialog alert;
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setMessage("Select an image from where?") //TODO Find better text. Also language support.
						.setCancelable(true)
						.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								alert.hide();
								Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
								if (takePicture.resolveActivity(context.getPackageManager()) != null) {
									startActivityForResult(takePicture, TAKE_PICTURE);
								}

							}
						})
						.setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								alert.hide();
								Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
								pickPhoto.setType("image/*");
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
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		switch(requestCode){
			case TAKE_PICTURE:
				if(resultCode == RESULT_OK){
					Bundle extras = data.getExtras();
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

	public class SpinnerActivity extends AsyncTask<Void, Void, List<ProductCategory>>{
		private ProgressDialog dialog;

		@Override
		protected  void onPreExecute(){
			dialog = new ProgressDialog(context);
			dialog.setMessage("Loading...");
			dialog.setCancelable(false);
			dialog.show();
		}

		@Override
		protected List<ProductCategory> doInBackground(Void... voids) {
			List<ProductCategory> contents;
			try{
				return ProductCategory.getProductCategories(new String[]{"en"});
			}
			catch (JSONException ex){
				return new ArrayList<ProductCategory>();
			}
		}

		@Override
		protected void onPostExecute(List<ProductCategory> contents){
			List<String> categories = new ArrayList<>();
			for(ProductCategory cat : contents){
				categories.add(cat.name.get("en"));
				ProductCategories.add(cat);
			}

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, categories);
			Spinner dropdown = (Spinner) getView().findViewById(R.id.product_cat);
			dropdown.setAdapter(adapter);
			dialog.hide();
			return;
		}
	}

	public class AddProductActivity extends AsyncTask<Product, Void, Integer>{
		private ProgressDialog dialog;

		protected void onPreExecute(){
			dialog = new ProgressDialog(context);
			dialog.setMessage("Working...");
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
					message = "Product added successfully.";
					break;
				case 1:
					message = "A product already exists with this ID.";
					break;
				case 2:
					message = "The selected category does not exist.";
					break;
				default:
					message = "An unexpected error occurred.";
					break;
			}

			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setMessage(message)
				.setCancelable(false)
				.setPositiveButton("OK", null);
			AlertDialog alert = builder.create();
			alert.show();
		}
	}
}
