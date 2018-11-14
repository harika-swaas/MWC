package com.mwc.docportal.Common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mwc.docportal.API.Model.ColorCodeModel;
import com.mwc.docportal.API.Model.GetCategoryDocumentsResponse;
import com.mwc.docportal.DMS.UploadListActivity;
import com.mwc.docportal.DMS.UploadListAdapter;
import com.mwc.docportal.Database.AccountSettings;
import com.mwc.docportal.Login.LoginActivity;
import com.mwc.docportal.Preference.PreferenceUtils;
import com.mwc.docportal.R;
import com.mwc.docportal.Utils.Constants;

import java.io.File;
import java.util.List;

public class CommonFunctions
{
    static AlertDialog mAlertDialog;
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


    public static boolean isApiSuccess(Activity mcontext, String message, Object code)
    {
        boolean isSuccess = true;

        Context context = mcontext;

        if(code instanceof Double)
        {
            double status_value = new Double(code.toString());
            if (status_value == 401.3)
            {
                if (!((Activity) context).isFinishing()) {
                    showAlertDialogForAccessDenied(context, message);
                }
                isSuccess = false;
            }
            else if(status_value ==  401 || status_value ==  401.0)
            {
                if (!((Activity) context).isFinishing()) {
                    showAlertDialogForSessionExpiry(context, message);
                }

                isSuccess = false;

            }
        }
        else if(code instanceof Integer)
        {
            int integerValue = new Integer(code.toString());
            if(integerValue ==  401)
            {
                if (!((Activity) context).isFinishing()) {
                    showAlertDialogForSessionExpiry(context, message);
                }
                isSuccess = false;

            }

        }
        else if(code instanceof Boolean)
        {
            if (code == Boolean.TRUE)
            {
                if (!((Activity) context).isFinishing()) {
                    showAlertDialogForAccessDenied(context, message);
                }
                isSuccess = false;
            }
        }


        return isSuccess;
    }

    private static void showAlertDialogForSessionExpiry(Context context, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
        builder.setView(view);
        builder.setCancelable(false);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText("Session Expired");

        TextView txtMessage = (TextView) view.findViewById(R.id.txt_message);

        txtMessage.setText(message);

        Button okButton = (Button) view.findViewById(R.id.send_pin_button);
        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);

        cancelButton.setVisibility(View.GONE);

        okButton.setText("Ok");

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
                AccountSettings accountSettings = new AccountSettings(context);
                accountSettings.LogouData();
            }
        });

        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    private static void showAlertDialogForAccessDenied(Context context, String message)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
        builder.setView(view);
        builder.setCancelable(false);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText("Alert");

        TextView txtMessage = (TextView) view.findViewById(R.id.txt_message);

        txtMessage.setText(message);

        Button okButton = (Button) view.findViewById(R.id.send_pin_button);
        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);

        cancelButton.setVisibility(View.GONE);

        okButton.setText("Ok");

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();

            }
        });

        mAlertDialog = builder.create();
        mAlertDialog.show();
    }


    public static void showSuccessfullyDownloaded(Context context)
    {
        if (!((Activity) context).isFinishing()) {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
            builder.setView(view);
            builder.setCancelable(false);

            TextView title = (TextView) view.findViewById(R.id.title);
            title.setText("Success");

            TextView txtMessage = (TextView) view.findViewById(R.id.txt_message);

            txtMessage.setText("Download completed. You can view these in Offline Files.");

            Button okButton = (Button) view.findViewById(R.id.send_pin_button);
            Button cancelButton = (Button) view.findViewById(R.id.cancel_button);

            cancelButton.setVisibility(View.GONE);

            okButton.setText("Ok");

            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAlertDialog.dismiss();

                }
            });

            mAlertDialog = builder.create();
            mAlertDialog.show();
        }
    }


    public static void showTimeoutAlert(Context context)
    {
        if (!((Activity) context).isFinishing())
        {
            getDialog(context).show();
        }

    }


    public static AlertDialog getDialog(Context context) {
        return new AlertDialog.Builder(context)
               // .setMessage("Network is disabled in your device. Would you like to enable it?")
                .setMessage(context.getString(R.string.check_network_txt))
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        context.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert).create();
    }

}
