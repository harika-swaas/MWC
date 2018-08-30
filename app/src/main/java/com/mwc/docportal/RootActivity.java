package com.mwc.docportal;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import com.mwc.docportal.Common.FileDownloadManager;
import com.mwc.docportal.Utils.Logger;

/**
 * Created by harika on 12-06-2018.
 */

public class RootActivity extends AppCompatActivity {

    protected static final String TAG = RootActivity.class.getSimpleName();

    protected View mCoordinatorLayout;
    public static FileDownloadManager mFileDownloadManager;

    /**
     * Our primary toolbar that is going to be made
     * the ActionBar.
     */
    protected Toolbar mToolbar;

    /**
     * The actual ActionBar instance.
     */
    private ActionBar mActionBar;

    /**
     * The layout holding the ToolBar (ActionBar) and TabLayout if any.
     * This layout allows us to control scroll behavior or the child elements
     * and its motion more easily and gracefully.
     */
    protected AppBarLayout mAppBarLayout;

    /**
     * The layout that is going to hold the actual content of the activity,
     * fragment, tab etc.
     */
    private FrameLayout mFlContentLayout;

    /**
     * Instance of Alert dialog to ensure dismiss of the dialog can be
     * handled separately.
     */
    protected AlertDialog mAlertDialog;

    /**
     * Dialogs are primarily UI components and hence convenience to declare
     * in RootActivity since any screen in Android is going to be under an
     * activity.
     */
    protected ProgressDialog mProgressDialog;

    /**
     * Floating Action button on each screen. To be used if necessary.
     * By default, VISIBILITY of this item is set to GONE since not all
     * screens would actually need this functionality.
     */
    protected FloatingActionButton mFloatingActionButton;

    public MessageDialog messageDialog;

    public Snackbar mSnackbar;

    int permissionStatus;

