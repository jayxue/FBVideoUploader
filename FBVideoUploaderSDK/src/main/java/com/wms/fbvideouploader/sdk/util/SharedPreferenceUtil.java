package com.wms.fbvideouploader.sdk.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceUtil {

	public static String FacebookUserAccessToken = "FacebookUserAccessToken";
	public static String FacebookUserID = "FacobookUserID";
	public static String FacebookUserName = "FacebookUserName";

	private static String preferencesFileName = "PreferenceFile";

	public static String getPreferenceItemByName(Context context, String itemName) {
		SharedPreferences settings = context.getSharedPreferences(preferencesFileName, Activity.MODE_PRIVATE);
		return settings.getString(itemName, "");
	}

	public static void savePreferenceItemByName(Context context, String itemName, String value) {
		SharedPreferences.Editor editor = context.getSharedPreferences(preferencesFileName, Activity.MODE_PRIVATE).edit();
		editor.putString(itemName, value);
		editor.commit();
	}

}
