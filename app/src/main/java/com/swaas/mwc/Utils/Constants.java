package com.swaas.mwc.Utils;

/**
 * Created by harika on 12-06-2018.
 */

public class Constants {

    // ***************************** Testing ********************************************
       //public static final String COMPANY_BASE_URL = "http://172.16.40.50";

    public static final String COMPANY_BASE_URL = "https://uatapi.mywealthcloud.com";


    // ***************************** Production ********************************************
     // public static final String COMPANY_BASE_URL = ""; // Prod

    public static final String EMAIL = "email";
    public static final String MOBILE = "mobile";

    public static final String USERNAME = "user_name";
    public static final String PASSWORD = "password";
    public static final String WELCOME_MSG = "welcome_msg";
    public static final String ACCESSTOKEN = "access-token";
    public static final String SETTERMS = "set-terms";
    public static final String IS_FROM_LOGIN = "is-from-login";
    public static final String IS_FROM_FTL = "is-from-ftl";
    public static final String SETTERMSPAGECONTENTURL = "set-terms-page-content-url";
    public static final String SETASSISTANCEPOPUPCONTENTURL = "set-assistance-popup-url";
    public static final String DOCUMENTPDFURL = "document-pdf-url";
    public static final String TEXT_BACKGROUND_COLOR = "text_background_color";
    public static final String TEXT_FOREGROUND_COLOR = "text_foreground_color";
    public static final String APP_BACKGROUND_COLOR = "app_background_color";
    public static final String TOOGLEGRID = "toogle-grid";
    public static final String SORT_BY_NAME = "sort_by_name";
    public static final String SORT_BY_NEWEST = "sort_by_newest";
    public static final String SORT_BY_SIZE = "sort_by_size";
    public static final String SORT_BY_DATE = "sort_by_date";
    public static final String SHAREDMSOBJ = "obj";
    public static final String OBJ = "obj";

    //Login Status
    public static final int Login_Not_Completed = 0;
    public static final int Login_Completed = 1;
    public static final int Local_Auth_Completed = 2;
    public static final int Push_Notification_Completed = 3;
    public static final int GDPR_Completed = 4;
    public static final int Assistance_Popup_Completed = 5;
    public static final int All_Settings_Completed = 6;
    public static final int Empty_Status = 90;

    public static final int REQUEST_ACCESS_NOTIFICATION_POLICY = 231;
}
