package com.swaas.mwc.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.swaas.mwc.R;

/**
 * Created by harika on 11-07-2018.
 */

public class ItemNavigationSharedFragment extends Fragment{

    public static ItemNavigationSharedFragment newInstance() {
        ItemNavigationSharedFragment fragment = new ItemNavigationSharedFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_item_navigation_shared, container, false);
    }
}
