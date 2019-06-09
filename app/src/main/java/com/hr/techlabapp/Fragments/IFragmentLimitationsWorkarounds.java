package com.hr.techlabapp.Fragments;

import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;

public interface IFragmentLimitationsWorkarounds {
	boolean onBackPressed();
	boolean onCreateOptionsMenu(Menu menu);
}
