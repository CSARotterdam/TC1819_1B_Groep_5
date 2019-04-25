package com.hr.techlabapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ProductList extends AppCompatActivity {

	private cGrid Products;

	private Button addProduct;
	private Button statistics;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_list);
		Products = findViewById(R.id.products);

		for(int i = 0; i  < 30; i++)
			Products.AddProduct(new Product("Arduino","des","man","ID","cat",8,5),false);
		Products.AddProduct(new Product("Arduino","des","man","ID","cat",8,5));

		Products.setItemClicked(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Product newp = v instanceof GridItem? ((GridItem) v).getProduct(): ((ListItem)v).getProduct();
				ProductInfo.currentProduct = newp;
				startActivity(new Intent(ProductList.this, ProductInfo.class));
			}
		});

		addProduct = findViewById(R.id.add_product);
		addProduct.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(ProductList.this, AddProduct.class));
			}
		});
		statistics = findViewById(R.id.statistics);
		statistics.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(ProductList.this, Statistics.class));
			}
		});
	}
}
