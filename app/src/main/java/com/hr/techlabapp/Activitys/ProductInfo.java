package com.hr.techlabapp.Activitys;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hr.techlabapp.Classes.Product;
import com.hr.techlabapp.CustomViews.DeleteItemDialog;
import com.hr.techlabapp.R;

public class ProductInfo extends AppCompatActivity {
	//TODO:not use this
	public static Product currentProduct;

	private ImageView image;

	private TextView name;
	private TextView id;
	private TextView man;
	private TextView cat;
	private TextView stock;

	private Button borrow;
	private Button delete;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_info);
		image = findViewById(R.id.image);
		image.setImageBitmap(currentProduct.getImage());

		// sets the string values
		name = findViewById(R.id.product_name);
		name.setText(currentProduct.getName());
		id = findViewById(R.id.product_id);
		id.setText(currentProduct.getProductID());
		man = findViewById(R.id.product_man);
		man.setText(currentProduct.getManufacturer());
		cat = findViewById(R.id.product_cat);
		cat.setText(currentProduct.getProductCategory());
		stock = findViewById(R.id.product_stock);
		stock.setText(String.valueOf(currentProduct.getProductsAvailable()));

		// sets the onClickListener
		borrow = findViewById(R.id.borrow);
		borrow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// starts the CheckLendRequest Activity
				startActivity(new Intent(ProductInfo.this,CheckLendRequest.class));
			}
		});

		// sets the onClickListener
		delete = findViewById(R.id.delete);
		delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// makes a new delete dialog
				DeleteItemDialog dialog = new DeleteItemDialog();
				// makes the args
				Bundle args = new Bundle();
				args.putCharSequence("ID",currentProduct.getProductID());
				// sets the args
				dialog.setArguments(args);
				// shows the dialog
				dialog.show(getSupportFragmentManager(),String.format("delete item %s",currentProduct.getProductID()));
			}
		});
	}
}
