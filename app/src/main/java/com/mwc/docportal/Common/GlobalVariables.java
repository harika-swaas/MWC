package com.mwc.docportal.Common;

import com.mwc.docportal.API.Model.GetCategoryDocumentsResponse;
import com.mwc.docportal.API.Model.UploadModel;
import com.mwc.docportal.Utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class GlobalVariables
{
    public static boolean isTileView = true;
    public static String sortType = Constants.NO_SORTING_TEXT;
    public static boolean isAscending = true;
    public static boolean isMoveInitiated = false;
    public static String selectedActionName = "";
    public static List<GetCategoryDocumentsResponse> selectedDocumentsList = new ArrayList<>();
    public static boolean refreshDMS = false;
    public static String searchKey = "";
    public static List<GetCategoryDocumentsResponse> globalSearchDocumentList = new ArrayList<>();
    public static boolean isGlobalSearchCompleted = false;
    public static List<GetCategoryDocumentsResponse> sharedDocumentList = new ArrayList<>();
    public static boolean isSharedTileView = true;
    public static boolean isSharedMoveInitiated = false;
    public static String sharedDocsSortType = "type";
    public static boolean sharedDocsIsAscending = true;
    public static boolean isMultiSelect = false;
    public static int selectedCountValue = 0;
    public static int activityCount = 0;
    public static int moveOriginIndex = 0;
    public static int activityFinishCount = 0;
    public static boolean refreshPage = false;
    public static boolean refreshSharedDocumentPage = false;
    public static int totalUnreadableCount = 0;
    public static List<GetCategoryDocumentsResponse> sharedRootDocumentList = new ArrayList<>();
    public static List<UploadModel> otherAppDocumentList = new ArrayList<>();
}
