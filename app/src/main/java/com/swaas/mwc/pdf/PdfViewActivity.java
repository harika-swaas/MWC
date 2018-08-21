package com.swaas.mwc.pdf;

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
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.google.gson.Gson;
import com.swaas.mwc.API.Model.ApiResponse;
import com.swaas.mwc.API.Model.DownloadDocumentRequest;
import com.swaas.mwc.API.Model.DownloadDocumentResponse;
import com.swaas.mwc.API.Model.EditDocumentPropertiesRequest;
import com.swaas.mwc.API.Model.EndUserRenameRequest;
import com.swaas.mwc.API.Model.ExternalShareResponseModel;
import com.swaas.mwc.API.Model.GetCategoryDocumentsResponse;
import com.swaas.mwc.API.Model.GetEndUserSharedParentFoldersResponse;
import com.swaas.mwc.API.Model.ListPinDevicesResponse;
import com.swaas.mwc.API.Model.LoginResponse;
import com.swaas.mwc.API.Model.OfflineFiles;
import com.swaas.mwc.API.Model.SharedDocumentResponseModel;
import com.swaas.mwc.API.Model.StopSharingRequestModel;
import com.swaas.mwc.API.Model.WhiteLabelResponse;
import com.swaas.mwc.API.Service.DownloadDocumentService;
import com.swaas.mwc.API.Service.EditDocumentPropertiesService;
import com.swaas.mwc.API.Service.EndUserRenameService;
import com.swaas.mwc.API.Service.GetEndUserParentSHaredFoldersService;
import com.swaas.mwc.API.Service.ShareEndUserDocumentsService;
import com.swaas.mwc.Common.FileDownloadManager;
import com.swaas.mwc.DMS.MyFolderActivity;
import com.swaas.mwc.DMS.MyFolderCopyActivity;
import com.swaas.mwc.DMS.MyFolderSharedDocuments;
import com.swaas.mwc.DMS.MyFoldersDMSActivity;
import com.swaas.mwc.DMS.Tab_Activity;
import com.swaas.mwc.Database.AccountSettings;
import com.swaas.mwc.Database.OffLine_Files_Repository;
import com.swaas.mwc.Dialogs.LoadingProgressDialog;
import com.swaas.mwc.Login.LoginActivity;
import com.swaas.mwc.Network.NetworkUtils;
import com.swaas.mwc.Preference.PreferenceUtils;
import com.swaas.mwc.R;
import com.swaas.mwc.Retrofit.RetrofitAPIBuilder;
import com.swaas.mwc.Utils.Constants;
import com.swaas.mwc.Utils.DateHelper;
import com.swaas.mwc.pdf.listener.OnLoadCompleteListener;
import com.swaas.mwc.pdf.listener.OnPageChangeListener;
import com.swaas.mwc.pdf.listener.OnSingleTapTouchListener;
import com.swaas.mwc.pdf.listener.PdfSwipeUpDownListener;
import com.swaas.mwc.pdf.pdfasync.DownloadPdfAysnc;
import com.swaas.mwc.pdf.pdfasync.OnPdfDownload;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

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
    List<WhiteLabelResponse> mWhiteLabelResponses;
    Context context = this;
    AlertDialog mAlertDialog;
    ImageView external_share_imgage, pdf_info_imgage;

    GetCategoryDocumentsResponse categoryDocumentsResponse;
    boolean isFromOffLine;
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

        external_share_imgage = (ImageView)findViewById(R.id.external_share_imgage);
        pdf_info_imgage = (ImageView)findViewById(R.id.pdf_info_imgage);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.mipmap.ic_back));
      //  getSupportActionBar().setTitle("Offline");

    //    Bundle bundle = getArguments();

        getWhiteLabelProperities();
        DownloadPdfAysnc downloadPdfAysnc =  new DownloadPdfAysnc(mContext,this);

            playmode = getIntent().getIntExtra("mode", 0);
            String urlName = getIntent().getStringExtra("url");
            isFromOffLine = getIntent().getBooleanExtra("IsFromOffline", false);
            /*filename = getIntent().getStringExtra("fileName");
            fileType = getIntent().getStringExtra("fileType");
            documentVersionId = getIntent().getStringExtra("documentVersionId");*/

            if(getIntent().getSerializableExtra("documentDetails") != null)
            {
                categoryDocumentsResponse = (GetCategoryDocumentsResponse)getIntent().getSerializableExtra("documentDetails");
                getSupportActionBar().setTitle(categoryDocumentsResponse.getName());
            }



            if (playmode == 0){

               /* File file = new File(Environment.getExternalStorageDirectory(), "somatosensory.pdf");
                String str = file.toString();*/

                mOfflineUrl = urlName;
            }else {

                mOnlineUrl = urlName;

                ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                if (netInfo != null){
                    downloadPdfAysnc.execute(mOnlineUrl,categoryDocumentsResponse.getName());
                }

            }

        if(isFromOffLine)
        {
            pdf_info_imgage.setVisibility(View.INVISIBLE);
        }
        else
        {
            pdf_info_imgage.setVisibility(View.VISIBLE);
        }


        pdf_info_imgage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceUtils.setDocumentVersionId(context,categoryDocumentsResponse.getDocument_version_id());
                PreferenceUtils.setDocument_Id(context, categoryDocumentsResponse.getObject_id());
                Intent intent = new Intent(context,Tab_Activity.class);
                startActivity(intent);

            }
        });

        external_share_imgage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showWarningAlertForSharingContent(categoryDocumentsResponse.getName(), categoryDocumentsResponse.getDocument_version_id());
            }
        });


    }

    private void showWarningAlertForSharingContent(final String name, final String document_version_id)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
        builder.setView(view);
        builder.setCancelable(false);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText("Warning");

        TextView txtMessage = (TextView) view.findViewById(R.id.txt_message);

        txtMessage.setText("You are sharing this document to external contacts. Please aware that document security will not be carried over to the recipient");

        Button sendPinButton = (Button) view.findViewById(R.id.send_pin_button);
        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);

        cancelButton.setText("CANCEL");

        sendPinButton.setText("OK");

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });

        sendPinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();

                getExternalSharingContentAPI(name, document_version_id);



            }
        });

        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    private void getExternalSharingContentAPI(final String name, String document_version_id)
    {
        if (NetworkUtils.isNetworkAvailable(context)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();

            final ExternalShareResponseModel externalShareResponseModel = new ExternalShareResponseModel("external_sharing", document_version_id);


            //  ExternalShareResponseModel = new GetCategoryDocumentsRequest(Integer.parseInt(obj), "list", "category", "1", "0");

            String request = new Gson().toJson(externalShareResponseModel);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final ShareEndUserDocumentsService mGetCategoryDocumentsService = retrofitAPI.create(ShareEndUserDocumentsService.class);

            Call call = mGetCategoryDocumentsService.getSharedDocumentDetails(params, PreferenceUtils.getAccessToken(context));

            call.enqueue(new Callback<SharedDocumentResponseModel>() {
                @Override
                public void onResponse(Response<SharedDocumentResponseModel> response, Retrofit retrofit) {

                    if (response != null) {

                        transparentProgressDialog.dismiss();

                        if (response.body().getStatus().getCode() instanceof Boolean) {
                            if (response.body().getStatus().getCode() == Boolean.FALSE) {
                                transparentProgressDialog.dismiss();

                                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                                sharingIntent.setType("text/plain");
                                String shareBody = "Here is the share content body";
                                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, name);
                                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                                Intent.createChooser(sharingIntent,"Share via");
                                context.startActivity(sharingIntent);


                            }

                        } else if (response.body().getStatus().getCode() instanceof Double) {
                            transparentProgressDialog.dismiss();
                            String mMessage = response.body().getStatus().getMessage().toString();

                            Object obj = 401.0;
                            if (obj.equals(401.0)) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
                                builder.setView(view);
                                builder.setCancelable(false);

                                TextView title = (TextView) view.findViewById(R.id.title);
                                title.setText("Alert");

                                TextView txtMessage = (TextView) view.findViewById(R.id.txt_message);

                                txtMessage.setText(mMessage);

                                Button sendPinButton = (Button) view.findViewById(R.id.send_pin_button);
                                Button cancelButton = (Button) view.findViewById(R.id.cancel_button);

                                cancelButton.setVisibility(View.GONE);

                                sendPinButton.setText("OK");

                                sendPinButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mAlertDialog.dismiss();
                                        AccountSettings accountSettings = new AccountSettings(context);
                                        accountSettings.deleteAll();
                                        context.startActivity(new Intent(context, LoginActivity.class));
                                    }
                                });

                                mAlertDialog = builder.create();
                                mAlertDialog.show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                    Log.d("PinDevice error", t.getMessage());
                }
            });
        }

    }

    @Override
    public void onStop() {
        super.onStop();

        isWentFromBackground = true;


    }


    private void getWhiteLabelProperities() {

        AccountSettings accountSettings = new AccountSettings(PdfViewActivity.this);
        accountSettings.SetWhiteLabelCB(new AccountSettings.GetWhiteLabelCB() {
            @Override
            public void getWhiteLabelSuccessCB(List<WhiteLabelResponse> whiteLabelResponses) {
                if (whiteLabelResponses != null && whiteLabelResponses.size() > 0) {
                    mWhiteLabelResponses = whiteLabelResponses;
                }
            }

            @Override
            public void getWhiteLabelFailureCB(String message) {

            }
        });

        accountSettings.getWhiteLabelProperties();
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
                        downloadPdfAysnc.execute(mOnlineUrl,categoryDocumentsResponse.getName());
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

     @Override
      public boolean onCreateOptionsMenu(Menu menu) {

         getMenuInflater().inflate(R.menu.menu_multi_select, menu);
         MenuItem menuItemSearch = menu.findItem(R.id.action_search);
         MenuItem menuItemDelete = menu.findItem(R.id.action_delete);
         MenuItem  menuItemShare = menu.findItem(R.id.action_share);
         MenuItem  menuItemMore = menu.findItem(R.id.action_more);
         MenuItem menuItemMove = menu.findItem(R.id.action_move);

         menuItemSearch.setVisible(false);
         menuItemDelete.setVisible(false);
         menuItemShare.setVisible(false);
         menuItemMove.setVisible(false);

         if(isFromOffLine)
         {
             menuItemMore.setVisible(false);
         }
         else
         {
             menuItemMore.setVisible(true);
         }


          String itemSelectedColor = mWhiteLabelResponses.get(0).getItem_Selected_Color();
          int selectedColor = Color.parseColor(itemSelectedColor);

          menuIconColor(menuItemSearch,selectedColor);

          return true;
      }

    public static void menuIconColor(MenuItem menuItem, int color) {
        Drawable drawable = menuItem.getIcon();
        if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
    }



         @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_more:

                openBottomSheetForDocument(categoryDocumentsResponse.getFiletype(), categoryDocumentsResponse.getName());
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void openBottomSheetForDocument(final String fileType, String name) {

        getWhiteLabelProperities();

        View view = ((PdfViewActivity) context).getLayoutInflater().inflate(R.layout.bottom_sheet_document_sort, null);
        RelativeLayout shareView = (RelativeLayout) view.findViewById(R.id.share_layout);
        TextView docText = (TextView) view.findViewById(R.id.doc_text);
        ImageView thumbnailIcon = (ImageView) view.findViewById(R.id.thumbnail_image);
        RelativeLayout docinfo = (RelativeLayout) view.findViewById(R.id.doc_info_layout);
        RelativeLayout copy = (RelativeLayout) view.findViewById(R.id.copy1);
        RelativeLayout move = (RelativeLayout) view.findViewById(R.id.move1);
        ImageView thumbnailCornerIcon = (ImageView) view.findViewById(R.id.thumbnail_corner_image);
        TextView thumbnailText = (TextView) view.findViewById(R.id.thumbnail_text);
        RelativeLayout rename_Layout = (RelativeLayout) view.findViewById(R.id.rename_layout);
        final SwitchCompat switchButton_share = (SwitchCompat) view.findViewById(R.id.switchButton_share);
        final SwitchCompat switchButton_download = (SwitchCompat) view.findViewById(R.id.switchButton_download);

        if(categoryDocumentsResponse.getIs_shared().equals("1"))
        {
            switchButton_share.setChecked(true);
        }
        else
        {
            switchButton_share.setChecked(false);
        }


        OffLine_Files_Repository offLine_files_repository = new OffLine_Files_Repository(context);
        if (!offLine_files_repository.checkAlreadyDocumentAvailableOrNot(categoryDocumentsResponse.getDocument_version_id())) {

            switchButton_download.setChecked(true);
        }
        else {
            switchButton_download.setChecked(false);
        }

        final Dialog mBottomSheetDialog = new Dialog(context, R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();


        switchButton_share.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(buttonView.isPressed() == true) {

                    if (!isChecked) {
                        switchButton_share.setChecked(true);
                        showWarningMessageAlertForSharingContent();
                        mBottomSheetDialog.dismiss();

                    } else {
                        switchButton_share.setChecked(false);
                        List<GetCategoryDocumentsResponse> selectedList = new ArrayList<>();
                        selectedList.add(categoryDocumentsResponse);

                        PreferenceUtils.setCategoryId(context, categoryDocumentsResponse.getCategory_id());
                        PreferenceUtils.setDocument_Id(context, categoryDocumentsResponse.getObject_id());

                        Intent mIntent = new Intent(context, MyFolderSharedDocuments.class);
                        mIntent.putExtra(Constants.OBJ, (Serializable) selectedList);
                        startActivity(mIntent);
                        mBottomSheetDialog.dismiss();
                    }


                }

            }
        });



        switchButton_download.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(buttonView.isPressed() == true) {

                    if (isChecked) {
                        switchButton_download.setChecked(true);
                        getDownloadurlFromService(categoryDocumentsResponse.getObject_id());
                        mBottomSheetDialog.dismiss();
                    }
                    else
                    {
                        OffLine_Files_Repository offLine_files_repository = new OffLine_Files_Repository(context);
                        offLine_files_repository.deleteAlreadydownloadedFile(categoryDocumentsResponse.getDocument_version_id());
                        switchButton_download.setChecked(false);
                        mBottomSheetDialog.dismiss();
                    }


                }

            }
        });





        final ImageView copyImage = (ImageView) view.findViewById(R.id.copy_image);
        ImageView moveImage = (ImageView) view.findViewById(R.id.move_image);
        ImageView renameImage = (ImageView) view.findViewById(R.id.rename_image);
        ImageView shareImage = (ImageView) view.findViewById(R.id.share_image);
        ImageView availableOfflineImage = (ImageView) view.findViewById(R.id.available_offline_image);

        if (mWhiteLabelResponses != null && mWhiteLabelResponses.size() > 0) {
            String itemSelectedColor = mWhiteLabelResponses.get(0).getItem_Selected_Color();
            int selectedColor = Color.parseColor(itemSelectedColor);

            copyImage.setColorFilter(selectedColor);
            renameImage.setColorFilter(selectedColor);
            moveImage.setColorFilter(selectedColor);
            shareImage.setColorFilter(selectedColor);
            availableOfflineImage.setColorFilter(selectedColor);
        }

        docText.setText(name);

        if (fileType.equalsIgnoreCase("pdf")) {
            //holder.imageView.setImageResource(R.mipmap.ic_pdf);
            thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_pdf_color));
            thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_pdf_corner_color));
            thumbnailText.setText(fileType);
        } else if (fileType.equalsIgnoreCase("xlsx") ||
                fileType.equalsIgnoreCase("xls") || fileType.equalsIgnoreCase("xlsm")
                || fileType.equalsIgnoreCase("csv")) {
            thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_xls_color));
            thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_xls_corner_color));
            thumbnailText.setText(fileType);
        } else if (fileType.equalsIgnoreCase("doc") ||
                fileType.equalsIgnoreCase("docx") || fileType.equalsIgnoreCase("docm")
                || fileType.equalsIgnoreCase("gdoc") || fileType.equalsIgnoreCase("keynote")) {
            thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_doc_color));
            thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_doc_corner_color));
            thumbnailText.setText(fileType);
        } else if (fileType.equalsIgnoreCase("ppt") ||
                fileType.equalsIgnoreCase("pptx") || fileType.equalsIgnoreCase("pps")
                || fileType.equalsIgnoreCase("ai")) {
            thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_ppt_color));
            thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_ppt_corner_color));
            thumbnailText.setText(fileType);
        } else if (fileType.equalsIgnoreCase("xml") ||
                fileType.equalsIgnoreCase("log") || fileType.equalsIgnoreCase("zip")
                || fileType.equalsIgnoreCase("rar") || fileType.equalsIgnoreCase("zipx")
                || fileType.equalsIgnoreCase("mht")) {
            thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_xml_color));
            thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_xml_corner_color));
            thumbnailText.setText(fileType);
        } else if (fileType.equalsIgnoreCase("xml") ||
                fileType.equalsIgnoreCase("log") || fileType.equalsIgnoreCase("rtf") ||
                fileType.equalsIgnoreCase("txt") || fileType.equalsIgnoreCase("epub")) {
            thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_xml_color));
            thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_xml_corner_color));
            thumbnailText.setText(fileType);
        } else if (fileType.equalsIgnoreCase("msg") || fileType.equalsIgnoreCase("dot") || fileType.equalsIgnoreCase("odt")
                || fileType.equalsIgnoreCase("ott")) {
            thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_msg_color));
            thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_msg_corner_color));
            thumbnailText.setText(fileType);
        } else if (fileType.equalsIgnoreCase("pages")) {
            thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_pages_color));
            thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_pages_corner_color));
            thumbnailText.setText(fileType);
        } else if (fileType.equalsIgnoreCase("pub") || fileType.equalsIgnoreCase("ods")) {
            thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_pub_color));
            thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_pub_corner_color));
            thumbnailText.setText(fileType);
        } else if (fileType.equalsIgnoreCase("gif") || fileType.equalsIgnoreCase("jpeg")
                || fileType.equalsIgnoreCase("jpg") || fileType.equalsIgnoreCase("png") || fileType.equalsIgnoreCase("bmp")
                || fileType.equalsIgnoreCase("tif") || fileType.equalsIgnoreCase("tiff") || fileType.equalsIgnoreCase("eps")
                || fileType.equalsIgnoreCase("svg") || fileType.equalsIgnoreCase("odp")
                || fileType.equalsIgnoreCase("otp")) {
            thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_gif_color));
            thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_gif_corner_color));
            thumbnailText.setText(fileType);
        } else if (fileType.equalsIgnoreCase("avi")
                || fileType.equalsIgnoreCase("flv") || fileType.equalsIgnoreCase("mpeg") ||
                fileType.equalsIgnoreCase("mpg") || fileType.equalsIgnoreCase("swf") || fileType.equalsIgnoreCase("wmv")) {
            thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_avi_color));
            thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_avi_corner_color));
            thumbnailText.setText(fileType);
        } else if (fileType.equalsIgnoreCase("mp3")
                || fileType.equalsIgnoreCase("wav") || fileType.equalsIgnoreCase("wma")) {
            thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_mp3_color));
            thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_mp3_corner_color));
            thumbnailText.setText(fileType);
        } else {
            thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_default_color));
            thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_default_corner_color));
            thumbnailText.setText(fileType);
        }


        docinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceUtils.setDocumentVersionId(context,categoryDocumentsResponse.getDocument_version_id());
                PreferenceUtils.setDocument_Id(context, categoryDocumentsResponse.getObject_id());
                Intent intent = new Intent(context, Tab_Activity.class);
                startActivity(intent);
            }
        });
      /*  shareView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
*/
        move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MyFolderActivity.class);
                startActivity(intent);
            }
        });

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MyFolderCopyActivity.class);
                startActivity(intent);
            }
        });

        rename_Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.rename_alert, null);
                builder.setView(view);
                builder.setCancelable(false);

                Button cancel = (Button) view.findViewById(R.id.cancel_b);
                Button allow = (Button) view.findViewById(R.id.allow);
                final EditText namer = (EditText) view.findViewById(R.id.edit_username1);
                allow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String folder = namer.getText().toString().trim();

                        renamedocument(categoryDocumentsResponse.getDocument_version_id(),folder,"","");
                        mAlertDialog.dismiss();
                        mBottomSheetDialog.dismiss();
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAlertDialog.dismiss();
                        mBottomSheetDialog.dismiss();
                    }
                });

                mAlertDialog = builder.create();
                mAlertDialog.show();


            }
        });

    }

    private void getDownloadurlFromService(String object_id)
    {
        if (NetworkUtils.isNetworkAvailable(context)) {
            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            final DownloadDocumentService downloadDocumentService = retrofitAPI.create(DownloadDocumentService.class);

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();

            //DownloadDocumentRequest downloadDocumentRequest = new DownloadDocumentRequest(PreferenceUtils.getDocumentVersionId(this));
            List<String> strlist = new ArrayList<>();
            strlist.add(object_id);
            DownloadDocumentRequest downloadDocumentRequest = new DownloadDocumentRequest(strlist);
            final String request = new Gson().toJson(downloadDocumentRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            Call call = downloadDocumentService.download(params, PreferenceUtils.getAccessToken(context));

            call.enqueue(new Callback<ApiResponse<DownloadDocumentResponse>>() {
                @Override
                public void onResponse(Response<ApiResponse<DownloadDocumentResponse>> response, Retrofit retrofit) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        if (apiResponse.status.getCode() == Boolean.FALSE) {
                            transparentProgressDialog.dismiss();
                            DownloadDocumentResponse downloadDocumentResponse = response.body().getData();

                            String downloaded_url = downloadDocumentResponse.getData();

                            String access_Token = PreferenceUtils.getAccessToken(context);

                            byte[] encodeValue = Base64.encode(access_Token.getBytes(), Base64.DEFAULT);
                            String base64AccessToken = new String(encodeValue);

                            if (android.os.Build.VERSION.SDK_INT > 9) {
                                StrictMode.ThreadPolicy policy =  new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                StrictMode.setThreadPolicy(policy);
                            }


                            categoryDocumentsResponse.setDownloadUrl(downloaded_url+"&token="+base64AccessToken);


                            getDownloadManagerForDownloading(categoryDocumentsResponse);

                        }
                        else {
                            transparentProgressDialog.dismiss();
                            String mMessage = apiResponse.status.getMessage().toString();
                           /*// mActivity.showMessagebox(mActivity, mMessage, new View.OnClickListener()
                                {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(mActivity, LoginActivity.class));
                                    mActivity.finish();
                                }
                            }, false);
                        */
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                }
            });
        }
    }

    private void getDownloadManagerForDownloading(final GetCategoryDocumentsResponse digitalAsset)
    {

            if (!TextUtils.isEmpty(digitalAsset.getDownloadUrl())) {
                FileDownloadManager fileDownloadManager = new FileDownloadManager(PdfViewActivity.this);
                fileDownloadManager.setFileTitle(digitalAsset.getName());
                fileDownloadManager.setDownloadUrl(digitalAsset.getDownloadUrl());
                fileDownloadManager.setDigitalAssets(digitalAsset);
                fileDownloadManager.setmFileDownloadListener(new FileDownloadManager.FileDownloadListener() {
                    @Override
                    public void fileDownloadSuccess(String path) {

                        OffLine_Files_Repository offLine_files_repository = new OffLine_Files_Repository(PdfViewActivity.this);
                        if (!offLine_files_repository.checkAlreadyDocumentAvailableOrNot(digitalAsset.getDocument_version_id())) {
                            offLine_files_repository.deleteAlreadydownloadedFile(digitalAsset.getDocument_version_id());
                            insertIntoOffLineFilesTable(digitalAsset, path);
                        }
                        else
                        {
                            insertIntoOffLineFilesTable(digitalAsset, path);
                        }


                    }

                    @Override
                    public void fileDownloadFailure() {

                    }
                });
                fileDownloadManager.downloadTheFile();
            }


    }


    private void insertIntoOffLineFilesTable(GetCategoryDocumentsResponse digitalAsset, String path)
    {
        OffLine_Files_Repository offLine_files_repository = new OffLine_Files_Repository(context);
        OfflineFiles offlineFilesModel = new OfflineFiles();
        offlineFilesModel.setDocumentId(digitalAsset.getObject_id());
        offlineFilesModel.setDocumentName(digitalAsset.getName());
        offlineFilesModel.setDocumentVersionId(digitalAsset.getDocument_version_id());
        offlineFilesModel.setDownloadDate(DateHelper.getCurrentDate());
        offlineFilesModel.setFilename(digitalAsset.getName());
        offlineFilesModel.setFilePath(path);
        offlineFilesModel.setFiletype(digitalAsset.getFiletype());
        offlineFilesModel.setFileSize(digitalAsset.getFilesize());
        offlineFilesModel.setFileSize(digitalAsset.getVersion_number());
        offlineFilesModel.setSource("Private");

        offLine_files_repository.InsertOfflineFilesData(offlineFilesModel);
    }




    public void showWarningMessageAlertForSharingContent()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
        builder.setView(view);
        builder.setCancelable(false);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText("Warning");

        TextView txtMessage = (TextView) view.findViewById(R.id.txt_message);

        txtMessage.setText("This action will stop sharing the selected document(s). Company with whom this has been shared will no longer be able to view this document");

        Button sendPinButton = (Button) view.findViewById(R.id.send_pin_button);
        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);

        cancelButton.setText("CANCEL");

        sendPinButton.setText("OK");

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });

        sendPinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();

                ArrayList<String> documentIdslist = new ArrayList<>();
                documentIdslist.add(categoryDocumentsResponse.getObject_id());
                getInternalStoppingSharingContentAPI(documentIdslist, categoryDocumentsResponse.getCategory_id());


            }
        });

        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    private void getInternalStoppingSharingContentAPI(ArrayList<String> objectid, String categoryId)
    {
        if (NetworkUtils.isNetworkAvailable(this)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(this);
            transparentProgressDialog.show();


            final StopSharingRequestModel deleteEndUserFolderRequest = new StopSharingRequestModel(objectid,categoryId);

            String request = new Gson().toJson(deleteEndUserFolderRequest);


            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final GetEndUserParentSHaredFoldersService mGetEndUserParentSHaredstopService = retrofitAPI.create(GetEndUserParentSHaredFoldersService.class);

            Call call = mGetEndUserParentSHaredstopService.getEndUserStopSharedDocuments(params, PreferenceUtils.getAccessToken(this));

            call.enqueue(new Callback<SharedDocumentResponseModel>() {
                @Override
                public void onResponse(Response<SharedDocumentResponseModel> response, Retrofit retrofit) {

                    if (response != null) {

                        transparentProgressDialog.dismiss();

                        if (response.body().getStatus().getCode() instanceof Boolean) {
                            if (response.body().getStatus().getCode() == Boolean.FALSE) {
                                transparentProgressDialog.dismiss();

                                categoryDocumentsResponse.setIs_shared("0");

                            }

                        } else if (response.body().getStatus().getCode() instanceof Double) {
                            transparentProgressDialog.dismiss();
                            String mMessage = response.body().getStatus().getMessage().toString();

                            Object obj = 401.0;
                            if(obj.equals(401.0)) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(PdfViewActivity.this);
                                LayoutInflater inflater = (LayoutInflater) PdfViewActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
                                builder.setView(view);
                                builder.setCancelable(false);

                                TextView title = (TextView) view.findViewById(R.id.title);
                                title.setText("Alert");

                                TextView txtMessage = (TextView) view.findViewById(R.id.txt_message);

                                txtMessage.setText(mMessage);

                                Button sendPinButton = (Button) view.findViewById(R.id.send_pin_button);
                                Button cancelButton = (Button) view.findViewById(R.id.cancel_button);

                                cancelButton.setVisibility(View.GONE);

                                sendPinButton.setText("OK");

                                sendPinButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mAlertDialog.dismiss();
                                        AccountSettings accountSettings = new AccountSettings(PdfViewActivity.this);
                                        accountSettings.deleteAll();
                                        startActivity(new Intent(PdfViewActivity.this, LoginActivity.class));
                                    }
                                });

                                mAlertDialog = builder.create();
                                mAlertDialog.show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                    Log.d("PinDevice error", t.getMessage());
                }
            });
        }

    }

    public void renamedocument(String object_id, final String name, String doc_created, String auth)
    {
        if (NetworkUtils.isNetworkAvailable(context)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();

            final EditDocumentPropertiesRequest editDocumentPropertiesRequest = new EditDocumentPropertiesRequest(object_id,name,doc_created,auth);

            String request = new Gson().toJson(editDocumentPropertiesRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final EditDocumentPropertiesService editDocumentPropertiesService = retrofitAPI.create(EditDocumentPropertiesService.class);

            Call call = editDocumentPropertiesService.getRenameDocument(params, PreferenceUtils.getAccessToken(context));

            call.enqueue(new Callback<ListPinDevicesResponse<LoginResponse>>() {
                @Override
                public void onResponse(Response<ListPinDevicesResponse<LoginResponse>> response, Retrofit retrofit) {
                    ListPinDevicesResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        transparentProgressDialog.dismiss();

                        if (apiResponse.status.getCode() instanceof Boolean) {
                            if (apiResponse.status.getCode() == Boolean.FALSE) {
                                transparentProgressDialog.dismiss();
                                categoryDocumentsResponse.setName(name);
                                getSupportActionBar().setTitle(name);
                            }

                        } else if (apiResponse.status.getCode() instanceof Integer) {
                            transparentProgressDialog.dismiss();
                            String mMessage = apiResponse.status.getMessage().toString();

                            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
                            builder.setView(view);
                            builder.setCancelable(false);

                            TextView txtMessage = (TextView) view.findViewById(R.id.txt_message);

                            txtMessage.setText(mMessage);

                            Button sendPinButton = (Button) view.findViewById(R.id.send_pin_button);
                            Button cancelButton = (Button) view.findViewById(R.id.cancel_button);

                            cancelButton.setVisibility(View.GONE);

                            sendPinButton.setText("OK");

                            sendPinButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mAlertDialog.dismiss();
                                    context.startActivity(new Intent(context, LoginActivity.class));
                                }
                            });

                            mAlertDialog = builder.create();
                            mAlertDialog.show();
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                    Log.d("PinDevice error", t.getMessage());
                }
            });
        }
    }


}

