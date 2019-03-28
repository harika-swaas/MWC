package com.mwc.docportal.pdf.pdfasync;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by ubuntu on 16/5/17.
 */

public class DownloadPdfAysnc extends AsyncTask<String,Void,String> {

    public Context mContext;
    public String resultpath;
    public OnPdfDownload onpdfdownload;

    public DownloadPdfAysnc(Context context, OnPdfDownload onpdfdownloaded){

        this.mContext = context;
        this.onpdfdownload = onpdfdownloaded;


    }


    @Override
    protected String doInBackground(String... params) {

        try
        {
            URL url = new URL(params[0]);

            URLConnection ucon = url.openConnection();
            ucon.setReadTimeout(100000);
            ucon.setConnectTimeout(100000);

            InputStream is = ucon.getInputStream();
            BufferedInputStream inStream = new BufferedInputStream(is, 1024 * 5);



            File file = new File(mContext.getDir("filesdir", Context.MODE_PRIVATE) + "/"+params[1]+".pdf");

            if (file.exists())
            {
                resultpath = file.getPath();

            }else {

                file.createNewFile();

                FileOutputStream outStream = new FileOutputStream(file);
                byte[] buff = new byte[5 * 1024];

                int len;
                while ((len = inStream.read(buff)) != -1)
                {
                    outStream.write(buff, 0, len);
                }

                outStream.flush();
                outStream.close();
                inStream.close();

                resultpath =  file.getPath();


            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }

        return resultpath;
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        onpdfdownload.onPdfDownloaded(s);
    }
}