    /**
     * Handler to this activity thread.
     */
    protected final Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_layout);

        // Initialize the toolbar layout and assign it as the Action Bar for
        // all screens.
        mCoordinatorLayout = findViewById(R.id.base_Coordinator);
        mToolbar = (Toolbar) findViewById(R.id.ga_ActionBar);
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();

        // Initialize all the parent level views. They will be modified or added
        // by child activities and fragments based on their need.

        mFlContentLayout = (FrameLayout) findViewById(R.id.base_FlContent);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.base_AppBar);

        // Call the ActionBar customizer. We do the basic setup. Child activities
        // can override this function to get better control over the action bar.
        // The action bar and toolbar instances should not be misused anywhere else.
        // Hence made private and setup here. Child activities can save a local instance
        // of action bar from here on. But they cannot manipulate before this level.
        customizeActionBar(mActionBar, mToolbar);
    }

    /**
     * Method to customize the action bar based on each activity's needs.
     */
    protected void customizeActionBar(ActionBar actionBar, Toolbar toolbar) {
        Logger.d(TAG, "customizeActionBar - setting TITLE to MWC");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public final void setActionBarTitle(String title) {
        mActionBar.setTitle(title);
    }

    // ----------------------------------------------
    // - LAYOUT STRUCTURE CHANGES
    // ----------------------------------------------

    /**
     * Sets the content view for this activity. Since we are using navigation
     * bar, content view cannot be used as before.
     *
     * @param view View to be set for the fragment or layout that is to be shown
     *             as the main content layout.
     */
    protected final void setMainContentView(View view) {
        try {
            if (mFlContentLayout != null) {
                mFlContentLayout.addView(view);
            }
        } catch (Exception e) {
            Logger.d(e.getLocalizedMessage());
            Logger.d(e.getMessage());
        }
    }

    /**
     * Sets the content view for this activity. Since we are using navigation
     * bar, content view cannot be used as before.
     *
     * @param layout Layout to be set for the fragment or layout that is to be shown
     *               as the main content layout.
     */
    protected final void setMainContentView(int layout) {
        try {
            if (mFlContentLayout != null) {
                Logger.d(TAG, "setMainContentView - setting the Layout with ID: " + layout);
                mFlContentLayout.addView(getLayoutInflater().inflate(layout, null));
            }
        } catch (Exception e) {
            Logger.d(e.getLocalizedMessage());
            Logger.d(e.getMessage());
        }
    }

    /**
     * Clears all the child views associated with the content view.
     */
    protected final void cleanMainContentView() {
        mFlContentLayout.removeAllViews();
    }

    /**
     * Look for a child with the given id in the Content layout.
     *
     * @param id Id of the view to search for in Content layout
     * @return View associated with the given ID
     */
    protected final View findViewByIdInContent(int id) {
        return mFlContentLayout.findViewById(id);
    }

    // ---------------------------------------------
    // - TAB BAR RELATED
    // ---------------------------------------------

    protected final void addTabLayout(int layout) {
        View view = getLayoutInflater().inflate(layout, null);
        mAppBarLayout.addView(view);
    }

    protected final void addTabLayout(View view) {
        mAppBarLayout.addView(view);
    }

    // ----------------------------------------------
    // - PROGRESS DIALOG FUNCTIONS
    // ----------------------------------------------

    public void showProgressDialog(int msgResourceId) {
        showProgressDialog(getString(msgResourceId));
    }

    public void showProgressDialog(String message) {
        initDialog();
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    private void initDialog() {
        // If a progress dialog instance is not available, create one.
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
        }
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    public void showSnackBar(CoordinatorLayout mCoordinatorLayout, String message) {
        mSnackbar = Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG)
                .setAction(R.string.snackbar_download, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideSnackbar();
                    }
                });
        mSnackbar.show();
    }

    public void hideSnackbar() {

        if (mSnackbar != null) {
            mSnackbar.dismiss();
        }
    }

    // -------------------------------------------
    // - ALERT DIALOG FUNCTIONS
    // -------------------------------------------

    public void showErrorDialog(String message) {
        showErrorDialog(null, message);
    }

    public void showErrorDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);

        if (title != null && title.length() > 0) {
            // We can show an alert dialog with an empty title. Hence this check.
            builder.setTitle(title);
        }

        // Generic error dialogs are used for information only. User only clicks on OK to dismiss it.
        builder.setNegativeButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    /**
     * Used to hide both Error dialog and Long press dialog
     */
    public void hideAlertDialog() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
    }

    public void showListAlertDialog(String title, final String[] array,
                                    int indexOfSelectedItem,
                                    DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(title);

//      builder.setItems(array, onClickListener);
        builder.setSingleChoiceItems(array, indexOfSelectedItem, onClickListener);

        mAlertDialog = builder.create();
        mAlertDialog.show();
    }


    public void showMultiChoiceListAlertDialog(String title, final String[] array,
                                               boolean[] indexOfSelectedItem,
                                               DialogInterface.OnMultiChoiceClickListener onClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(title);

        builder.setMultiChoiceItems(array, indexOfSelectedItem, onClickListener);

        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        mAlertDialog = builder.create();
        mAlertDialog.show();

    }
    // -------------------------------------------
    // - UTILITY FUNCTIONS
    // -------------------------------------------

    /**
     * Hides the virtual Keyboard if its showing currently
     */
    public void hideSoftKeyBoard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager.isAcceptingText()) {
            if (getCurrentFocus() != null) {
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public boolean showConfirmationDialog(String message, String[] buttonNames, DialogInterface.OnClickListener onClickListener, Context mcontext) {
        final boolean[] canContinue = {false};
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mcontext);
        builder.setMessage(message)
                .setPositiveButton(buttonNames[0], onClickListener)
                .setNegativeButton(buttonNames[1], new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
        return canContinue[0];
    }

    public void showMessagebox(Context context, String message, View.OnClickListener onClickListener, boolean isCancelable) {
        initMessageDialog(context);
        messageDialog.showDialog(context, message, onClickListener, isCancelable);
    }

    protected void showAlertDialog(Context context, String message, String dcrSubmitSuccess, View.OnClickListener positiveListener,
                                   View.OnClickListener negativeListener, String buttonOne, String buttonTwo) {
        initMessageDialog(context);
        messageDialog.showAlertDialog(context, message, dcrSubmitSuccess, positiveListener, negativeListener, buttonOne, buttonOne);
    }

    private void initMessageDialog(Context context) {
        if (messageDialog == null) {
            messageDialog = new MessageDialog(context);
        }
    }

    public void hideMessageDialog() {
        if (messageDialog != null) {
            messageDialog.dismiss();
        }
    }

    public void setmFileDownloadManager(FileDownloadManager fileDownloadManager) {
        mFileDownloadManager = fileDownloadManager;
    }
}
