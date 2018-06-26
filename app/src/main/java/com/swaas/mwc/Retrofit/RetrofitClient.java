package com.swaas.mwc.Retrofit;

import com.facebook.stetho.okhttp.StethoInterceptor;
import com.squareup.okhttp.OkHttpClient;
import com.swaas.mwc.Utils.Constants;

import java.util.concurrent.TimeUnit;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by harika on 12-06-2018.
 */

public class RetrofitClient {

    static  Retrofit retrofit = null;
    public static synchronized Retrofit getInstance() {

        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(600, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(600, TimeUnit.SECONDS);
        okHttpClient.networkInterceptors().add(new StethoInterceptor());

        if(retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.COMPANY_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
        return retrofit;
    }
}
