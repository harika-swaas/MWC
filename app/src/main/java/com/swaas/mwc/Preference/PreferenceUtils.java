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
    private static final String USER_PIN_DEVICE_ID = "userPinDeviceId";
    private static final String PIN = "pin";
    private static final String MOBILE_ITEM_ENABLE_COLOR = "mobileItemEnableColor";
    private static final String MOBILE_ITEM_DISABLE_COLOR = "mobileItemDisableColor";


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

    public static void setUserPinDeviceId(Context context, String userPinDeviceId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_PIN_DEVICE_ID, userPinDeviceId);
        editor.commit();
    }

    public static String getUserPinDeviceId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        String userPinDeviceId = sharedPreferences.getString(USER_PIN_DEVICE_ID, null);
        return userPinDeviceId;
    }

    public static void setPin(Context context, int userPin) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PIN, userPin);
        editor.commit();
    }

    public static int getPin(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        int userPin = sharedPreferences.getInt(PIN, -1);
        return userPin;
    }

    public static void setMobileItemEnableColor(Context context, String mobileItemEnableColor) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MOBILE_ITEM_ENABLE_COLOR, mobileItemEnableColor);
        editor.commit();
    }

    public static String getMobileItemEnableColor(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        String mobileItemEnableColor = sharedPreferences.getString(MOBILE_ITEM_ENABLE_COLOR, null);
        return mobileItemEnableColor;
    }

    public static void setMobileItemDisableColor(Context context, String mobileItemDisableColor) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MOBILE_ITEM_DISABLE_COLOR, mobileItemDisableColor);
        editor.commit();
    }

    public static String getMobileItemDisableColor(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        String mobileItemDisableColor = sharedPreferences.getString(MOBILE_ITEM_DISABLE_COLOR, null);
        return mobileItemDisableColor;
    }
}
