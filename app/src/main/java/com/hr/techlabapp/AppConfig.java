package com.hr.techlabapp;

import android.content.res.Resources;

import com.hr.techlabapp.Networking.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public final class AppConfig {
	public static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
	public static final String serverAddress = "192.168.86.32";
	//public static final String serverAddress = "192.168.178.9"; //Nolz desktop

	public static User currentUser;

	public static String getLanguage() {
		return Resources.getSystem().getConfiguration().locale.getLanguage();
	}
}
