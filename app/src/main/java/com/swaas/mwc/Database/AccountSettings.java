package com.swaas.mwc.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.swaas.mwc.API.Model.AccountSettingsResponse;
import com.swaas.mwc.API.Model.WhiteLabelResponse;
import com.swaas.mwc.LogTracer;

import java.util.ArrayList;
import java.util.List;

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

    public static final String WHITE_LABEL_ID = "White_Label_Id";
    public static final String ITEM_SELECTED_COLOR = "Item_Selected_Color";
    public static final String ITEM_UNSELECTED_COLOR = "Item_UnSelected_Color";
    public static final String SPLASH_SCREEN_COLOR = "Splash_Screen_Color";
    public static final String FOLDER_COLOR = "Folder_Color";

    private Context mContext;
    private DatabaseHandler dbHandler = null;
    private SQLiteDatabase database = null;
    private Retrofit retrofit;
    GetLoggedInCB getLoggedInCB;
    GetWhiteLabelCB getWhiteLabelCB;

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

    public static String CreateWhiteLabel() {
        String CREATE_WHITE_LABEL_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_WHITE_LABEL + "("
                + WHITE_LABEL_ID + "INTEGER PRIMARY KEY, " + ITEM_SELECTED_COLOR + " TEXT, " + ITEM_UNSELECTED_COLOR + " TEXT, "
                + SPLASH_SCREEN_COLOR + " TEXT, " + FOLDER_COLOR + " TEXT)";

        return CREATE_WHITE_LABEL_TABLE;
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

    public void InsertWhiteLabelDetails(WhiteLabelResponse whitelabelResponse) {
        DBConnectionOpen();

        try {
            database.delete(TABLE_WHITE_LABEL, null, null);

            ContentValues userContentValues = new ContentValues();
            userContentValues.put(ITEM_SELECTED_COLOR, whitelabelResponse.getItem_Selected_Color());
            userContentValues.put(ITEM_UNSELECTED_COLOR, whitelabelResponse.getItem_Unselected_Color());
            userContentValues.put(SPLASH_SCREEN_COLOR, whitelabelResponse.getSplash_Screen_Color());
            userContentValues.put(FOLDER_COLOR, whitelabelResponse.getFolder_Color());

            database.insert(TABLE_WHITE_LABEL, null, userContentValues);

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

    public void updateTermsAcceptedAndLoggedInStatus(String loggedInCompleteStatus, String isLocalAuthEnableStatus) {
        String query = "UPDATE tbl_Account_Settings SET Login_Complete_Status = '" + loggedInCompleteStatus + "', Is_Terms_Accepted = '" + isLocalAuthEnableStatus + "'";

        try {
            DBConnectionOpen();
            database.execSQL(query);
        } finally {
            DBConnectionClose();
        }
    }


    public void updateLocalAuthEnableAndLoggedInStatus(String loggedInCompleteStatus, String isLocalAuthEnableStatus) {
        String query = "UPDATE tbl_Account_Settings SET Login_Complete_Status = '" + loggedInCompleteStatus + "', Is_Local_Auth_Enabled = '" + isLocalAuthEnableStatus + "'";

        try {
            DBConnectionOpen();
            database.execSQL(query);
        } finally {
            DBConnectionClose();
        }
    }

    public void updateIsHelpAcceptedAndLoggedInStatus(String loggedInCompleteStatus, String isLocalAuthEnableStatus) {
        String query = "UPDATE tbl_Account_Settings SET Login_Complete_Status = '" + loggedInCompleteStatus + "', Is_Help_Accepted = '" + isLocalAuthEnableStatus + "'";

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

    public void getWhiteLabelProperties() {
        String selectQuery = "SELECT Item_Selected_Color, Item_UnSelected_Color FROM tbl_white_label";
        try {
            DBConnectionOpen();
            Cursor cursor = database.rawQuery(selectQuery, null);
            List<WhiteLabelResponse> whiteLabelResponses = getWhiteLabelPropertiesFromCursor(cursor);
            cursor.close();
            getWhiteLabelCB.getWhiteLabelSuccessCB(whiteLabelResponses);
        } catch (Throwable t) {
            getWhiteLabelCB.getWhiteLabelFailureCB(t.getMessage());
        } finally {
            DBConnectionClose();
        }
    }

    private List<WhiteLabelResponse> getWhiteLabelPropertiesFromCursor(Cursor cursor) {
        List<WhiteLabelResponse> whiteLabelResponseList = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            int itemSelectedColor = cursor.getColumnIndex(ITEM_SELECTED_COLOR);
            int itemUnSelectedColor = cursor.getColumnIndex(ITEM_UNSELECTED_COLOR);

            do {
                WhiteLabelResponse whiteLabelResponse = new WhiteLabelResponse();
                whiteLabelResponse.setItem_Selected_Color(cursor.getString(itemSelectedColor));
                whiteLabelResponse.setItem_Unselected_Color(cursor.getString(itemUnSelectedColor));

                whiteLabelResponseList.add(whiteLabelResponse);
            } while (cursor.moveToNext());
        }

        return whiteLabelResponseList;
    }


    public void getLoggedInStatusDetails() {
        String selectQuery = "SELECT * FROM tbl_Account_Settings";
        try {
            DBConnectionOpen();
            Cursor cursor = database.rawQuery(selectQuery, null);
            List<AccountSettingsResponse> accountSettingsResponseList = getLoggedInStatusFromCursor(cursor);
            cursor.close();
            getLoggedInCB.getLoggedInSuccessCB(accountSettingsResponseList);
        } catch (Throwable t) {
            getLoggedInCB.getLoggedInFailureCB(t.getMessage());
        } finally {
            DBConnectionClose();
        }
    }

    private List<AccountSettingsResponse> getLoggedInStatusFromCursor(Cursor cursor) {
        List<AccountSettingsResponse> accountSettingsResponseList = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            int userId = cursor.getColumnIndex(USER_ID);
            int userName = cursor.getColumnIndex(USER_NAME);
            int emailId = cursor.getColumnIndex(EMAIL_ID);
            int companyName = cursor.getColumnIndex(COMPANY_NAME);
            int accessToken = cursor.getColumnIndex(ACCESS_TOKEN);
            int helpGuideURL = cursor.getColumnIndex(HELP_GUIDE_URL);
            int termsURL = cursor.getColumnIndex(TERMS_URL);
            int isTermsAccepted = cursor.getColumnIndex(IS_TERMS_ACCEPTED);
            int ishelpAccepted = cursor.getColumnIndex(IS_HELP_ACCEPTED);
            int loginCompStatus = cursor.getColumnIndex(LOGIN_COMPLETE_STATUS);
            int isLocalAuthEnabled = cursor.getColumnIndex(IS_LOCAL_AUTH_ENABLED);
            int isPushNotificationEnabled = cursor.getColumnIndex(IS_PUSH_NOTIFICATION_ENABLED);

            do {
                AccountSettingsResponse accountSettingsResponse = new AccountSettingsResponse();
                accountSettingsResponse.setUser_Id(cursor.getString(userId));
                accountSettingsResponse.setUser_Name(cursor.getString(userName));
                accountSettingsResponse.setEmail_id(cursor.getString(emailId));
                accountSettingsResponse.setCompany_Name(cursor.getString(companyName));
                accountSettingsResponse.setAccess_Token(cursor.getString(accessToken));
                accountSettingsResponse.setHelp_Guide_URL(cursor.getString(helpGuideURL));
                accountSettingsResponse.setTerms_URL(cursor.getString(termsURL));
                accountSettingsResponse.setIs_Terms_Accepted(cursor.getString(isTermsAccepted));
                accountSettingsResponse.setIs_Help_Accepted(cursor.getString(ishelpAccepted));
                accountSettingsResponse.setLogin_Complete_Status(cursor.getString(loginCompStatus));
                accountSettingsResponse.setIs_Local_Auth_Enabled(cursor.getString(isLocalAuthEnabled));
                accountSettingsResponse.setIs_Push_Notification_Enabled(cursor.getString(isPushNotificationEnabled));

                accountSettingsResponseList.add(accountSettingsResponse);
            } while (cursor.moveToNext());
        }

        return accountSettingsResponseList;
    }

    public interface GetLoggedInCB {
        void getLoggedInSuccessCB(List<AccountSettingsResponse> accountSettingsResponses);
        void getLoggedInFailureCB(String message);
    }

    public void SetLoggedInCB(GetLoggedInCB getLoggedInCB) {
        this.getLoggedInCB = getLoggedInCB;
    }

    public interface GetWhiteLabelCB {
        void getWhiteLabelSuccessCB(List<WhiteLabelResponse> whiteLabelResponses);
        void getWhiteLabelFailureCB(String message);
    }

    public void SetWhiteLabelCB(GetWhiteLabelCB getWhiteLabelCB) {
        this.getWhiteLabelCB = getWhiteLabelCB;
    }
}
