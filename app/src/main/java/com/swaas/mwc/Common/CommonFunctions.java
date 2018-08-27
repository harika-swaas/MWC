package com.swaas.mwc.Common;



import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.swaas.mwc.API.Model.ColorCodeModel;
import com.swaas.mwc.API.Model.GetCategoryDocumentsResponse;
import com.swaas.mwc.API.Model.ListPinDevicesResponse;
import com.swaas.mwc.API.Model.LoginResponse;
import com.swaas.mwc.API.Model.MoveDocumentRequest;
import com.swaas.mwc.API.Service.MoveDocumentService;
import com.swaas.mwc.DMS.MyFolderActivity;
import com.swaas.mwc.DMS.MyFoldersDMSActivity;
import com.swaas.mwc.Database.AccountSettings;
import com.swaas.mwc.Dialogs.LoadingProgressDialog;
import com.swaas.mwc.Login.LoginActivity;
import com.swaas.mwc.Network.NetworkUtils;
import com.swaas.mwc.Preference.PreferenceUtils;
import com.swaas.mwc.R;

import java.io.File;
import java.util.List;

public class CommonFunctions
{
    AlertDialog mAlertDialog;
   public static ColorCodeModel getColorCodesforFileType(String fileType)
    {
        ColorCodeModel colorCodeModel = new ColorCodeModel();
        if (fileType.equalsIgnoreCase("pdf")) {
            colorCodeModel.setPrimaryColor(R.color.thumbnail_pdf_color);
            colorCodeModel.setSecondaryColor(R.color.thumbnail_pdf_corner_color);

        } else if (fileType.equalsIgnoreCase("xlsx") ||
                fileType.equalsIgnoreCase("xls") || fileType.equalsIgnoreCase("xlsm")
                || fileType.equalsIgnoreCase("csv")) {
            colorCodeModel.setPrimaryColor(R.color.thumbnail_xls_color);
            colorCodeModel.setSecondaryColor(R.color.thumbnail_xls_corner_color);

        } else if (fileType.equalsIgnoreCase("doc") ||
                fileType.equalsIgnoreCase("docx") || fileType.equalsIgnoreCase("docm")
                || fileType.equalsIgnoreCase("gdoc") || fileType.equalsIgnoreCase("keynote")) {
            colorCodeModel.setPrimaryColor(R.color.thumbnail_doc_color);
            colorCodeModel.setSecondaryColor(R.color.thumbnail_doc_corner_color);
        } else if (fileType.equalsIgnoreCase("ppt") ||
                fileType.equalsIgnoreCase("pptx") || fileType.equalsIgnoreCase("pps")
                || fileType.equalsIgnoreCase("ai")) {
            colorCodeModel.setPrimaryColor(R.color.thumbnail_ppt_color);
            colorCodeModel.setSecondaryColor(R.color.thumbnail_ppt_corner_color);
        } else if (fileType.equalsIgnoreCase("xml") ||
                fileType.equalsIgnoreCase("log") || fileType.equalsIgnoreCase("zip")
                || fileType.equalsIgnoreCase("rar") || fileType.equalsIgnoreCase("zipx")
                || fileType.equalsIgnoreCase("mht")) {

            colorCodeModel.setPrimaryColor(R.color.thumbnail_xml_color);
            colorCodeModel.setSecondaryColor(R.color.thumbnail_xml_corner_color);

        } else if (fileType.equalsIgnoreCase("xml") ||
                fileType.equalsIgnoreCase("log") || fileType.equalsIgnoreCase("rtf") ||
                fileType.equalsIgnoreCase("txt") || fileType.equalsIgnoreCase("epub")) {
            colorCodeModel.setPrimaryColor(R.color.thumbnail_xml_color);
            colorCodeModel.setSecondaryColor(R.color.thumbnail_xml_corner_color);

        } else if (fileType.equalsIgnoreCase("msg") || fileType.equalsIgnoreCase("dot") || fileType.equalsIgnoreCase("odt")
                || fileType.equalsIgnoreCase("ott")) {
            colorCodeModel.setPrimaryColor(R.color.thumbnail_msg_color);
            colorCodeModel.setSecondaryColor(R.color.thumbnail_msg_corner_color);

        } else if (fileType.equalsIgnoreCase("pages")) {
            colorCodeModel.setPrimaryColor(R.color.thumbnail_pages_color);
            colorCodeModel.setSecondaryColor(R.color.thumbnail_pages_corner_color);

        } else if (fileType.equalsIgnoreCase("pub") || fileType.equalsIgnoreCase("ods")) {
            colorCodeModel.setPrimaryColor(R.color.thumbnail_pub_color);
            colorCodeModel.setSecondaryColor(R.color.thumbnail_pub_corner_color);

        } else if (fileType.equalsIgnoreCase("gif") || fileType.equalsIgnoreCase("jpeg")
                || fileType.equalsIgnoreCase("jpg") || fileType.equalsIgnoreCase("png") || fileType.equalsIgnoreCase("bmp")
                || fileType.equalsIgnoreCase("tif") || fileType.equalsIgnoreCase("tiff") || fileType.equalsIgnoreCase("eps")
                || fileType.equalsIgnoreCase("svg") || fileType.equalsIgnoreCase("odp")
                || fileType.equalsIgnoreCase("otp")) {
            colorCodeModel.setPrimaryColor(R.color.thumbnail_gif_color);
            colorCodeModel.setSecondaryColor(R.color.thumbnail_gif_corner_color);

        } else if (fileType.equalsIgnoreCase("avi")
                || fileType.equalsIgnoreCase("flv") || fileType.equalsIgnoreCase("mpeg") ||
                fileType.equalsIgnoreCase("mpg") || fileType.equalsIgnoreCase("swf") || fileType.equalsIgnoreCase("wmv")) {

            colorCodeModel.setPrimaryColor(R.color.thumbnail_avi_color);
            colorCodeModel.setSecondaryColor(R.color.thumbnail_avi_corner_color);

        } else if (fileType.equalsIgnoreCase("mp3")
                || fileType.equalsIgnoreCase("wav") || fileType.equalsIgnoreCase("wma")) {
            colorCodeModel.setPrimaryColor(R.color.thumbnail_mp3_color);
            colorCodeModel.setSecondaryColor(R.color.thumbnail_mp3_corner_color);

        } else {
            colorCodeModel.setPrimaryColor(R.color.thumbnail_default_color);
            colorCodeModel.setSecondaryColor(R.color.thumbnail_default_corner_color);

        }

        colorCodeModel.setFileType(fileType);

        return colorCodeModel;
    }


    public static void setSelectedItems(List<GetCategoryDocumentsResponse> mSelectedDocumentList)
    {
        GlobalVariables.selectedDocumentsList = mSelectedDocumentList;
    }

    public static void deleteFileFromInternalStorage(String selectedFilePath)
    {
        File file = new File(selectedFilePath);
        boolean deleted = file.delete();
    }


}
