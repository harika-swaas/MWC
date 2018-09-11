package com.mwc.docportal.pdf;

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
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.mwc.docportal.API.Model.ApiResponse;
import com.mwc.docportal.API.Model.ColorCodeModel;
import com.mwc.docportal.API.Model.DeleteDocumentRequest;
import com.mwc.docportal.API.Model.DeleteDocumentResponseModel;
import com.mwc.docportal.API.Model.DocumentPreviewRequest;
import com.mwc.docportal.API.Model.DocumentPropertiesRequest;
import com.mwc.docportal.API.Model.DocumentPropertiesResponse;
import com.mwc.docportal.API.Model.DownloadDocumentRequest;
import com.mwc.docportal.API.Model.DownloadDocumentResponse;
import com.mwc.docportal.API.Model.EditDocumentPropertiesRequest;
import com.mwc.docportal.API.Model.EditDocumentResponse;
import com.mwc.docportal.API.Model.ExternalShareResponseModel;
import com.mwc.docportal.API.Model.GetCategoryDocumentsResponse;
import com.mwc.docportal.API.Model.ListPinDevicesResponse;
import com.mwc.docportal.API.Model.LoginResponse;
import com.mwc.docportal.API.Model.OfflineFiles;
import com.mwc.docportal.API.Model.PdfDocumentResponseModel;
import com.mwc.docportal.API.Model.SharedDocumentResponseModel;
import com.mwc.docportal.API.Model.StopSharingRequestModel;
import com.mwc.docportal.API.Model.WhiteLabelResponse;
import com.mwc.docportal.API.Service.DeleteDocumentService;
import com.mwc.docportal.API.Service.DocumentPreviewService;
import com.mwc.docportal.API.Service.DocumentPropertiesService;
import com.mwc.docportal.API.Service.DownloadDocumentService;
import com.mwc.docportal.API.Service.EditDocumentPropertiesService;

import com.mwc.docportal.API.Service.GetEndUserParentSHaredFoldersService;
import com.mwc.docportal.API.Service.ShareEndUserDocumentsService;
import com.mwc.docportal.Common.CommonFunctions;
import com.mwc.docportal.Common.FileDownloadManager;
import com.mwc.docportal.Common.GlobalVariables;

import com.mwc.docportal.DMS.MyFolderSharedDocuments;

