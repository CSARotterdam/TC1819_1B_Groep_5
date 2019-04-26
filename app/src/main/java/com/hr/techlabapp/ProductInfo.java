package com.hr.techlabapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

		borrow = findViewById(R.id.borrow);
		borrow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(ProductInfo.this,CheckLendRequest.class));
			}
		});

		delete = findViewById(R.id.delete);
		delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DeleteItemDialog dialog = new DeleteItemDialog();
				Bundle args = new Bundle();
				args.putCharSequence("ID",currentProduct.getProductID());
				dialog.setArguments(args);
				dialog.show(getSupportFragmentManager(),String.format("delete item %s",currentProduct.getProductID()));
			}
		});
	}
}
