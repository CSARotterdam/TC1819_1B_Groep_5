package com.hr.techlabapp;

import com.hr.techlabapp.Networking.*;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class AppConfig {
	public static final Map<String, String> serverAddresses = new HashMap<String, String>()
	{{
		put("Conor HR", "145.137.52.171");
	}};

	public static String language = Locale.getDefault().getLanguage();
	public static User currentUser;
}
