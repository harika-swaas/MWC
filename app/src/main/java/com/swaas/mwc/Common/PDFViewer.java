package com.swaas.mwc.Common;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.swaas.mwc.R;
import com.swaas.mwc.Utils.Constants;

import es.voghdev.pdfviewpager.library.PDFViewPager;

/**
 * Created by barath on 7/26/2018.
 */

public class PDFViewer extends AppCompatActivity {

    String mDocumentPDFUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

        mDocumentPDFUrl = getIntent().getStringExtra(Constants.DOCUMENTPDFURL);
      //  PDFViewPager pdfView = new PDFViewPager(this, "http://172.16.40.50/data/temp/testdocument.pdf");

        PDFViewPager pdfView = new PDFViewPager(this, mDocumentPDFUrl);

       // pdfView.fromAsset("http://172.16.40.50/data/temp/testdocument.pdf").load();
    }
}
