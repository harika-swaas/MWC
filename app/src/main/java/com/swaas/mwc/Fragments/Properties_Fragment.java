package com.swaas.mwc.Fragments;

/**
 * Created by barath on 7/19/2018.
 */


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.swaas.mwc.R;

public class Properties_Fragment extends Fragment{

    View mView;
    public static Properties_Fragment newInstance() {
        Properties_Fragment fragment = new Properties_Fragment();
        return fragment;
    }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
// Inflate the layout for this fragment
            mView= inflater.inflate(R.layout.tab_fragment_1, container, false);
            return mView;
        }

    }
