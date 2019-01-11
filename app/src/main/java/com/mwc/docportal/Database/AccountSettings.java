package com.mwc.docportal.Database;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.mwc.docportal.API.Model.AccountSettingsResponse;
import com.mwc.docportal.API.Model.WhiteLabelResponse;
import com.mwc.docportal.Common.GlobalVariables;
import com.mwc.docportal.LogTracer;
import com.mwc.docportal.Login.LoginActivity;
import com.mwc.docportal.Utils.Constants;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Retrofit;

/**
 * Created by harika on 09-07-2018.
 */

public class AccountSettings {

    private static final String MWC = "MWC";
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
        String selectQuery = "SELECT * FROM tbl_white_label";
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
            int splashSplashScreenColor = cursor.getColumnIndex(SPLASH_SCREEN_COLOR);
            int folderColor = cursor.getColumnIndex(FOLDER_COLOR);

            do {
                WhiteLabelResponse whiteLabelResponse = new WhiteLabelResponse();
                whiteLabelResponse.setItem_Selected_Color(cursor.getString(itemSelectedColor));
                whiteLabelResponse.setItem_Unselected_Color(cursor.getString(itemUnSelectedColor));
                whiteLabelResponse.setSplash_Screen_Color(cursor.getString(splashSplashScreenColor));
                whiteLabelResponse.setFolder_Color(cursor.getString(folderColor));
               /* whiteLabelResponse.setItem_Selected_Color("#007BF5");
                whiteLabelResponse.setItem_Unselected_Color("#007BF5");
                whiteLabelResponse.setSplash_Screen_Color("#007BF5");
                whiteLabelResponse.setFolder_Color("#007BF5");*/

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

   /* public void DeleteAllDatabaseTables()
    {
        DBConnectionOpen();
        ArrayList<String> arrTblNames = new ArrayList<String>();
        Cursor cursor = database.rawQuery("SELECT name FROM sqlite_master WHERE type='table' and name <> 'sqlite_sequence'", null);
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    arrTblNames.add(cursor.getString(cursor.getColumnIndex("name")));
                    cursor.moveToNext();
                }
            }

            if(arrTblNames != null && arrTblNames.size() > 0){
                for(String tableName: arrTblNames){
                    deleteDatabaseTables(tableName);
                }
            }

        }

        dbHandler.onCreate(database);

        DBConnectionClose();
    }

    private void deleteDatabaseTables(String tableName)
    {
        if(!tableName.equalsIgnoreCase("tbl_pushNotificationSettings"))
        {
            database.execSQL("drop table "+ tableName);
        }

    }
*/


    public void deleteAllTables()
    {
        DBConnectionOpen();

        database.delete("tbl_white_label",null,null);
        database.delete("tbl_Account_Settings",null,null);
        database.delete("tbl_Offline_Files",null,null);
        DBConnectionClose();

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

    public void UpdatePushNotificatoinSettings(String register_type)
    {
        String query = "UPDATE tbl_Account_Settings SET Is_Push_Notification_Enabled = '" + register_type + "'";

        try {
            DBConnectionOpen();
            database.execSQL(query);
        } finally {
            DBConnectionClose();
        }
    }

    public void UpdateFingerPrintSettings(String opt_value)
    {
        String updated_opt_value = "";
        if(opt_value.equalsIgnoreCase("opt-in"))
        {
            updated_opt_value = "1";
        }
        else  if(opt_value.equalsIgnoreCase("opt-out"))
        {
            updated_opt_value = "0";
        }


        String query = "UPDATE tbl_Account_Settings SET Is_Local_Auth_Enabled = '" + updated_opt_value + "'";

        try {
            DBConnectionOpen();
            database.execSQL(query);
        } finally {
            DBConnectionClose();
        }

    }


    public String getPushNotificatonStatusFromTable()
    {
        String pushNotificationEnabled = "";
        try {
            DBConnectionOpen();
            String selectQuery = "SELECT Is_Push_Notification_Enabled FROM tbl_Account_Settings";
            Cursor cursor = database.rawQuery(selectQuery, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                pushNotificationEnabled = cursor.getString(cursor.getColumnIndex("Is_Push_Notification_Enabled"));
            }
            cursor.close();
        } finally {
            DBConnectionClose();
        }
        return pushNotificationEnabled;
    }

    public String getCompanyName()
    {
        String companyName = "";
        try {
            DBConnectionOpen();
            String selectQuery = "SELECT Company_Name FROM tbl_Account_Settings";
            Cursor cursor = database.rawQuery(selectQuery, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                companyName = cursor.getString(cursor.getColumnIndex("Company_Name"));
            }
            cursor.close();
        } finally {
            DBConnectionClose();
        }
        return companyName;
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

    public void deleteAll() {
        try {
            DBConnectionOpen();
            database.delete(TABLE_ACCOUNT_SETTINGS,null,null);
            database.delete(TABLE_WHITE_LABEL,null,null);
            database.delete("tbl_Offline_Files", null, null);
            SharedPreferences share_settings = mContext.getSharedPreferences(MWC, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = share_settings.edit();
            editor.clear();
            editor.commit();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            DBConnectionClose();
        }
    }


    public void LogouData()
    {
        deleteAllTables();
        SharedPreferences share_settings = mContext.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = share_settings.edit();
        editor.clear();
        editor.commit();

          File dir = new File(Environment.getExternalStorageDirectory() + "/"+Constants.Folder_Name);
          deleteRecursive(dir);

        // clear Global variables Data

        GlobalVariables.isTileView = true;
        GlobalVariables.sortType = Constants.NO_SORTING_TEXT;
        GlobalVariables.isAscending = true;
        GlobalVariables.isMoveInitiated = false;
        GlobalVariables.selectedActionName = "";
        GlobalVariables.selectedDocumentsList.clear();
        GlobalVariables.refreshDMS = false;
        GlobalVariables.searchKey = "";
        GlobalVariables.globalSearchDocumentList.clear();
        GlobalVariables.isGlobalSearchCompleted = false;
        GlobalVariables.sharedDocumentList.clear();
        GlobalVariables.isSharedTileView = true;
        GlobalVariables.sharedDocsSortType = "type";
        GlobalVariables.sharedDocsIsAscending = true;
        GlobalVariables.isMultiSelect = false;
        GlobalVariables.selectedCountValue = 0;
        GlobalVariables.activityCount = 0;
        GlobalVariables.moveOriginIndex = 0;
        GlobalVariables.activityFinishCount = 0;
        GlobalVariables.refreshPage = false;
        GlobalVariables.refreshSharedDocumentPage = false;
        GlobalVariables.totalUnreadableCount = 0;
        GlobalVariables.sharedRootDocumentList.clear();
        GlobalVariables.otherAppDocumentList.clear();

        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        mContext.startActivity(intent);

    }

    public void deleteRecursive(File dir) {

        if (dir.isDirectory()) {
            if (dir.listFiles() != null && dir.listFiles().length > 0) {
                for (File child : dir.listFiles()) {
                    if (child != null) {
                        deleteRecursive(child);
                    }

                }
            }

        }
        // if(dir.isFile())
        dir.delete();

    }

}
