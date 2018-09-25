package com.mwc.docportal.Retrofit;

import android.os.Build;

import com.facebook.stetho.okhttp.StethoInterceptor;
import com.squareup.okhttp.OkHttpClient;
import com.mwc.docportal.BuildConfig;
import com.mwc.docportal.Utils.Constants;

import java.util.concurrent.TimeUnit;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by harika on 22-06-2018.
 */

public class RetrofitAPIBuilder {

    static Retrofit retrofit = null;
    static Retrofit sharedRetrofit = null;

    public static synchronized Retrofit getInstance() {

        /*String UA = System.getProperty("http.agent");*/

        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        String versionRelease = Build.VERSION.RELEASE;
        String MwcApp = "MWCApp";

        String UA = "(MWCAndroid)"+"("+MwcApp + " " + versionName+"["+versionCode+"]"+")"+"(Android" + " " + versionRelease+")";
      //  String UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36";

        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(150, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(150, TimeUnit.SECONDS);
        okHttpClient.networkInterceptors().add(new StethoInterceptor());
        okHttpClient.interceptors().add(new UserAgentInterceptor(UA));

        if(retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.COMPANY_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
        return retrofit;
    }


    public static synchronized Retrofit getUploadInstance() {

        /*String UA = System.getProperty("http.agent");*/

        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        String versionRelease = Build.VERSION.RELEASE;
        String MwcApp = "MWCApp";

        String UA = "(MWCAndroid)"+"("+MwcApp + " " + versionName+"["+versionCode+"]"+")"+"(Android" + " " + versionRelease+")";
        //  String UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36";

        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(600, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(600, TimeUnit.SECONDS);
        okHttpClient.networkInterceptors().add(new StethoInterceptor());
        okHttpClient.interceptors().add(new UserAgentInterceptor(UA));

        if(sharedRetrofit == null) {
            sharedRetrofit = new Retrofit.Builder()
                    .baseUrl(Constants.COMPANY_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
        return sharedRetrofit;
    }


}
