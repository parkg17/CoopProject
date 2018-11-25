package skku.alticastvux.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by woorim on 2018. 7. 25..
 */

public class SharedPreferencesUtil {

    private static SharedPreferences pref;
    private static final String PREF = "PREF";

    // called by SKKUVuxApp
    public static void init(Context context) {
        pref = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public static void putString(String key, String value) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getString(String key, String defaultValue) {
        return pref.getString(key, defaultValue);
    }

    public static void putInt(String key, int value) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static int getInt(String key, int defaultValue) {
        return pref.getInt(key, defaultValue);
    }
}
