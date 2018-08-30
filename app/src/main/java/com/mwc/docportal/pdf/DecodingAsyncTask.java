
package com.mwc.docportal.pdf;

import android.content.Context;
import android.os.AsyncTask;


import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;
import com.mwc.docportal.pdf.source.DocumentSource;


class DecodingAsyncTask extends AsyncTask<Void, Void, Throwable> {

    private boolean cancelled;

    private PDFView pdfView;

    private Context context;
    private PdfiumCore pdfiumCore;
    private PdfDocument pdfDocument;
    private String password;
    private DocumentSource docSource;
    private int firstPageIdx;
    private int pageWidth;
    private int pageHeight;

    DecodingAsyncTask(DocumentSource docSource, String password, PDFView pdfView, PdfiumCore pdfiumCore, int firstPageIdx) {
        this.docSource = docSource;
        this.firstPageIdx = firstPageIdx;
        this.cancelled = false;
        this.pdfView = pdfView;
        this.password = password;
        this.pdfiumCore = pdfiumCore;
        context = pdfView.getContext();
    }

    @Override
    protected Throwable doInBackground(Void... params) {
        try {
            pdfDocument = docSource.createDocument(context, pdfiumCore, password);
            // We assume all the pages are the same size
            pdfiumCore.openPage(pdfDocument, firstPageIdx);
            pageWidth = pdfiumCore.getPageWidth(pdfDocument, firstPageIdx);
            pageHeight = pdfiumCore.getPageHeight(pdfDocument, firstPageIdx);
            return null;
        } catch (Throwable t) {
            return t;
        }
    }

    @Override
    protected void onPostExecute(Throwable t) {
        if (t != null) {
            pdfView.loadError(t);
            return;
        }
        if (!cancelled) {
            pdfView.loadComplete(pdfDocument, pageWidth, pageHeight);
        }
    }

    @Override
    protected void onCancelled() {
        cancelled = true;
    }
}
