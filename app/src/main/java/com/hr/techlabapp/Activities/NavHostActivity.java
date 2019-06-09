package com.hr.techlabapp.Activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.navigation.NavigationView;
import com.hr.techlabapp.Fragments.IFragmentLimitationsWorkarounds;
import com.hr.techlabapp.R;

public class NavHostActivity extends AppCompatActivity{
	public Fragment currentFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nav_host);
	}

	@Override
	public void onBackPressed() {
		if(!(currentFragment instanceof IFragmentLimitationsWorkarounds) || !((IFragmentLimitationsWorkarounds)currentFragment).onBackPressed())
			super.onBackPressed();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return currentFragment instanceof IFragmentLimitationsWorkarounds?
				((IFragmentLimitationsWorkarounds)currentFragment).onCreateOptionsMenu(menu) :super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return currentFragment.onOptionsItemSelected(item);
	}
}
