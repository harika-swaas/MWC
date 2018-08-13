package com.swaas.mwc.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.swaas.mwc.API.Model.AccountSettingsResponse;
import com.swaas.mwc.API.Model.OfflineFiles;
import com.swaas.mwc.API.Model.WhiteLabelResponse;
import com.swaas.mwc.LogTracer;

import java.util.ArrayList;
import java.util.List;

import retrofit.Retrofit;

public class OffLine_Files_Repository
{
    private static final LogTracer LOG_TRACER = LogTracer.instance(OffLine_Files_Repository.class);


    public static final String TABLE_OFFLINE_FILES = "tbl_Offline_Files";

    public static final String OFFLINE_FILE_ID = "offline_file_id";
    public static final String DOCUMENTID = "documentId";
    public static final String DOCUMENTNAME = "documentName";
    public static final String DOCUMENTVERSIONID = "documentVersionId";
    public static final String DOWNLOADDATE = "downloadDate";
    public static final String FILENAME = "filename";
    public static final String FILEPATH = "filePath";
    public static final String FILESIZE = "fileSize";
    public static final String VERSIONNUMBER = "versionNumber";
    public static final String FILETYPE = "filetype";
    public static final String SOURCE = "source";

    GetOfflineFilesListenerCB getOfflineFilesListenerCB;

    private Context mContext;
    private DatabaseHandler dbHandler = null;
    private SQLiteDatabase database = null;
    private Retrofit retrofit;

    public OffLine_Files_Repository(Context context) {
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


    public static String Create_Offline_Files() {

        String CREATE_OFFLINE_FILES = "CREATE TABLE IF NOT EXISTS " + TABLE_OFFLINE_FILES + "("
                + OFFLINE_FILE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + DOCUMENTID + " TEXT, " + DOCUMENTNAME + " TEXT, "
                + DOCUMENTVERSIONID + " TEXT, " + DOWNLOADDATE + " TEXT, " + FILENAME + " TEXT, " + FILEPATH + " TEXT, " + FILESIZE + " TEXT, " +
                FILETYPE + " TEXT, " + SOURCE + " TEXT, "
                + VERSIONNUMBER + " TEXT)";

        return CREATE_OFFLINE_FILES;
    }




    public interface GetOfflineFilesListenerCB {
        void getOfflineFilesDataSuccessCB(List<OfflineFiles> Listdata);

        void getOfflineFilesDataFailureCB(String message);
    }

    public void setForm1MasterAPIListenerCB(GetOfflineFilesListenerCB getOfflineFilesListenerCB) {
        this.getOfflineFilesListenerCB = getOfflineFilesListenerCB;
    }

    public void InsertOfflineFilesData(OfflineFiles offlineFiles) {
        DBConnectionOpen();

        try {

            ContentValues userContentValues = new ContentValues();
            userContentValues.put(DOCUMENTID, offlineFiles.getDocumentId());
            userContentValues.put(DOCUMENTNAME, offlineFiles.getDocumentName());
            userContentValues.put(DOCUMENTVERSIONID, offlineFiles.getDocumentVersionId());
            userContentValues.put(DOWNLOADDATE, offlineFiles.getDownloadDate());
            userContentValues.put(FILENAME, offlineFiles.getFilename());
            userContentValues.put(FILEPATH, offlineFiles.getFilePath());
            userContentValues.put(FILESIZE, offlineFiles.getFileSize());
            userContentValues.put(VERSIONNUMBER, offlineFiles.getVersionNumber());
            userContentValues.put(FILETYPE, offlineFiles.getFiletype());
            userContentValues.put(SOURCE, offlineFiles.getSource());
            database.insert(TABLE_OFFLINE_FILES, null, userContentValues);

        } catch (Exception e) {
            LOG_TRACER.e(e);

        } finally {
            DBConnectionClose();
        }
    }


    public void getOfflineDocumentList()
    {
        String selectQuery = "SELECT * FROM tbl_Offline_Files";
        try {
            DBConnectionOpen();
            Cursor cursor = database.rawQuery(selectQuery, null);
            List<OfflineFiles> whiteLabelResponses = getOffLineFilesFromCursor(cursor);
            cursor.close();
            getOfflineFilesListenerCB.getOfflineFilesDataSuccessCB(whiteLabelResponses);
        } catch (Throwable t) {
            getOfflineFilesListenerCB.getOfflineFilesDataFailureCB(t.getMessage());
        } finally {
            DBConnectionClose();
        }
    }

    private List<OfflineFiles> getOffLineFilesFromCursor(Cursor cursor)
    {
        List<OfflineFiles> offLineResponseList = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            int documentId = cursor.getColumnIndex(DOCUMENTID);
            int documentName = cursor.getColumnIndex(DOCUMENTNAME);
            int documentVersionId = cursor.getColumnIndex(DOCUMENTVERSIONID);
            int downloadDate = cursor.getColumnIndex(DOWNLOADDATE);
            int filePath = cursor.getColumnIndex(FILEPATH);
            int fileName = cursor.getColumnIndex(FILENAME);
            int fileSize = cursor.getColumnIndex(FILESIZE);
            int versionNumber = cursor.getColumnIndex(VERSIONNUMBER);
            int filetype = cursor.getColumnIndex(FILETYPE);
            int source = cursor.getColumnIndex(SOURCE);


            do {
                OfflineFiles offlineModel = new OfflineFiles();
                offlineModel.setDocumentId(cursor.getString(documentId));
                offlineModel.setDocumentName(cursor.getString(documentName));
                offlineModel.setDocumentVersionId(cursor.getString(documentVersionId));
                offlineModel.setDownloadDate(cursor.getString(downloadDate));
                offlineModel.setFilePath(cursor.getString(filePath));
                offlineModel.setFilename(cursor.getString(fileName));
                offlineModel.setFileSize(cursor.getString(fileSize));
                offlineModel.setVersionNumber(cursor.getString(versionNumber));
                offlineModel.setFiletype(cursor.getString(filetype));
                offlineModel.setSource(cursor.getString(source));

                offLineResponseList.add(offlineModel);

            } while (cursor.moveToNext());
        }

        return offLineResponseList;
    }

}
