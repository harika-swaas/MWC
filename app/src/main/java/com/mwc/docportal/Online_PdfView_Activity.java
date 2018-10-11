package com.mwc.docportal;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mwc.docportal.Common.GlobalVariables;
import com.mwc.docportal.Dialogs.LoadingProgressDialog;
import com.mwc.docportal.FTL.WebviewLoaderTermsActivity;
import com.mwc.docportal.Utils.SplashScreen;
import com.mwc.docportal.pdf.PDFView;
import com.mwc.docportal.pdf.listener.OnLoadCompleteListener;
import com.mwc.docportal.pdf.listener.OnPageChangeListener;
import com.mwc.docportal.pdf.listener.OnSingleTapTouchListener;
import com.mwc.docportal.pdf.listener.PdfSwipeUpDownListener;
import com.mwc.docportal.pdf.pdfasync.DownloadPdfAysnc;
import com.mwc.docportal.pdf.pdfasync.OnPdfDownload;

import java.io.File;

import java.util.Calendar;
import java.util.Date;


public class Online_PdfView_Activity extends AppCompatActivity implements OnPdfDownload, OnPageChangeListener, OnLoadCompleteListener, OnSingleTapTouchListener, PdfSwipeUpDownListener {

    private String mAssetUrl;
    private Context mContext = this;
    private CountDownTimer waitTimer,Page_holderTimer,Page_holderTimer_Countinues;
    private PDFView mPdfview;

    public boolean isWentFromBackground = false;
    private ImageView mPrevious, mNext;
    // private String filename;
    private String PdfPath;
    //  private String fileType, documentVersionId;
    public boolean onLoadComplete = false;
    private LinearLayout mMenuHolder;
    private TextView mSinglePageChanger,mContinousPageCahnger;
    private CountDownTimer countDownTimer;
    //  private int CurrentAssetPosition;
    public boolean isSinglePageTap;
    private int playmode;
    public boolean loadpdfOnlineinitial = false;
    private TextView mProgressText;
    public LinearLayout mPageHolder;
    private TextView mCurrentpagenumber, mTotalPAgenumber;
    private RelativeLayout mMediaControllerHolder;

