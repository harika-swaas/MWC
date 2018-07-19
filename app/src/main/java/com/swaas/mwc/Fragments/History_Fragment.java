package com.swaas.mwc.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.swaas.mwc.R;

/**
 * Created by barath on 7/19/2018.
 */

public class History_Fragment extends android.support.v4.app.Fragment {
    View mView;
    public static Notes_Fragment newInstance() {
        Notes_Fragment fragment = new Notes_Fragment();
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
        mView= inflater.inflate(R.layout.tab_fragment_3, container, false);
        return mView;
    }
}
