package com.hr.techlabapp;

import com.hr.techlabapp.Networking.*;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class AppConfig {
	public static final String serverAddress = "192.168.2.101";

	public static String language = Locale.getDefault().getLanguage();
	public static User currentUser;
}