    public boolean isVisible=false;
    public boolean alreadyDownloaded = false,AlreadyOffline = false;
    public int alreadyloadedpagenumber = -1,PreviousPageNumber = -1, CurrentPageNumber = -1, TotalPageNumber = -1;
    private String mOnlineUrl,mOfflineUrl;
    Toolbar toolbar;
    private Date DetailedStartTime , PageStartTime;
    private ProgressDialog pDialog;
    Context context = this;
    String urlName, title;
    LoadingProgressDialog loadingProgressDialog;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_pdf_view);


        mPdfview = (PDFView) findViewById(R.id.asset_pdf_player);
        mSinglePageChanger = (TextView) findViewById(R.id.singlepageChanger);
        mContinousPageCahnger = (TextView) findViewById(R.id.ContinousPageChanger);
        mCurrentpagenumber = (TextView) findViewById(R.id.currentpage_number);
        mTotalPAgenumber = (TextView) findViewById(R.id.total_pagenumber);
        mPageHolder = (LinearLayout) findViewById(R.id.page_holder);
        mProgressText = (TextView) findViewById(R.id.progress_text);
        mMenuHolder = (LinearLayout) findViewById(R.id.menu_holder);
        mMenuHolder.setVisibility(View.GONE);
        mPdfview.useBestQuality(false);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.mipmap.ic_back));

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


        DownloadPdfAysnc downloadPdfAysnc =  new DownloadPdfAysnc(mContext,this);


        playmode = getIntent().getIntExtra("mode", 0);
        urlName = getIntent().getStringExtra("url");
        title = getIntent().getStringExtra("Terms_Title");
       getSupportActionBar().setTitle(title);

        if (!TextUtils.isEmpty(urlName)){
            urlName = urlName.replaceAll(" ", "%20");
        }


        mProgressText.setVisibility(View.GONE);
        loadingProgressDialog = new LoadingProgressDialog(Online_PdfView_Activity.this);
        loadingProgressDialog.show();


        if (playmode == 0){
            mOfflineUrl = urlName;
        }else {

            mOnlineUrl = urlName;

            ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
            if (netInfo != null){
                downloadPdfAysnc.execute(mOnlineUrl,title);
            }

        }

    }





    @Override
    public void onStop() {
        super.onStop();
        isWentFromBackground = true;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        isWentFromBackground = false;
    }

    private void LoadOfflinePdf() {

        if (mOfflineUrl!=null && mOfflineUrl.length() > 0){

            PdfPath = mOfflineUrl;

            mPdfview.setListenerForSinglePage(this);
            mPdfview.fromUri(Uri.parse("file:///"+mOfflineUrl)).defaultPage(0)
                    .onPageChange(this)
                    .enableAnnotationRendering(true)
                    .onLoad(this)
                    .enableSingletap(this)
                    .load();
            mPdfview.fitToWidth();
            loadpdfOnlineinitial = true;

        }
    }



    @Override
    public void onResume() {
        super.onResume();

        if(GlobalVariables.isComingFromApp)
        {
            Intent intent = new Intent(context, SplashScreen.class);
            intent.putExtra("IsFromForeground", true);
            intent.putExtra("ActivityName", "com.mwc.docportal.pdf.Online_PdfView_Activity");
            startActivityForResult(intent, 222);
        }

        if (isVisible){

            if (isWentFromBackground){

                if (playmode == 1){

                    ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                    DownloadPdfAysnc downloadPdfAysnc =  new DownloadPdfAysnc(mContext,this);
                    if (netInfo != null){
                        downloadPdfAysnc.execute(mOnlineUrl,title);
                    }
                }

            }

        }


        Log.e("Pdf","onResume");
        LoadOfflinePdf();


    }



    @Override
    public void onPause() {
        super.onPause();
        Log.e("Pdf","pause");


    }



    @Override
    public void onPdfDownloaded(String pdfpath) {

        if (pdfpath!=null){
            PdfPath = pdfpath;
            LoadOnlinePdf();

        }
    }

    private void LoadOnlinePdf() {

        mPdfview.setListenerForSinglePage(this);
        mPdfview.fromFile(new File(PdfPath)).defaultPage(0)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .load();
        loadpdfOnlineinitial = true;


    }

    @Override
    public void onPageChanged(int page, int pageCount) {

        Log.d("=>checkingsingle", "" + page);


    }



    @Override
    public void loadComplete(int nbPages) {

        mPdfview.setVisibility(View.VISIBLE);
        mProgressText.setVisibility(View.GONE);
        loadingProgressDialog.dismiss();

        Log.e("=>pdf","onloadcomplete"+nbPages);
        mPdfview.enableSingletap(this);
        if (isSinglePageTap){

            if (Page_holderTimer != null){
                Page_holderTimer.cancel();
            }
            mPageHolder.setVisibility(View.VISIBLE);
            mCurrentpagenumber.setText(CurrentPageNumber+"");
            mTotalPAgenumber.setText(TotalPageNumber+"");

            Page_holderTimer = new CountDownTimer(2000, 300) {
                public void onTick(long millisUntilFinished) {
                    //called every 300 milliseconds, which could be used to
                    //send messages or some other action
                }
                public void onFinish() {
                    mPageHolder.setVisibility(View.GONE);

                }
            }.start();
        }

        if (loadpdfOnlineinitial){

            singlePageShow();
            loadpdfOnlineinitial =false;
        }


    }

    private void setTimer(final int page) {

        waitTimer = new CountDownTimer(3000, 300) {
            public void onTick(long millisUntilFinished) {
                //called every 300 milliseconds, which could be used to
                //send messages or some other action
            }
            public void onFinish() {

                Calendar calendarfinish = Calendar.getInstance();
                calendarfinish.add(Calendar.SECOND,-3);
                DetailedStartTime = calendarfinish.getTime();
                PageStartTime = calendarfinish.getTime();


            }
        }.start();
    }


    @Override
    public void onSingletapTouch() {

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
       /* if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {


            setUpLandscape();

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){

            setPortrait();

        }*/
    }


    private void UpdatePage(String onlinePdfPath, int currentPageNumber, int totalPageNumber) {

        mPdfview.fromFile(new File(onlinePdfPath)).defaultPage(currentPageNumber)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .pages(currentPageNumber)
                .load();
        onPageChanged(currentPageNumber,totalPageNumber);
        CurrentPageNumber = currentPageNumber+1;

    }

    @Override
    public void onSwipeUp() {

        MovetoNextPage();

    }
    @Override
    public void onSwipeDown() {

        MovetoPreviousPage();

    }

    @Override
    public void onScroll() {

        if (Page_holderTimer_Countinues!=null){
            Page_holderTimer_Countinues.cancel();
        }
        //  assetPlayerActivity.showStatusBar();
        if (!isSinglePageTap){
            mPageHolder.setVisibility(View.VISIBLE);

        }

    }

    @Override
    public void onScrollEnding() {

        Page_holderTimer_Countinues = new CountDownTimer(2000, 300) {
            public void onTick(long millisUntilFinished) {
                //called every 300 milliseconds, which could be used to
                //send messages or some other action
            }
            public void onFinish() {
                mPageHolder.setVisibility(View.GONE);

            }
        }.start();

    }



    private void MovetoNextPage() {

        if (isSinglePageTap){

            if ((CurrentPageNumber)<TotalPageNumber){
                UpdatePage(PdfPath,CurrentPageNumber,TotalPageNumber);
            }

        }

    }

    private void MovetoPreviousPage() {

        if (isSinglePageTap){

            if (CurrentPageNumber-2 >= 0) {
                UpdatePage(PdfPath,CurrentPageNumber-2,TotalPageNumber);

            }

        }


    }


   /* public void showMenu(int itemId) {

        switch (itemId){

            case R.id.singlepage:

                singlePageShow();

                break;

            case R.id.continuespage:

                loadpdfOnlineinitial = false;
                mMenuHolder.setVisibility(View.GONE);
                isSinglePageTap = false;
                mPageHolder.setVisibility(View.GONE);
                mPdfview.setListenerForSinglePage(this);
                mPdfview.fromFile(new File(PdfPath)).defaultPage(CurrentPageNumber)
                        .onPageChange(this)
                        .enableAnnotationRendering(true)
                        .onLoad(this)
                        .load();



                break;

            default:

                break;

        }

    }*/

    private void singlePageShow() {

        mMenuHolder.setVisibility(View.GONE);
        isSinglePageTap = true;
        if (CurrentPageNumber == -1){
            CurrentPageNumber = mPdfview.getCurrentPage();
        }
        if (TotalPageNumber == -1){

            TotalPageNumber = mPdfview.getPageCount();
        }
        mPdfview.setListenerForSinglePage(this);
        UpdatePage(PdfPath,CurrentPageNumber,TotalPageNumber);


    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

    }



}