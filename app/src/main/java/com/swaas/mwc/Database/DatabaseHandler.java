package com.swaas.mwc.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by harika on 09-07-2018.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String LOG = DatabaseHandler.class.getName();

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "DOC_PORTAL";

    public Context mContext;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(AccountSettings.CreateAccountSettings());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
}
