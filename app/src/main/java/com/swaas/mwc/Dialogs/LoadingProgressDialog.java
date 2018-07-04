package com.swaas.mwc.Dialogs;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.swaas.mwc.R;

/**
 * Created by harika on 03-07-2018.
 */

public class LoadingProgressDialog extends ProgressDialog {

    public LoadingProgressDialog(Context context) {
        super(context);
    }

    @Override
    public void show() {
        super.show();
        setCancelable(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.custom_progress_dialog);
    }
}
