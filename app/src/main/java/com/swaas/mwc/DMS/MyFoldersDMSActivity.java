package com.swaas.mwc.DMS;

import android.os.Bundle;

import com.swaas.mwc.Fragments.MyFoldersDMSFragment;
import com.swaas.mwc.R;
import com.swaas.mwc.RootActivity;

/**
 * Created by harika on 11-07-2018.
 */

public class MyFoldersDMSActivity extends RootActivity {

    MyFoldersDMSFragment mMyFoldersDMSFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_folders_dms);

        mMyFoldersDMSFragment = new MyFoldersDMSFragment();
        loadDMSFragment();
    }

    private void loadDMSFragment() {

        getSupportFragmentManager().beginTransaction().replace(R.id.dms_fragment, mMyFoldersDMSFragment).
                addToBackStack(null).commit();
    }
}
