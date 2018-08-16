package com.swaas.mwc.pdf;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.swaas.mwc.R;
import com.swaas.mwc.pdf.listener.OnLoadCompleteListener;
import com.swaas.mwc.pdf.listener.OnPageChangeListener;
import com.swaas.mwc.pdf.listener.OnSingleTapTouchListener;
import com.swaas.mwc.pdf.listener.PdfSwipeUpDownListener;
import com.swaas.mwc.pdf.pdfasync.DownloadPdfAysnc;
import com.swaas.mwc.pdf.pdfasync.OnPdfDownload;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Hariharan on 24/5/17.
 */

public class PdfViewActivity extends AppCompatActivity implements OnPdfDownload, OnPageChangeListener, OnLoadCompleteListener, OnSingleTapTouchListener, PdfSwipeUpDownListener {

    private String mAssetUrl;
    private Context mContext = this;
    private CountDownTimer waitTimer,Page_holderTimer,Page_holderTimer_Countinues;
    private PDFView mPdfview;

    public boolean isWentFromBackground = false;
    private ImageView mPrevious, mNext;
    private String filename;
    private String PdfPath;
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
    private Date DetailedStartTime , PageStartTime;
    private ProgressDialog pDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pdf_viewer);


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

    //    Bundle bundle = getArguments();
        DownloadPdfAysnc downloadPdfAysnc =  new DownloadPdfAysnc(mContext,this);
    //    if (bundle!=null){
          //  DAname =  bundle.getInt("filename");


         //   playmode =  bundle.getInt("playmode");
            playmode = getIntent().getIntExtra("mode", 0);
            String urlName = getIntent().getStringExtra("url");
            filename = getIntent().getStringExtra("fileName");

           /* if(playmode == 0)
            {
                mOfflineUrl = urlName;
            }
            else
            {
                mOnlineUrl = urlName;
            }*/


         //   CurrentAssetPosition = bundle.getInt("Index");
            if (playmode == 0){

               /* File file = new File(Environment.getExternalStorageDirectory(), "somatosensory.pdf");
                String str = file.toString();*/

                mOfflineUrl = urlName;
            }else {

                mOnlineUrl = urlName;

                ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                if (netInfo != null){
                    downloadPdfAysnc.execute(mOnlineUrl,filename);
                }

            }

    //    }
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



   /* private void setOnclickListeners() {

        mPrevious.setOnClickListener(this);
        mNext.setOnClickListener(this);
        mSinglePageChanger.setOnClickListener(this);
        mContinousPageCahnger.setOnClickListener(this);

    }*/


    @Override
    public void onResume() {
        super.onResume();

        if (isVisible){

            if (isWentFromBackground){

                if (playmode == 1){

                    ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                    DownloadPdfAysnc downloadPdfAysnc =  new DownloadPdfAysnc(mContext,this);
                    if (netInfo != null){
                        downloadPdfAysnc.execute(mOnlineUrl,filename);
                    }
                }

            }

        }


      /*  if (assetPlayerActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){

            setUpLandscape();

        }else {

            setPortrait();

        }*/


        Log.e("Pdf","onResume");
        LoadOfflinePdf();


    }

   /* private void showProgressDialogue(String message) {

        pDialog = new ProgressDialog(assetPlayerActivity);
        // Set progressbar title
        pDialog.setTitle("Hi Doctor");
        // Set progressbar message
        pDialog.setMessage(message);
        pDialog.setIndeterminate(false);

        // Show progressbar
        pDialog.show();

    }*/

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


    /*private void showNextAssetAlert() {

        new CountDownTimer(2000, 300){

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                assetPlayerActivity.ShowAlert(getString(R.string.willingtonextasset));

            }
        }.start();

    }*/

    @Override
    public void loadComplete(int nbPages) {

        mPdfview.setVisibility(View.VISIBLE);
        mProgressText.setVisibility(View.GONE);
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

/*        if (countDownTimer!=null){
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(6000,300) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {

                if (mMediaControllerHolder.getVisibility()==View.VISIBLE) {
                    mMediaControllerHolder.setVisibility(View.GONE);
                }
                assetPlayerActivity.HideActionBarControll();
            }
        }.start();


        assetPlayerActivity.ShowActionBarControll();
        if (mMediaControllerHolder.getVisibility() == View.VISIBLE) {
            mMediaControllerHolder.setVisibility(View.GONE);

        } else {

            mMediaControllerHolder.setVisibility(View.VISIBLE);
            if (isVisible){
                assetPlayerActivity.ShowSettingsButton();
            }

        }*/
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


  /*  private void setUpLandscape(){

        mPrevious.setImageResource(R.mipmap.ic_previous_land);
        mNext.setImageResource(R.mipmap.ic_next_land);

    }


    private void setPortrait() {

        mNext.setImageResource(R.mipmap.ic_next);
        mPrevious.setImageResource(R.mipmap.ic_previous);

    }
*/



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


}

