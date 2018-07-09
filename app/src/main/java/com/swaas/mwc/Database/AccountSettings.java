package com.swaas.mwc.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.swaas.mwc.API.Model.AccountSettingsResponse;
import com.swaas.mwc.LogTracer;

import retrofit.Retrofit;

/**
 * Created by harika on 09-07-2018.
 */

public class AccountSettings {

    private static final LogTracer LOG_TRACER = LogTracer.instance(AccountSettings.class);

    public static final String TABLE_WHITE_LABEL = "tbl_white_label";
    public static final String TABLE_ACCOUNT_SETTINGS = "tbl_Account_Settings";

    public static final String ACCOUNT_SETTINGS_ID = "Account_Settings_Id";
    public static final String USER_ID = "User_Id";
    public static final String USER_NAME = "User_Name";
    public static final String EMAIL_ID = "Email_Id";
    public static final String COMPANY_NAME = "Company_Name";
    public static final String ACCESS_TOKEN = "Access_Token";
    public static final String HELP_GUIDE_URL = "Help_Guide_URL";
    public static final String TERMS_URL = "Terms_URL";
    public static final String IS_TERMS_ACCEPTED = "Is_Terms_Accepted";
    public static final String IS_HELP_ACCEPTED = "Is_Help_Accepted";
    public static final String LOGIN_COMPLETE_STATUS = "Login_Complete_Status";
    public static final String IS_LOCAL_AUTH_ENABLED = "Is_Local_Auth_Enabled";
    public static final String IS_PUSH_NOTIFICATION_ENABLED = "Is_Push_Notification_Enabled";

    private Context mContext;
    private DatabaseHandler dbHandler = null;
    private SQLiteDatabase database = null;
    private Retrofit retrofit;

    public AccountSettings(Context context) {
        dbHandler = new DatabaseHandler(context);
        mContext = context;
    }

    public void DBConnectionOpen() {
        database = dbHandler.getWritableDatabase();
    }

    public void DBConnectionClose() {
        if (database.isOpen()) {
            database.close();
        }
    }

    // Create Static Method.
    public static String CreateAccountSettings() {
        String CREATE_ACCOUNT_SETTINGS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_ACCOUNT_SETTINGS + "("
                + ACCOUNT_SETTINGS_ID + "INTEGER PRIMARY KEY, " + USER_ID + " TEXT, " + USER_NAME + " TEXT, "
                + EMAIL_ID + " TEXT, " + COMPANY_NAME + " TEXT, " + ACCESS_TOKEN + " TEXT, " + HELP_GUIDE_URL + " TEXT, " + TERMS_URL + " TEXT, "
                + IS_TERMS_ACCEPTED + " TEXT, " + IS_HELP_ACCEPTED + " TEXT, " + LOGIN_COMPLETE_STATUS + " TEXT, " + IS_LOCAL_AUTH_ENABLED + " TEXT, " + IS_PUSH_NOTIFICATION_ENABLED + " TEXT)";

        return CREATE_ACCOUNT_SETTINGS_TABLE;
    }

    // Insert.
    public void InsertAccountSettings(AccountSettingsResponse accountSettings) {
        DBConnectionOpen();

        try {
            database.delete(TABLE_ACCOUNT_SETTINGS, null, null);

            ContentValues userContentValues = new ContentValues();
            userContentValues.put(USER_ID, accountSettings.getUser_Id());
            userContentValues.put(USER_NAME, accountSettings.getUser_Name());
            userContentValues.put(EMAIL_ID, accountSettings.getEmail_id());
            userContentValues.put(COMPANY_NAME, accountSettings.getCompany_Name());
            userContentValues.put(ACCESS_TOKEN, accountSettings.getAccess_Token());
            userContentValues.put(HELP_GUIDE_URL, accountSettings.getHelp_Guide_URL());
            userContentValues.put(TERMS_URL, accountSettings.getTerms_URL());
            userContentValues.put(IS_TERMS_ACCEPTED, accountSettings.getIs_Terms_Accepted());
            userContentValues.put(IS_HELP_ACCEPTED, accountSettings.getIs_Help_Accepted());
            userContentValues.put(LOGIN_COMPLETE_STATUS, accountSettings.getLogin_Complete_Status());
            userContentValues.put(IS_LOCAL_AUTH_ENABLED, accountSettings.getIs_Local_Auth_Enabled());
            userContentValues.put(IS_PUSH_NOTIFICATION_ENABLED, accountSettings.getIs_Push_Notification_Enabled());

            database.insert(TABLE_ACCOUNT_SETTINGS, null, userContentValues);

        } catch (Exception e) {
            LOG_TRACER.e(e);

        } finally {
            DBConnectionClose();
        }
    }

    /*public long Delete() {
        try {
            DBConnectionOpen();
            long result = database.delete(TABLE_ACCOUNT_SETTINGS, "", new String[]{});
            DBConnectionClose();
            return result;
        } catch (SQLException ex) {
            return -1;
        }
    }*/

    public void updateLocalAuthEnableAndLoggedInStatus(String loggedInCompleteStatus, String isLocalAuthEnableStatus) {
        String query = "UPDATE tbl_Account_Settings SET Login_Complete_Status = '" + loggedInCompleteStatus + "', Is_Local_Auth_Enabled = '" + isLocalAuthEnableStatus + "'";

        try {
            DBConnectionOpen();
            database.execSQL(query);
        } finally {
            DBConnectionClose();
        }
    }

    public void updateLocalAuthEnableStatus(String loggedInCompleteStatus) {
        String query = "UPDATE tbl_Account_Settings SET Login_Complete_Status = '" + loggedInCompleteStatus + "'";

        try {
            DBConnectionOpen();
            database.execSQL(query);
        } finally {
            DBConnectionClose();
        }
    }

    public void updatePushNotificationEnableAndLoggedInStatus(String loggedInCompleteStatus, String isPushNotificationEnableStatus) {
        String query = "UPDATE tbl_Account_Settings SET Login_Complete_Status = '" + loggedInCompleteStatus + "', Is_Push_Notification_Enabled = '" + isPushNotificationEnableStatus + "'";

        try {
            DBConnectionOpen();
            database.execSQL(query);
        } finally {
            DBConnectionClose();
        }
    }

    public void updatePushNotificationEnableStatus(String loggedInCompleteStatus) {
        String query = "UPDATE tbl_Account_Settings SET Login_Complete_Status = '" + loggedInCompleteStatus + "'";

        try {
            DBConnectionOpen();
            database.execSQL(query);
        } finally {
            DBConnectionClose();
        }
    }
}
