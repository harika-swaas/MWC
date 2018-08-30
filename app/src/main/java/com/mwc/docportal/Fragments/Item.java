package com.mwc.docportal.Fragments;

/**
 * Created by barath on 7/17/2018.
 */

import android.os.Parcel;
import android.os.Parcelable;

    public class Item implements Parcelable {

        private int image2;
        private int image;
        private String text;

        public Item(int image, String text,int image2){
            this.image = image;
            this.text = text;
            this.image2=image2;
        }

        public int getImage(){
            return( this.image);
        }
        public int getImage2(){
            return  (this.image2);
        }

        public String getText() {
            return text;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.image);
            dest.writeString(this.text);
            dest.writeInt(this.image2);
        }

        protected Item(Parcel in) {
            this.image = in.readInt();
            this.text = in.readString();
            this.image2=in.readInt();
        }

        public static final Creator CREATOR = new Creator() {
            public Item createFromParcel(Parcel source) {
                return new Item(source);
            }

            public Item[] newArray(int size) {
                return new Item[size];
            }
        };
    }

