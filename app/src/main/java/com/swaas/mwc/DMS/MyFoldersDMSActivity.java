package com.swaas.mwc.DMS;

import android.os.Bundle;
import android.widget.Toast;

import com.swaas.mwc.Fragments.MyFoldersDMSFragment;
import com.swaas.mwc.R;
import com.swaas.mwc.RootActivity;

/**
 * Created by harika on 11-07-2018.
 */

public class MyFoldersDMSActivity extends RootActivity {

    MyFoldersDMSFragment mMyFoldersDMSFragment;
    int backButtonCount=0;

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

    @Override
    public void onBackPressed() {
        if (backButtonCount >= 1) {
            backButtonCount = 0;
            moveTaskToBack(true);
        } else {
            Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }
    }
}
