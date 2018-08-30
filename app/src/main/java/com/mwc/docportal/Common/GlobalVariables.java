package com.mwc.docportal.Common;

import com.mwc.docportal.API.Model.GetCategoryDocumentsResponse;

import java.util.ArrayList;
import java.util.List;

public class GlobalVariables
{
    public static boolean isTileView = true;
    public static String sortType = "type";
    public static boolean isAscending = true;
    public static boolean isMoveInitiated = false;
    public static String selectedActionName = "";
    public static List<GetCategoryDocumentsResponse> selectedDocumentsList = new ArrayList<>();

    public static boolean refreshDMS = false;

}
