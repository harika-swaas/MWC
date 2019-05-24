package com.mwc.docportal.Scanning.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.thegrizzlylabs.geniusscan.sdk.core.Scan;

import java.io.File;

/**
 * Created by guillaume on 29/09/16.
 */

public class Image implements Scan, Parcelable {

    public Image(String name) {
        this.name = name;
    }

    private String name;

    @Override
    public String getAbsolutePath(Context context) {
        return new File(context.getExternalFilesDir(null), name).getAbsolutePath();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Image> CREATOR
            = new Creator<Image>() {
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }

    private Image(Parcel in) {
        name = in.readString();
    }
}
