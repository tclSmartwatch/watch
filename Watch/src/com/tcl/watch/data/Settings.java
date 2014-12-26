package com.tcl.watch.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * init before use.
 */
public final class Settings {
	Settings() {
	}

	static SharedPreferences prefer = null;

	public static void init(Context ctx) {
		if (prefer == null) {
			prefer = ctx.getSharedPreferences("main_prefer",
					Context.MODE_WORLD_WRITEABLE);
		}
	}

	public static String getString(String key, String defValue) {
		return prefer.getString(key, defValue);
	}

	public static int getInt(String key, int defValue) {
		return prefer.getInt(key, defValue);
	}
	public static float getFloat(String key, float defValue) {
		return prefer.getFloat(key, defValue);
	}

	public static boolean getBoolean(String key, boolean defValue) {
		return prefer.getBoolean(key, defValue);
	}

	public static long getLong(String key, long defValue) {
		return prefer.getLong(key, defValue);
	}

	public static void putString(String key, String value) {
		prefer.edit().putString(key, value).commit();
	}

	public static void putInt(String key, int value) {
		prefer.edit().putInt(key, value).commit();
	}

	public static void putBoolean(String key, boolean value) {
		prefer.edit().putBoolean(key, value).commit();
	}

	public static void putLong(String key, long value) {
		prefer.edit().putLong(key, value).commit();
	}
	public static void putFloat(String key, float value) {
		prefer.edit().putFloat(key, value).commit();
	}

}
