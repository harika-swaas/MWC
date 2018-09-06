package com.mwc.docportal.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mwc.docportal.LogTracer;
import com.mwc.docportal.Preference.PreferenceUtils;

import retrofit.Retrofit;

public class PushNotificatoinSettings_Respository
{
    private static final LogTracer LOG_TRACER = LogTracer.instance(PushNotificatoinSettings_Respository.class);


    public static final String TABLE_PUSHNOTIFICATIONSETTINGS = "tbl_pushNotificationSettings";

    public static final String DEVICETOKEN = "deviceToken";
    public static final String ISPNENABLED = "isPNEnabled";
    public static final String LOGINSOURCE = "loginSource";

    private Context mContext;
    private DatabaseHandler dbHandler = null;
    private SQLiteDatabase database = null;
    private Retrofit retrofit;

    public PushNotificatoinSettings_Respository(Context context) {
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

    public static String Create_PushNotification_Settings() {

        String CREATE_PUSH_NOTIFICATON_SETTINGS = "CREATE TABLE IF NOT EXISTS " + TABLE_PUSHNOTIFICATIONSETTINGS + "("
                 + DEVICETOKEN + " TEXT, " + ISPNENABLED + " TEXT, "
                + LOGINSOURCE + " TEXT)";

        return CREATE_PUSH_NOTIFICATON_SETTINGS;
    }

    public int getCountOfPushNotificationSettings()
    {
        int rowCount = 0;
        try {

            String query = "Select * from tbl_pushNotificationSettings";
            DBConnectionOpen();
            Cursor cursor = database.rawQuery(query, null);

            rowCount = cursor.getCount();

        } finally {
            DBConnectionClose();
        }
        return rowCount;
    }

    public void insertIntoPushNotificatonTable(String deviceToken)
    {
        DBConnectionOpen();
       // String mobileDeviceToken = PreferenceUtils.getMobileDeviceToken(mContext);

        try {

            ContentValues deviceContentValues = new ContentValues();
            deviceContentValues.put(DEVICETOKEN, deviceToken);
            /*deviceContentValues.put(ISPNENABLED, allowStatus);
            deviceContentValues.put(LOGINSOURCE, loginSource);*/
            database.insert(TABLE_PUSHNOTIFICATIONSETTINGS, null, deviceContentValues);

        } catch (Exception e) {
            LOG_TRACER.e(e);

        } finally {
            DBConnectionClose();
        }
    }

    public String getPushNotificatonSettingsStatus()
    {
        String pushNotificationStatus = "";
        try {
            DBConnectionOpen();
            String selectQuery = "SELECT isPNEnabled FROM tbl_pushNotificationSettings";
            Cursor cursor = database.rawQuery(selectQuery, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                pushNotificationStatus = cursor.getString(cursor.getColumnIndex("isPNEnabled"));
            }
            cursor.close();
        } finally {
            DBConnectionClose();
        }
        return pushNotificationStatus;
    }

    public void updatePushNotificatoinStatus(String register_type)
    {
        String query = "UPDATE tbl_pushNotificationSettings SET isPNEnabled = '" + register_type + "'";

        try {
            DBConnectionOpen();
            database.execSQL(query);
        } finally {
            DBConnectionClose();
        }

    }


    public String getDeviceTokenFromTableStatus()
    {
        String deviceToken = "";
        try {
            DBConnectionOpen();
            String selectQuery = "SELECT deviceToken FROM tbl_pushNotificationSettings";
            Cursor cursor = database.rawQuery(selectQuery, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                deviceToken = cursor.getString(cursor.getColumnIndex("deviceToken"));
            }
            cursor.close();
        } finally {
            DBConnectionClose();
        }
        return deviceToken;
    }





}
