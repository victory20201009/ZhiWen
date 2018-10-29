package com.zzu.zk.zhiwen.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceUtils {
    private static SharedPreferences mInstance;

    private static SharedPreferences getInstance(Context context) {
        if (mInstance == null) {
            mInstance = context.getSharedPreferences("SharePreferenceUtils", Context.MODE_PRIVATE);
        }
        return mInstance;
    }

    public static void putString(Context context, String key, String value) {
        SharedPreferences preferences = getInstance(context);
        //存入数据
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
    public static String getString(Context context, String key) {
        SharedPreferences preferences = getInstance(context);
        return preferences.getString(key, "");
    }




    public static void putInteger(Context context, String key, Integer value) {
        SharedPreferences preferences = getInstance(context);
        //存入数据
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }
    public static Integer getInteger(Context context, String key) {
        SharedPreferences preferences = getInstance(context);
        return preferences.getInt(key,-2);
    }
}
