package com.swaas.mwc.FTL;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.swaas.mwc.R;
import com.swaas.mwc.Utils.Constants;
import com.wang.avi.AVLoadingIndicatorView;

/**
 * Created by harika on 04-07-2018.
 */

public class WebviewLoaderTermsActivity extends AppCompatActivity {

    private WebView mHelpWebview;
    ProgressBar mProgressBar;
    private AVLoadingIndicatorView mAviLoadingIndicatorView;
    String mUrl, mTermsPageContentUrl, mAssistanceHelpGuideUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview_loader_terms);

        intializeViews();
        getIntentData();
        loadWebView();
    }

    private void loadWebView() {
        mHelpWebview.getSettings().setAppCachePath( getApplicationContext().getCacheDir().getAbsolutePath());
        mHelpWebview.getSettings().setAllowFileAccess(true);
        mHelpWebview.getSettings().setAppCacheEnabled(true);
        mHelpWebview.getSettings().setJavaScriptEnabled(true);
        mHelpWebview.getSettings().setUseWideViewPort(true);
        if (Build.VERSION.SDK_INT >= 19) {
            mHelpWebview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            mHelpWebview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        mHelpWebview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

        if(!TextUtils.isEmpty(mUrl)) {
            mHelpWebview.loadUrl(mUrl);
        } else if (!TextUtils.isEmpty(mTermsPageContentUrl)) {
            mHelpWebview.loadUrl(mTermsPageContentUrl);
        } else if (!TextUtils.isEmpty(mAssistanceHelpGuideUrl)) {
            mHelpWebview.loadUrl(mAssistanceHelpGuideUrl);
        }

        mAviLoadingIndicatorView.show();

        WebViewClient webViewClient = new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                Log.d("<==>Loading","Loading");
                mAviLoadingIndicatorView.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                mHelpWebview.setVisibility(View.VISIBLE);
                mAviLoadingIndicatorView.hide();
                Log.d("<==>Finished","Finished");
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url){
                // do your handling codes here, which url is the requested url
                // probably you need to open that url rather than redirect:
                view.loadUrl(url);
                return true; // then it is not handled by default action
            }
        };
        mHelpWebview.setWebViewClient(webViewClient);
    }

    private void intializeViews() {
        mAviLoadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.avi);
        mHelpWebview = (WebView) findViewById(R.id.HelpWebview);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    private void getIntentData() {
        if(getIntent() != null){
            mUrl = getIntent().getStringExtra(Constants.SETTERMS);
            mTermsPageContentUrl = getIntent().getStringExtra(Constants.SETTERMSPAGECONTENTURL);
            mAssistanceHelpGuideUrl = getIntent().getStringExtra(Constants.SETASSISTANCEPOPUPCONTENTURL);
        }
    }
}
