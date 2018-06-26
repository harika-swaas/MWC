package com.swaas.mwc;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by harika on 12-06-2018.
 */

public class MessageDialog {
    private static final LogTracer LOG_TRACER = LogTracer.instance(MessageDialog.class);
    TextView content,dialogTitle;
    Dialog dialog;
    Button updateButton;
    Button okButton,laterButton;
    Context context;

    /*Approve and Reject*/
    CustomFontTextView dialogTitleAR;
    CustomFontEditText editTextAR;
    Dialog dialogAR;
    CustomFontTextView buttonOneAR,buttonTwoAR,optionalOrMandatory;

    public MessageDialog(Context context) {
        this.context = context;
        initDialog();
    }

    private void initDialog() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.message_dialog);
        content = (TextView) dialog.findViewById(R.id.text_dialog);
        okButton = (Button) dialog.findViewById(R.id.ok_button);
        laterButton = (Button) dialog.findViewById(R.id.later_button);
        dialogTitle = (TextView) dialog.findViewById(R.id.title);
    }

    public void showDialog(final Context context, String message, View.OnClickListener okClickListener, boolean isCancelable){
        dialog.setCancelable(isCancelable);
        // set message content
        content.setText(message);
        laterButton.setVisibility(View.GONE);
        // set if any listener
        if(okClickListener != null) {
            okButton.setOnClickListener(okClickListener);
        } else {
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
        if(!isCancelable) {
            dialog.setOnKeyListener(new Dialog.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {

                    }
                    return false;
                }
            });
        }
        if (! ((Activity) context).isFinishing()) {
            dialog.show();
        }
    }


    public void showDialogForDownload(final Context context, String message, View.OnClickListener positiveButton, View.OnClickListener negativebutton, boolean isCancelable){
        dialog.setCancelable(isCancelable);
        // set message content
        content.setText(message);
        laterButton.setVisibility(View.VISIBLE);
        laterButton.setText("No");
        okButton.setText("Yes");
        // set if any listener
        if(positiveButton != null) {
            okButton.setOnClickListener(positiveButton);
        } else {
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
        if(negativebutton == null){
            laterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }



        if(!isCancelable) {
            dialog.setOnKeyListener(new Dialog.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {

                    }
                    return false;
                }
            });
        }
        if (! ((Activity) context).isFinishing()) {
            dialog.show();
        }
    }


    public void showAlertDialog(final Context context, String message, String title,View.OnClickListener positiveListener,
                                View.OnClickListener negativeListener,String buttonOne,String buttonTwo) {
        okButton.setText(buttonOne);
        // set message content
        content.setText(message);
        dialogTitle.setText(title);
        laterButton.setVisibility(View.VISIBLE);
        laterButton.setText(buttonTwo);
        // set if any listener
        if(positiveListener != null) {
            okButton.setOnClickListener(positiveListener);
        }
        /*if(negativeListener != null)
            laterButton.setOnClickListener(negativeListener);*/
        laterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        if (! ((Activity) context).isFinishing()) {
            dialog.show();
        }

    }

    public void dismiss() {
        if(dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
