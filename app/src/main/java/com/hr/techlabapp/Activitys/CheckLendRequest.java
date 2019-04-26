package com.hr.techlabapp.Activitys;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hr.techlabapp.R;

import java.time.LocalDate;
import java.util.Calendar;

public class CheckLendRequest extends AppCompatActivity {

	private ImageView image;

	private TextView username;
	private TextView requestDate;
	private TextView name;
	private TextView amount;

	private Button userHistory;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_check_lend_request);

		image = findViewById(R.id.image);
		//TODO: I repeat NOT USE THIS
		image.setImageBitmap(ProductInfo.currentProduct.getImage());

		username = findViewById(R.id.username);
		username.setText(getResources().getString(R.string.username_id,"Gijs","Puelinckx",958956));
		requestDate = findViewById(R.id.request_date);
		requestDate.setText(getResources().getString(R.string.date_of_request, Build.VERSION.SDK_INT >= 26 ? LocalDate.now(): Calendar.getInstance().getTime(), Build.VERSION.SDK_INT >= 26 ? LocalDate.now(): Calendar.getInstance().getTime(),Build.VERSION.SDK_INT >= 26 ? LocalDate.now(): Calendar.getInstance().getTime()));
		name = findViewById(R.id.name);
		name.setText(ProductInfo.currentProduct.getName());
		amount = findViewById(R.id.amount);
		amount.setText(getResources().getString(R.string.amount_value,ProductInfo.currentProduct.getProductsAvailable()));

		userHistory = findViewById(R.id.user_history);
		userHistory.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				UserHistoryDialog dialog = new UserHistoryDialog();
				Bundle args = new Bundle();
				args.putInt("ID",958956);
				dialog.setArguments(args);
				dialog.show(getSupportFragmentManager(),String.format("user history %07d",958956));
			}
		});
	}
}
