package com.hr.techlabapp.Fragments;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import androidx.appcompat.widget.Toolbar;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;

import com.google.android.material.navigation.NavigationView;
import com.hr.techlabapp.Activities.NavHostActivity;
import com.hr.techlabapp.CustomViews.GridItem;
import com.hr.techlabapp.CustomViews.ListItem;
import com.hr.techlabapp.CustomViews.cGrid;
import com.hr.techlabapp.Networking.Product;
import com.hr.techlabapp.Networking.Statistics;
import com.hr.techlabapp.R;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.hr.techlabapp.Fragments.ProductInfoFragment.PRODUCT_AVAILABILITY_KEY;
import static com.hr.techlabapp.Fragments.ProductInfoFragment.PRODUCT_CATEGORY_KEY;
import static com.hr.techlabapp.Fragments.ProductInfoFragment.PRODUCT_ID_KEY;
import static com.hr.techlabapp.Fragments.ProductInfoFragment.PRODUCT_IMAGE_ID_KEY;
import static com.hr.techlabapp.Fragments.ProductInfoFragment.PRODUCT_IMAGE_KEY;
import static com.hr.techlabapp.Fragments.ProductInfoFragment.PRODUCT_MANUFACTURER_KEY;
import static com.hr.techlabapp.Fragments.ProductInfoFragment.PRODUCT_NAME_KEY;

/**
 * A simple {@link Fragment} subclass.
 */
// gets rid of the useless/usefull warnings
@SuppressWarnings("all")
public class ProductListFragment extends Fragment
	implements IFragmentLimitationsWorkarounds{

	private cGrid Products;

	private Toolbar toolbar;

	private NavigationView navigationView;

	private Button addProduct;
	private Button statistics;

	private DrawerLayout drawerLayout;

	private ScrollView scrollView;

	public ProductListFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_product_list, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		((NavHostActivity)getActivity()).currentFragment = this;
		Products = getView().findViewById(R.id.products);
		Products.setList(false);
		(new FillProducts()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		toolbar = getView().findViewById(R.id.toolbar);
		((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

		drawerLayout = getView().findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawerLayout,toolbar,
				R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawerLayout.addDrawerListener(toggle);
		toggle.syncState();
		navigationView = getView().findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
				int id = menuItem.getItemId();
				if(id == R.id.statistics){
					Bundle b = new Bundle();
					ArrayList<Product> products = new ArrayList<>();
					if(Products.isList())
						for(ConstraintLayout cl: Products.getItems())
							products.add(((ListItem)cl).getProduct());
					else
						for(ConstraintLayout cl: Products.getItems())
							products.add(((GridItem)cl).getProduct());
					b.putSerializable("products", products);
					b.putSerializable("availabilty",GridItem.Availability);
					Navigation.findNavController(getView()).navigate(R.id.action_productListFragment_to_statisticsFragment);
				}
				return false;
			}
		});
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getActivity().getMenuInflater().inflate(R.menu.productlistmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so lon
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.language)
			return true;
		else if (id == R.id.Help)
			return true;
		else if (id == R.id.Notifications)
			return true;
		else if (id == R.id.add_product){
			Navigation.findNavController(getView()).navigate(R.id.action_productListFragment_to_addProductFragment);
			return true;
		}


		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onBackPressed() {
		if (drawerLayout.isDrawerOpen(GravityCompat.START))
			drawerLayout.closeDrawer(GravityCompat.START);
		return true;
	}

	class FillProducts extends AsyncTask<Void, Void, List<Product>>{

		@Override
		protected List<Product> doInBackground(Void... voids) {
			try{
				List<Product> Products = Product.getProducts(null,new String[]{"en"});
				new setAvailability().executeOnExecutor(THREAD_POOL_EXECUTOR, Products);
				return Products;
			}
			catch (JSONException ex){
				return  null;
			}
		}

		@Override
		protected void onPostExecute(List<Product> products) {
			super.onPostExecute(products);
			if(products == null)
				return;
			for (Product p : products)
				Products.AddProduct(p);
			Products.setItemClicked(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Product p = v instanceof GridItem? ((GridItem) v).getProduct(): ((ListItem)v).getProduct();
					Bundle b = new Bundle();
					b.putString(PRODUCT_CATEGORY_KEY, p.categoryID);
					b.putString(PRODUCT_ID_KEY, p.ID);
					b.putString(PRODUCT_MANUFACTURER_KEY, p.manufacturer);
					b.putString(PRODUCT_IMAGE_ID_KEY, p.imageId);
					b.putSerializable(PRODUCT_NAME_KEY, p.name);
					b.putSerializable(PRODUCT_AVAILABILITY_KEY, GridItem.Availability.get(p.ID));
					b.putParcelable(PRODUCT_IMAGE_KEY, p.image);
					Navigation.findNavController(getView()).navigate(R.id.action_productListFragment_to_productInfoFragment,b);
				}
			});
		}
	}

	class setAvailability extends AsyncTask<List<Product>, Void, Void>{
		@Override
		protected Void doInBackground(List<Product>... products) {
			ArrayList<String> productIds = new ArrayList<>();

			for(Product p: products[0])
				productIds.add(p.ID);
			try{
				GridItem.Availability = ListItem.Availability = Statistics.getProductAvailability(productIds);
			}
			catch (JSONException ex){
				Log.e("TAG", "SHIT BROKE");
				GridItem.Availability = ListItem.Availability = new HashMap<String, HashMap<String, Integer>>();
			}
			return null;
		}
	}
}