import com.mwc.docportal.DMS.NavigationMyFolderActivity;
import com.mwc.docportal.DMS.NavigationSharedActivity;
import com.mwc.docportal.DMS.Tab_Activity;
import com.mwc.docportal.Database.AccountSettings;
import com.mwc.docportal.Database.OffLine_Files_Repository;
import com.mwc.docportal.Dialogs.LoadingProgressDialog;
import com.mwc.docportal.Login.LoginActivity;
import com.mwc.docportal.Network.NetworkUtils;
import com.mwc.docportal.Preference.PreferenceUtils;
import com.mwc.docportal.R;
import com.mwc.docportal.Retrofit.RetrofitAPIBuilder;
import com.mwc.docportal.Utils.Constants;
import com.mwc.docportal.Utils.DateHelper;
import com.mwc.docportal.pdf.listener.OnLoadCompleteListener;
import com.mwc.docportal.pdf.listener.OnPageChangeListener;
import com.mwc.docportal.pdf.listener.OnSingleTapTouchListener;
import com.mwc.docportal.pdf.listener.PdfSwipeUpDownListener;
import com.mwc.docportal.pdf.pdfasync.DownloadPdfAysnc;
import com.mwc.docportal.pdf.pdfasync.OnPdfDownload;

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
    AlertDialog mBackDialog;
    ImageView external_share_imgage, pdf_info_imgage;

    GetCategoryDocumentsResponse categoryDocumentsResponse;
    boolean isFromOffLine, isFromStatus400, isFromDocumentShare;
    LinearLayout document_preview_linearlayout;
    Button download_button;
    MenuItem  menuItemMore;
    List<DocumentPropertiesResponse> documentPropertiesResponse;
    String urlName;
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
        document_preview_linearlayout = (LinearLayout)findViewById(R.id.document_preview_linearlayout);
        download_button = (Button) findViewById(R.id.download_button);


        external_share_imgage = (ImageView)findViewById(R.id.external_share_imgage);
        pdf_info_imgage = (ImageView)findViewById(R.id.pdf_info_imgage);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.mipmap.ic_back));
      //  getSupportActionBar().setTitle("Offline");

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        if(PreferenceUtils.getPushNotificationDocumentVersionId(context) != null && !PreferenceUtils.getPushNotificationDocumentVersionId(context).isEmpty())
        {
            getDocumentDetailsBasedOnDocument(PreferenceUtils.getPushNotificationDocumentVersionId(context), PreferenceUtils.getPushNotificationDocumentShare(context));
            PreferenceUtils.setPushNotificationDocumentVersionId(context, null);
            PreferenceUtils.setPushNotificationDocumentShare(context, null);
        }


        getWhiteLabelProperities();
        DownloadPdfAysnc downloadPdfAysnc =  new DownloadPdfAysnc(mContext,this);


            playmode = getIntent().getIntExtra("mode", 0);
            urlName = getIntent().getStringExtra("url");
            isFromOffLine = getIntent().getBooleanExtra("IsFromOffline", false);
            isFromStatus400 = getIntent().getBooleanExtra("isFrom_Status400", false);
            isFromDocumentShare = getIntent().getBooleanExtra("IsFromShare", false);


            if(getIntent().getSerializableExtra("documentDetails") != null)
            {
                categoryDocumentsResponse = (GetCategoryDocumentsResponse)getIntent().getSerializableExtra("documentDetails");
                getSupportActionBar().setTitle(categoryDocumentsResponse.getName());
            }

            if(isFromStatus400)
            {
                document_preview_linearlayout.setVisibility(View.VISIBLE);
                mProgressText.setVisibility(View.GONE);
                mPdfview.setVisibility(View.GONE);
                external_share_imgage.setVisibility(View.GONE);

                OffLine_Files_Repository offLine_files_repository = new OffLine_Files_Repository(context);
                String filepath = offLine_files_repository.getFilePathFromLocalTable(categoryDocumentsResponse.getDocument_version_id());

                if(filepath != null && !filepath.isEmpty())
                {
                    external_share_imgage.setVisibility(View.VISIBLE);
                    download_button.setText("View");
                }

            }
            else
            {
                document_preview_linearlayout.setVisibility(View.GONE);
                mProgressText.setVisibility(View.VISIBLE);

            }









            if (playmode == 0){
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


        download_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(download_button.getText().toString().equalsIgnoreCase("Download"))
                {
                    getDownloadurlFromService(categoryDocumentsResponse.getObject_id(), categoryDocumentsResponse.getIs_shared(),false);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                }
                else if(download_button.getText().toString().equalsIgnoreCase("View"))
                {
                    OffLine_Files_Repository offLine_files_repository = new OffLine_Files_Repository(context);
                    String filepath = offLine_files_repository.getFilePathFromLocalTable(categoryDocumentsResponse.getDocument_version_id());

                    getExternalSharingContentAPI(categoryDocumentsResponse.getName(), categoryDocumentsResponse.getDocument_version_id(), filepath);
                }


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

        Button okButton = (Button) view.findViewById(R.id.send_pin_button);
        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);

        cancelButton.setText("CANCEL");

        okButton.setText("OK");

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
                OffLine_Files_Repository offLine_files_repository = new OffLine_Files_Repository(context);
                String filepath = offLine_files_repository.getFilePathFromLocalTable(categoryDocumentsResponse.getDocument_version_id());

                if(filepath == null || filepath.isEmpty())
                {
                    getDownloadurlFromService(categoryDocumentsResponse.getObject_id(), categoryDocumentsResponse.getIs_shared(),true);
                }
                else
                {
                    getExternalSharingContentAPI(name, document_version_id, filepath);
                }





            }
        });

        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    private void getExternalSharingContentAPI(final String name, String document_version_id, String filepath)
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


                                String[] mimetypes = {"image/*", "application/*|text/*"};

                                String imagePath =filepath;

                                File imageFileToShare = new File(imagePath);

                                Uri uri = Uri.fromFile(imageFileToShare);

                                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                                sharingIntent.setType("*/*");
                                String shareBody = "";
                                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, name);
                                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                                sharingIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                                sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
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

        if(isFromStatus400)
        {
            mPdfview.setVisibility(View.GONE);
            mProgressText.setVisibility(View.GONE);
        }
        else
        {
            mPdfview.setVisibility(View.VISIBLE);
            mProgressText.setVisibility(View.GONE);
        }


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
      public boolean onCreateOptionsMenu(Menu menu) {

         getMenuInflater().inflate(R.menu.menu_multi_select, menu);
         MenuItem menuItemSearch = menu.findItem(R.id.action_search);
         MenuItem menuItemDelete = menu.findItem(R.id.action_delete);
         MenuItem  menuItemShare = menu.findItem(R.id.action_share);
         menuItemMore = menu.findItem(R.id.action_more);
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


         if(isFromStatus400)
         {
             menuItemMore.setVisible(false);
             OffLine_Files_Repository offLine_files_repository = new OffLine_Files_Repository(context);
             String filepath = offLine_files_repository.getFilePathFromLocalTable(categoryDocumentsResponse.getDocument_version_id());

             if(filepath != null && !filepath.isEmpty())
             {
                 menuItemMore.setVisible(true);
             }
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

                openBottomSheetForDocument(categoryDocumentsResponse, categoryDocumentsResponse.getFiletype(), categoryDocumentsResponse.getName());
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void openBottomSheetForDocument(GetCategoryDocumentsResponse categoryDocumentsResponse, final String fileType, String name) {

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
        TextView delete= (TextView)view.findViewById(R.id.delete);


        if(this.categoryDocumentsResponse.getIs_shared().equals("1"))
        {
            switchButton_share.setChecked(true);
        }
        else
        {
            switchButton_share.setChecked(false);
        }

        List<GetCategoryDocumentsResponse> categoryDocumentlist = new ArrayList<>();
        categoryDocumentlist.add(categoryDocumentsResponse);
        CommonFunctions.setSelectedItems(categoryDocumentlist);


        OffLine_Files_Repository offLine_files_repository = new OffLine_Files_Repository(context);
        if (!offLine_files_repository.checkAlreadyDocumentAvailableOrNot(this.categoryDocumentsResponse.getDocument_version_id())) {

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

        if(isFromDocumentShare)
        {
            shareView.setVisibility(View.GONE);
            move.setVisibility(View.GONE);
            rename_Layout.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
        }


        switchButton_share.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(buttonView.isPressed() == true) {
                    mBottomSheetDialog.dismiss();
                    if (!isChecked) {
                        switchButton_share.setChecked(true);
                        showWarningMessageAlertForSharingContent();

                    } else {
                        switchButton_share.setChecked(false);
                        GlobalVariables.isMoveInitiated = true;
                        GlobalVariables.selectedActionName =  "share";
                        Intent intent = new Intent(context, NavigationSharedActivity.class);
                        intent.putExtra("ObjectId", "0");
                        context.startActivity(intent);
                    }


                }

            }
        });



        delete.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v)
            {
                mBottomSheetDialog.dismiss();
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.delete_document, null);
                builder.setView(view);
                builder.setCancelable(false);
                Button delete = (Button)view.findViewById(R.id.delete);
                Button delete_historic=(Button)view.findViewById(R.id.movefolder);
                Button delete_all =(Button)view.findViewById(R.id.deleteall);
                Button cancel = (Button)view.findViewById(R.id.canceldel);

                if(PdfViewActivity.this.categoryDocumentsResponse.getVersion_count().equals("0"))
                {
                    delete_historic.setEnabled(false);
                    delete_historic.setTextColor(R.color.grey);
                    delete_all.setEnabled(false);
                    delete_all.setTextColor(R.color.grey);
                }


                mBackDialog = builder.create();
                mBackDialog.show();
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (NetworkUtils.isNetworkAvailable(context)) {

                            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

                            ArrayList<String> documentVersionList = new ArrayList<>();
                            documentVersionList.add(PdfViewActivity.this.categoryDocumentsResponse.getDocument_version_id());

                            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
                            transparentProgressDialog.show();

                            ArrayList<DeleteDocumentRequest> docsList = new ArrayList<>();

                            DeleteDocumentRequest deleteDocumentRequest= new DeleteDocumentRequest();
                            deleteDocumentRequest.setDoc_id(PdfViewActivity.this.categoryDocumentsResponse.getObject_id());
                            deleteDocumentRequest.setDoc_version_ids(documentVersionList);
                            deleteDocumentRequest.setMode("0");
                            docsList.add(deleteDocumentRequest);

                            final DeleteDocumentRequest.DeleteDocRequest deleteDocRequest = new DeleteDocumentRequest.DeleteDocRequest(docsList);

                            String request = new Gson().toJson(deleteDocRequest);

                            //Here the json data is add to a hash map with key data
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("data", request);

                            final DeleteDocumentService deleteDocumentService = retrofitAPI.create(DeleteDocumentService.class);

                            Call call = deleteDocumentService.delete_eu_document(params, PreferenceUtils.getAccessToken(context));

                            call.enqueue(new Callback<DeleteDocumentResponseModel>() {
                                @Override
                                public void onResponse(Response<DeleteDocumentResponseModel> response, Retrofit retrofit) {
                                    DeleteDocumentResponseModel apiResponse = response.body();
                                    if (apiResponse != null) {

                                        transparentProgressDialog.dismiss();

                                        if (apiResponse.getStatus().getCode() instanceof Boolean) {
                                            if (apiResponse.getStatus().getCode() == Boolean.FALSE) {
                                                mBackDialog.dismiss();
                                                GlobalVariables.refreshDMS = true;
                                                finish();

                                            }

                                        } else if (apiResponse.getStatus().getCode() instanceof Integer) {
                                            transparentProgressDialog.dismiss();
                                            mBackDialog.dismiss();
                                            String mMessage = apiResponse.getStatus().getMessage().toString();

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
                                    mBackDialog.dismiss();
                                    Log.d("PinDevice error", t.getMessage());
                                }
                            });
                        }


                    }
                });


                delete_historic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (NetworkUtils.isNetworkAvailable(context)) {

                            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

                            ArrayList<String> documentVersionList = new ArrayList<>();
                            documentVersionList.add(PdfViewActivity.this.categoryDocumentsResponse.getDocument_version_id());

                            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
                            transparentProgressDialog.show();

                            ArrayList<DeleteDocumentRequest> docsList = new ArrayList<>();

                            DeleteDocumentRequest deleteDocumentRequest= new DeleteDocumentRequest();
                            deleteDocumentRequest.setDoc_id(PdfViewActivity.this.categoryDocumentsResponse.getObject_id());
                            deleteDocumentRequest.setDoc_version_ids(documentVersionList);
                            deleteDocumentRequest.setMode("1");
                            docsList.add(deleteDocumentRequest);
                            final DeleteDocumentRequest.DeleteDocRequest deleteDocRequest = new DeleteDocumentRequest.DeleteDocRequest(docsList);

                            String request = new Gson().toJson(deleteDocRequest);

                            //Here the json data is add to a hash map with key data
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("data", request);

                            final DeleteDocumentService deleteDocumentService = retrofitAPI.create(DeleteDocumentService.class);

                            Call call = deleteDocumentService.delete_eu_document(params, PreferenceUtils.getAccessToken(context));

                            call.enqueue(new Callback<DeleteDocumentResponseModel>() {
                                @Override
                                public void onResponse(Response<DeleteDocumentResponseModel> response, Retrofit retrofit) {
                                    DeleteDocumentResponseModel apiResponse = response.body();
                                    if (apiResponse != null) {

                                        transparentProgressDialog.dismiss();

                                        if (apiResponse.getStatus().getCode() instanceof Boolean) {
                                            if (apiResponse.getStatus().getCode() == Boolean.FALSE) {
                                                mBackDialog.dismiss();
                                                GlobalVariables.refreshDMS = true;
                                                finish();



                                            }

                                        } else if (apiResponse.getStatus().getCode() instanceof Integer) {
                                            transparentProgressDialog.dismiss();
                                            mBackDialog.dismiss();
                                            String mMessage = apiResponse.getStatus().getMessage().toString();

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
                                    mBackDialog.dismiss();
                                    Log.d("PinDevice error", t.getMessage());
                                }
                            });
                        }



                    }
                });

                delete_all.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (NetworkUtils.isNetworkAvailable(context)) {

                            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

                            ArrayList<String> documentVersionList = new ArrayList<>();
                            documentVersionList.add(PdfViewActivity.this.categoryDocumentsResponse.getDocument_version_id());

                            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
                            transparentProgressDialog.show();

                            ArrayList<DeleteDocumentRequest> docsList = new ArrayList<>();

                            DeleteDocumentRequest deleteDocumentRequest= new DeleteDocumentRequest();
                            deleteDocumentRequest.setDoc_id(PdfViewActivity.this.categoryDocumentsResponse.getObject_id());
                            deleteDocumentRequest.setDoc_version_ids(documentVersionList);
                            deleteDocumentRequest.setMode("2");
                            docsList.add(deleteDocumentRequest);
                            final DeleteDocumentRequest.DeleteDocRequest deleteDocRequest = new DeleteDocumentRequest.DeleteDocRequest(docsList);

                            String request = new Gson().toJson(deleteDocRequest);

                            //Here the json data is add to a hash map with key data
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("data", request);

                            final DeleteDocumentService deleteDocumentService = retrofitAPI.create(DeleteDocumentService.class);

                            Call call = deleteDocumentService.delete_eu_document(params, PreferenceUtils.getAccessToken(context));

                            call.enqueue(new Callback<DeleteDocumentResponseModel>() {
                                @Override
                                public void onResponse(Response<DeleteDocumentResponseModel> response, Retrofit retrofit) {
                                    DeleteDocumentResponseModel apiResponse = response.body();
                                    if (apiResponse != null) {

                                        transparentProgressDialog.dismiss();

                                        if (apiResponse.getStatus().getCode() instanceof Boolean) {
                                            if (apiResponse.getStatus().getCode() == Boolean.FALSE) {
                                                mBackDialog.dismiss();
                                                GlobalVariables.refreshDMS = true;
                                                finish();



                                            }

                                        } else if (apiResponse.getStatus().getCode() instanceof Integer) {
                                            transparentProgressDialog.dismiss();
                                            mBackDialog.dismiss();
                                            String mMessage = apiResponse.getStatus().getMessage().toString();

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
                                    mBackDialog.dismiss();
                                    Log.d("PinDevice error", t.getMessage());
                                }
                            });
                        }


                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBackDialog.dismiss();

                    }
                });




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
                        getDownloadurlFromService(categoryDocumentsResponse.getObject_id(), categoryDocumentsResponse.getIs_shared(),false);
                        mBottomSheetDialog.dismiss();
                    }
                    else
                    {
                        OffLine_Files_Repository offLine_files_repository = new OffLine_Files_Repository(context);
                        offLine_files_repository.deleteAlreadydownloadedFile(PdfViewActivity.this.categoryDocumentsResponse.getDocument_version_id());
                        String filepath = offLine_files_repository.getFilePathFromLocalTable(PdfViewActivity.this.categoryDocumentsResponse.getDocument_version_id());
                        if(filepath != null && !filepath.isEmpty())
                        {
                            CommonFunctions.deleteFileFromInternalStorage(filepath);
                        }
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

        ColorCodeModel colorCodeModel = CommonFunctions.getColorCodesforFileType(fileType);
        if(colorCodeModel != null)
        {
            thumbnailIcon.setColorFilter(context.getResources().getColor(colorCodeModel.getPrimaryColor()));
            thumbnailCornerIcon.setColorFilter(context.getResources().getColor(colorCodeModel.getSecondaryColor()));
            thumbnailText.setText(colorCodeModel.getFileType());

        }




        docinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
                PreferenceUtils.setDocumentVersionId(context, PdfViewActivity.this.categoryDocumentsResponse.getDocument_version_id());
                PreferenceUtils.setDocument_Id(context, PdfViewActivity.this.categoryDocumentsResponse.getObject_id());
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
                mBottomSheetDialog.dismiss();
                initiateMoveAction("move");


            }
        });

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
                initiateMoveAction("copy");
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

                        if(folder != null && !folder.isEmpty())
                        {
                            renamedocument(PdfViewActivity.this.categoryDocumentsResponse.getDocument_version_id(),folder,"","");
                            mAlertDialog.dismiss();
                            mBottomSheetDialog.dismiss();
                        }
                        else
                        {
                            Toast.makeText(context, "Please enter name", Toast.LENGTH_SHORT).show();
                        }


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

    private void getDownloadurlFromService(String object_id, String is_Shared, boolean isFromshare)
    {
        if (NetworkUtils.isNetworkAvailable(context)) {
            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            final DownloadDocumentService downloadDocumentService = retrofitAPI.create(DownloadDocumentService.class);

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();

            //DownloadDocumentRequest downloadDocumentRequest = new DownloadDocumentRequest(PreferenceUtils.getDocumentVersionId(this));
            List<String> strlist = new ArrayList<>();
            strlist.add(object_id);
            DownloadDocumentRequest downloadDocumentRequest = new DownloadDocumentRequest(strlist, is_Shared);
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
                            getDownloadManagerForDownloading(categoryDocumentsResponse, isFromshare);

                        }
                        else {
                            transparentProgressDialog.dismiss();
                            String mMessage = apiResponse.status.getMessage().toString();
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
                }
            });
        }
    }

    private void getDownloadManagerForDownloading(final GetCategoryDocumentsResponse digitalAsset, boolean isFromshare)
    {

            if (!TextUtils.isEmpty(digitalAsset.getDownloadUrl())) {
                FileDownloadManager fileDownloadManager = new FileDownloadManager(PdfViewActivity.this);
                fileDownloadManager.setFileTitle(digitalAsset.getName());
                fileDownloadManager.setDownloadUrl(digitalAsset.getDownloadUrl());
                fileDownloadManager.setDigitalAssets(digitalAsset);
                fileDownloadManager.setmFileDownloadListener(new FileDownloadManager.FileDownloadListener() {
                    @Override
                    public void fileDownloadSuccess(String path) {

                        if(isFromStatus400)
                        {
                            external_share_imgage.setVisibility(View.VISIBLE);
                            menuItemMore.setVisible(true);
                            download_button.setText("View");
                            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        }
                        else if(isFromshare)
                        {
                            getExternalSharingContentAPI(digitalAsset.getName(), digitalAsset.getDocument_version_id(), path);
                        }

                        OffLine_Files_Repository offLine_files_repository = new OffLine_Files_Repository(PdfViewActivity.this);
                        if (!offLine_files_repository.checkAlreadyDocumentAvailableOrNot(digitalAsset.getDocument_version_id())) {
                            offLine_files_repository.deleteAlreadydownloadedFile(digitalAsset.getDocument_version_id());
                            String filepath = offLine_files_repository.getFilePathFromLocalTable(digitalAsset.getDocument_version_id());
                            if(filepath != null && !filepath.isEmpty())
                            {
                                CommonFunctions.deleteFileFromInternalStorage(filepath);
                            }
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

            ArrayList<EditDocumentResponse> documentResponses = new ArrayList<>();
            final EditDocumentPropertiesRequest editDocumentPropertiesRequest = new EditDocumentPropertiesRequest(object_id,name,doc_created,auth, documentResponses);

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


    public void initiateMoveAction(String actionName) {
        GlobalVariables.isMoveInitiated = true;
        GlobalVariables.selectedActionName =  actionName;
        Intent intent = new Intent(context, NavigationMyFolderActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("ObjectId", "0");
        startActivity(intent);

    }


    @Override
    public void onBackPressed()
    {
        if(isFromStatus400)
        {
            if(getSupportActionBar().getDisplayOptions() != 0)
            {
                super.onBackPressed();
            }
        }
        else
        {
            super.onBackPressed();
        }


    }


    private void getDocumentDetailsBasedOnDocument(String pushNotificationDocumentVersionId, String documentShareType)
    {
        if (NetworkUtils.isNetworkAvailable(context)) {
            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            final DocumentPropertiesService documentPropertiesService = retrofitAPI.create(DocumentPropertiesService.class);
            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();

            DocumentPropertiesRequest documentPropertiesRequest = new DocumentPropertiesRequest(pushNotificationDocumentVersionId);
            String request = new Gson().toJson(documentPropertiesRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            Call call = documentPropertiesService.getdocumentprop(params, PreferenceUtils.getAccessToken(context));

            call.enqueue(new Callback<ListPinDevicesResponse<DocumentPropertiesResponse>>() {
                @Override
                public void onResponse(Response<ListPinDevicesResponse<DocumentPropertiesResponse>> response, Retrofit retrofit) {
                    ListPinDevicesResponse apiResponse = response.body();
                    if (apiResponse != null) {
                        transparentProgressDialog.dismiss();
                        if (apiResponse.status.getCode() == Boolean.FALSE) {

                            documentPropertiesResponse = response.body().getData();

                            getDocumentViewUrl(documentPropertiesResponse, pushNotificationDocumentVersionId, documentShareType);


                        } else if (apiResponse.status.getCode() instanceof Integer) {

                            int status_value = new Integer(apiResponse.status.getCode().toString());
                            if(status_value == 401)
                            {
                                String mMessage = apiResponse.status.getMessage().toString();
                                showSessionExpiryAlert(mMessage);

                            }

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


    private void getDocumentViewUrl(List<DocumentPropertiesResponse> documentPropertiesResponse, String pushNotificationDocumentVersionId, String documentShare)
    {
        if (NetworkUtils.isNetworkAvailable(context)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();

            final DocumentPreviewRequest mDocumentPreviewRequest = new DocumentPreviewRequest(pushNotificationDocumentVersionId);

            String request = new Gson().toJson(mDocumentPreviewRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final DocumentPreviewService mDocumentPreviewService = retrofitAPI.create(DocumentPreviewService.class);
            Call call = mDocumentPreviewService.getDocumentPreviews(params, PreferenceUtils.getAccessToken(context));

            call.enqueue(new Callback<PdfDocumentResponseModel>() {
                @Override
                public void onResponse(Response<PdfDocumentResponseModel> response, Retrofit retrofit) {
                    PdfDocumentResponseModel apiResponse = response.body();
                    if (apiResponse != null) {

                        transparentProgressDialog.dismiss();

                        if (apiResponse.getStatus().getCode() instanceof Boolean) {
                            if (apiResponse.getStatus().getCode() == Boolean.FALSE) {
                                transparentProgressDialog.dismiss();
                                PdfDocumentResponseModel getDocumentPreviewResponses = response.body();



                                playmode = 1;
                                String document_preview_url = getDocumentPreviewResponses.getData().getDocumentPdfUrl();

                                GetCategoryDocumentsResponse  categoryDocumentsResponse = new GetCategoryDocumentsResponse();
                                categoryDocumentsResponse.setObject_id(documentPropertiesResponse.get(0).getDocument_id());
                                categoryDocumentsResponse.setDocument_version_id(pushNotificationDocumentVersionId);
                                categoryDocumentsResponse.setName(documentPropertiesResponse.get(0).getDocument_name());
                                categoryDocumentsResponse.setFiletype(documentPropertiesResponse.get(0).getFiletype());
                                categoryDocumentsResponse.setFilesize(documentPropertiesResponse.get(0).getFilesize());
                                categoryDocumentsResponse.setCreated_date(documentPropertiesResponse.get(0).getCreation_date());
                                categoryDocumentsResponse.setCategory_id(documentPropertiesResponse.get(0).getCategory_id());
                                categoryDocumentsResponse.setVersion_number(documentPropertiesResponse.get(0).getVersion_number());
                                categoryDocumentsResponse.setIs_shared(documentPropertiesResponse.get(0).getIs_shared());
                                categoryDocumentsResponse.setVersion_count(documentPropertiesResponse.get(0).getVersion_count());

                                boolean isFromShare = false;
                                if(documentShare.equalsIgnoreCase("document_share"))
                                {
                                    isFromShare = true;
                                }



                                Intent intent = new Intent(context, PdfViewActivity.class);
                                intent.putExtra("mode",1);
                                intent.putExtra("url", document_preview_url);
                                intent.putExtra("documentDetails", categoryDocumentsResponse);
                                intent.putExtra("IsFromShare", isFromShare);
                                context.startActivity(intent);
                                finish();
                            }

                        } else if (apiResponse.getStatus().getCode() instanceof Double) {
                            transparentProgressDialog.dismiss();

                            double status_value = new Double(apiResponse.getStatus().getCode().toString());

                            if (status_value == 400.0)
                            {
                                boolean isFromShare = false;
                                if(documentShare.equalsIgnoreCase("document_share"))
                                {
                                    isFromShare = true;
                                }

                                isFromStatus400 = true;
                                GetCategoryDocumentsResponse categoryDocumentsResponse = new GetCategoryDocumentsResponse();
                                categoryDocumentsResponse.setObject_id(documentPropertiesResponse.get(0).getDocument_id());
                                categoryDocumentsResponse.setDocument_version_id(pushNotificationDocumentVersionId);
                                categoryDocumentsResponse.setName(documentPropertiesResponse.get(0).getDocument_name());
                                categoryDocumentsResponse.setFiletype(documentPropertiesResponse.get(0).getFiletype());
                                categoryDocumentsResponse.setFilesize(documentPropertiesResponse.get(0).getFilesize());
                                categoryDocumentsResponse.setCreated_date(documentPropertiesResponse.get(0).getCreation_date());
                                categoryDocumentsResponse.setCategory_id(documentPropertiesResponse.get(0).getCategory_id());
                                categoryDocumentsResponse.setVersion_number(documentPropertiesResponse.get(0).getVersion_number());
                                categoryDocumentsResponse.setIs_shared(documentPropertiesResponse.get(0).getIs_shared());
                                categoryDocumentsResponse.setVersion_count(documentPropertiesResponse.get(0).getVersion_count());


                                Intent intent = new Intent(context, PdfViewActivity.class);
                                intent.putExtra("isFrom_Status400",true);
                                intent.putExtra("documentDetails", categoryDocumentsResponse);
                                intent.putExtra("IsFromShare", isFromShare);
                                context.startActivity(intent);
                                finish();

                            }
                            else if (status_value == 401.0 ) {

                                String mMessage = apiResponse.getStatus().getMessage().toString();
                                showSessionExpiryAlert(mMessage);

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

    private void showSessionExpiryAlert(String mMessage)
    {
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
                startActivity(new Intent(context, LoginActivity.class));
            }
        });

        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

}
