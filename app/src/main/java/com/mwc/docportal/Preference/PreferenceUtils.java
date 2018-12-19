package com.mwc.docportal.Preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mwc.docportal.API.Model.FTLPINResponse;
import com.mwc.docportal.API.Model.GetCategoryDocumentsResponse;
import com.mwc.docportal.API.Model.LoginResponse;

import java.lang.reflect.Type;
import java.util.ArrayList;

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
    private static final String DOC_PORTAL_LOGGED_IN_OBJ = "doc_portal_logged_in_obj";
    private static final String DOCUMENT_VERSION_ID = "document_version_id";
    private static final String DOCUMENT_ID = "document_id";
    private static final int TERMS_ACCEPT = 1;
    private static final String OBJECT_ID = "object_id";
    private static  final String NOTES_ID = "notes_id";
    private static  final String CATEGORY_ID = "category_id";
    private static  final String WORKSPACE_ID = "workspace_id";
    private static  final String PARENT_ID = "parent_id";
    private static final String DOC_ID="doc_id";
    private static final String UPLOAD_LIST="upload_list";
    private static final String BACK_BUTTON_LIST="back_button_list";
    private static final String DELETE="delete";
    private static final String ASSIST = "assist";
    private static final int BACK = 0;
    private static final String FILE_FORMATS="file_formats";
    private static final String MAX_SIZE_UPLOAD="max_size_upload";

    private static final String PUSH_NOTIFICATION_DOCUMENT_VERSION_ID = "push_notification_document_version_id";
    private static final String PUSH_NOTIFICATION_DOCUMENT_SHARE = "push_notification_document_share";

    private static final String LOGO_IMAGE_PATH = "logo_image_path";
    private static final String DOCUMENT_NAME = "document_Name";
    private static final String SHARETYPE_DOCUMENTVERSIONID = "shareType_documentVersionId";
    private static final String ROOT_WORKSPACEID = "root_WorkSpaceId";

    // Logo files download
    private static final String SPLASH_LOGO_IMAGE_PATH = "splash_logo_image_path";
    private static final String SETTINGS_LOGO_IMAGE_PATH = "settings_logo_image_path";

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

    public static void setBack(Context context, int back) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(String.valueOf(BACK), back);
        editor.commit();
    }

    public static int getBack(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        int back = sharedPreferences.getInt(String.valueOf(BACK), -1);
        return back;
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

    public static String getAssist(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ASSIST, Context.MODE_PRIVATE);
        String assist = sharedPreferences.getString(ASSIST, null);
        return assist;
    }

    public static void setAssist(Context context, String assist) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ASSIST, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ASSIST, assist);
        editor.commit();
    }

    public static String getMobileItemDisableColor(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        String mobileItemDisableColor = sharedPreferences.getString(MOBILE_ITEM_DISABLE_COLOR, null);
        return mobileItemDisableColor;
    }


    public static String getDocPortalLoggedInObj(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        String loggedInString = sharedPreferences.getString("loggedInObj", null);
        return loggedInString;
    }

    public static void setDocPortalLoggedInObj(Context context, LoginResponse loginResponse) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String loggedInJson = gson.toJson(loginResponse);
        editor.putString("loggedInObj", loggedInJson);
        editor.commit();
    }

    public static int get_terms_accept(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        int terms_accept = sharedPreferences.getInt(null, TERMS_ACCEPT);
        return terms_accept;
    }

    public static void setDocPortalFTLLoggedObj(Context context, FTLPINResponse ftlpinResponse) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String loggedInJson = gson.toJson(ftlpinResponse);
        editor.putString("loggedFTLObj", loggedInJson);
        editor.commit();
    }

    public static String getDocPortalFTLLoggedObj(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        String loggedInString = sharedPreferences.getString("loggedFTLObj", null);
        return loggedInString;
    }

    public static void setDocumentVersionId(Context context, String documentVersionId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DOCUMENT_VERSION_ID, documentVersionId);
        editor.commit();
    }

    public static String getDocumentVersionId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        String documentVersionId = sharedPreferences.getString(DOCUMENT_VERSION_ID, null);
        return documentVersionId;
    }

    public static void setDocument_Id(Context context, String document_id) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DOCUMENT_ID, document_id);
        editor.commit();
    }

    public static String getDocument_Id(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        String document_id = sharedPreferences.getString(DOCUMENT_ID, null);
        return document_id;
    }

    public static void setObjectId(Context context, String objectId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(OBJECT_ID, objectId);
        editor.commit();
    }

    public static String getObjectId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        String objectId = sharedPreferences.getString(OBJECT_ID, "");
        return objectId;
    }

    public static void setNotesId(Context context, String notes_id) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(NOTES_ID, notes_id);
        editor.commit();
    }

    public static String getNotesId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        String notes_id = sharedPreferences.getString(NOTES_ID, null);
        return notes_id;
    }

    public static void setCategoryId(Context context, String category_id) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CATEGORY_ID, category_id);
        editor.commit();
    }

    public static String getCategoryId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        String category_id = sharedPreferences.getString(CATEGORY_ID, null);
        return category_id;
    }

    public static void setWorkspaceId(Context context, String workspace_id) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(WORKSPACE_ID, workspace_id);
        editor.commit();
    }

    public static String getWorkspaceId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        String workspace_id = sharedPreferences.getString(WORKSPACE_ID, null);
        return workspace_id;
    }

    public static void setParentId(Context context, String parent_id) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PARENT_ID, parent_id);
        editor.commit();
    }

    public static String getParentId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        String workspace_id = sharedPreferences.getString(PARENT_ID, null);
        return workspace_id;
    }
    public static void saveArrayList(Context context, ArrayList<String> list, String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences(DOC_ID, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    public static ArrayList<String> getArrayList(Context context,String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences(DOC_ID, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }


    public static void setupload(Context context, ArrayList<String> list, String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences(UPLOAD_LIST, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    public static ArrayList<String> getupload(Context context,String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences(UPLOAD_LIST, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public static void setBackButtonList(Context context, ArrayList<String> list, String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences(BACK_BUTTON_LIST, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    public static ArrayList<String> getBackButtonList(Context context,String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences(BACK_BUTTON_LIST, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public static void setDelete(Context context, ArrayList<String> list, String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences(DELETE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    public static ArrayList<String> getDelete(Context context,String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences(DELETE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }


    public static void setFileFormats(Context context, ArrayList<String> list, String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_FORMATS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    public static ArrayList<String> getFileFormats(Context context,String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_FORMATS, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public static String getMaxSizeUpload(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MAX_SIZE_UPLOAD, Context.MODE_PRIVATE);
        String assist = sharedPreferences.getString(MAX_SIZE_UPLOAD, null);
        return assist;
    }

    public static void setMaxSizeUpload(Context context, String assist) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MAX_SIZE_UPLOAD, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MAX_SIZE_UPLOAD, assist);
        editor.commit();
    }




    public static void setPushNotificationDocumentVersionId(Context context, String accesstoken) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PUSH_NOTIFICATION_DOCUMENT_VERSION_ID, accesstoken);
        editor.commit();
    }

    public static String getPushNotificationDocumentVersionId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        String pushNotificationDocumentId = sharedPreferences.getString(PUSH_NOTIFICATION_DOCUMENT_VERSION_ID, null);
        return pushNotificationDocumentId;
    }


    public static void setPushNotificationDocumentShare(Context context, String documentShare) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PUSH_NOTIFICATION_DOCUMENT_SHARE, documentShare);
        editor.commit();
    }

    public static String getPushNotificationDocumentShare(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        String pushNotificationShareType = sharedPreferences.getString(PUSH_NOTIFICATION_DOCUMENT_SHARE, null);
        return pushNotificationShareType;
    }


    public static void setLogoImagePath(Context context, String imagePath) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LOGO_IMAGE_PATH, imagePath);
        editor.commit();
    }

    public static String getLogoImagePath(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        String imagePath = sharedPreferences.getString(LOGO_IMAGE_PATH, null);
        return imagePath;
    }

    public static void setDocumentName(Context context, String documentName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DOCUMENT_NAME, documentName);
        editor.commit();
    }

    public static String getDocumentName(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        String documentName = sharedPreferences.getString(DOCUMENT_NAME, null);
        return documentName;
    }

    public static void setSharetypeDocumentversionid(Context context, String documentVersionId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHARETYPE_DOCUMENTVERSIONID, documentVersionId);
        editor.commit();
    }

    public static String getSharetypeDocumentversionid(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        String versionid = sharedPreferences.getString(SHARETYPE_DOCUMENTVERSIONID, null);
        return versionid;
    }

    public static void setRootWorkspaceid(Context context, String workSpaceId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ROOT_WORKSPACEID, workSpaceId);
        editor.commit();
    }

    public static String getRootWorkspaceid(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        String workSpaceId = sharedPreferences.getString(ROOT_WORKSPACEID, null);
        return workSpaceId;
    }

    public static void setSplashLogoImagePath(Context context, String imagePath) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SPLASH_LOGO_IMAGE_PATH, imagePath);
        editor.commit();
    }

    public static String getSplashLogoImagePath(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        String imagePath = sharedPreferences.getString(SPLASH_LOGO_IMAGE_PATH, null);
        return imagePath;
    }

    public static void setSettingsLogoImagePath(Context context, String imagePath) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SETTINGS_LOGO_IMAGE_PATH, imagePath);
        editor.commit();
    }

    public static String getSettingsLogoImagePath(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MWC, Context.MODE_PRIVATE);
        String imagePath = sharedPreferences.getString(SETTINGS_LOGO_IMAGE_PATH, null);
        return imagePath;
    }



}
