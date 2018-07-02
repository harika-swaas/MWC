package com.swaas.mwc.Preference;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by harika on 22-06-2018.
 */

public class PreferenceUtils {

    private static final String MWC = "MWC";
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String SET_TERMS_URL = "setTermsURL";

    public static void setAccessToken(Context context, String accesstoken) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ACCESS_TOKEN, accesstoken);
        editor.commit();
    }

    public static String getAccessToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        String accessToken = sharedPreferences.getString(ACCESS_TOKEN, null);
        return accessToken;
    }

    public static void setTermsURL(Context context, String settermsURL) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SET_TERMS_URL, settermsURL);
        editor.commit();
    }

    public static String getTermsURL(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        String settermsURL = sharedPreferences.getString(SET_TERMS_URL, null);
        return settermsURL;
    }
}
