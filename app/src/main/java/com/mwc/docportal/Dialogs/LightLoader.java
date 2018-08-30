package com.mwc.docportal.Dialogs;

import android.content.Context;
import android.util.TypedValue;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by harika on 03-07-2018.
 */

public class LightLoader extends MaterialDialog {
    public LightLoader(Context context){
        super(new MaterialDialog.Builder(context).progress(true, 0));
        ViewGroup.LayoutParams params = getWindow().getAttributes();
        params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                350,
                context.getResources().getDisplayMetrics());
        getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        setCancelable(false);
        this.show();
    }
}
