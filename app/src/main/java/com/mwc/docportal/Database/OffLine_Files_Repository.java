package com.mwc.docportal.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mwc.docportal.API.Model.AccountSettingsResponse;
import com.mwc.docportal.API.Model.OfflineFiles;
import com.mwc.docportal.API.Model.WhiteLabelResponse;
import com.mwc.docportal.LogTracer;

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

    public boolean checkAlreadyDocumentAvailableOrNot(String document_version_id)
    {
        try {

            String query = "Select documentVersionId from tbl_Offline_Files Where documentVersionId = '"+document_version_id+"'";
            DBConnectionOpen();
            Cursor cursor = database.rawQuery(query, null);

            if (cursor.getCount() > 0)
            {
                cursor.close();
                return false;
            }
            else {
                cursor.close();
                return true;
            }

        } finally {
            DBConnectionClose();
        }
    }

    public void deleteAlreadydownloadedFile(String document_version_id)
    {
        String stringQuery = "DELETE FROM tbl_Offline_Files WHERE documentVersionId = '"+document_version_id+"'";
        try {
            DBConnectionOpen();
            database.execSQL(stringQuery);
        } catch (Exception e) {
            LOG_TRACER.e(e);
        } finally {
            DBConnectionClose();
        }
    }


    public void deleteAlreadydownloadedFileBasedUPonCondition(String document_version_id, String objectId)
    {
        String stringQuery = "DELETE FROM tbl_Offline_Files WHERE documentId = '"+objectId+"' AND documentVersionId <> '"+document_version_id+"'";
        try {
            DBConnectionOpen();
            database.execSQL(stringQuery);
        } catch (Exception e) {
            LOG_TRACER.e(e);
        } finally {
            DBConnectionClose();
        }
    }

    public void deleteAlreadydownloadedFileBasedOnVersionId(String objectId)
    {
        String stringQuery = "DELETE FROM tbl_Offline_Files WHERE documentId = '"+objectId+"'";
        try {
            DBConnectionOpen();
            database.execSQL(stringQuery);
        } catch (Exception e) {
            LOG_TRACER.e(e);
        } finally {
            DBConnectionClose();
        }
    }


    public String getFilePathFromLocalTable(String document_version_id)
    {

        String filePath = "";
        try {
            DBConnectionOpen();
            String selectQuery = "SELECT filePath FROM tbl_Offline_Files WHERE documentVersionId = '"+document_version_id+"' ";
            Cursor cursor = database.rawQuery(selectQuery, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                filePath = cursor.getString(cursor.getColumnIndex("filePath"));
            }
            cursor.close();
        } finally {
            DBConnectionClose();
        }
        return filePath;
    }

    public List<OfflineFiles> getFilePathFromLocalTableBasedUponCondition(String document_version_id, String objectid)
    {

        List<OfflineFiles> offlineFilesList = new ArrayList<>();
        String selectQuery = "SELECT * FROM tbl_Offline_Files WHERE documentId = '"+objectid+"' AND documentVersionId <> '"+document_version_id+"'";

        try {
            DBConnectionOpen();
            Cursor mcursor = database.rawQuery(selectQuery, null);
            offlineFilesList = GetOfflinefilesPathFromCursor(mcursor);
            mcursor.close();

        } finally {
            DBConnectionClose();
        }
        return offlineFilesList;
    }

    private List<OfflineFiles> GetOfflinefilesPathFromCursor(Cursor mcursor)
    {
        List<OfflineFiles> offlineFilesList = new ArrayList<>();

        if (mcursor != null && mcursor.getCount() > 0) {
            mcursor.moveToFirst();

            int filepath = mcursor.getColumnIndex(FILEPATH);
            int documentId = mcursor.getColumnIndex(DOCUMENTID);
            int documentVersionId = mcursor.getColumnIndex(DOCUMENTVERSIONID);
            int documentName = mcursor.getColumnIndex(DOCUMENTNAME);


            do {
                OfflineFiles offlineFilesModel = new OfflineFiles();
                offlineFilesModel.setFilePath(mcursor.getString(filepath));
                offlineFilesModel.setDocumentId(mcursor.getString(documentId));
                offlineFilesModel.setDocumentVersionId(mcursor.getString(documentVersionId));
                offlineFilesModel.setDocumentName(mcursor.getString(documentName));
                offlineFilesList.add(offlineFilesModel);

            } while (mcursor.moveToNext());

        }
        return offlineFilesList;
    }


    public List<OfflineFiles> getFilePathFromLocalTableBasedOnVersionId(String objectId)
    {

        List<OfflineFiles> offlineFilesList = new ArrayList<>();
        String selectQuery = "SELECT * FROM tbl_Offline_Files WHERE documentId = '"+objectId+"'";
        try {
            DBConnectionOpen();

            Cursor mcursor = database.rawQuery(selectQuery, null);
            offlineFilesList = GetOfflinefilesPathFromCursor(mcursor);
            mcursor.close();
        } finally {
            DBConnectionClose();
        }
        return offlineFilesList;
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
