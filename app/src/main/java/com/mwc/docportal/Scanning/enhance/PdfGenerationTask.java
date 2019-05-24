package com.mwc.docportal.Scanning.enhance;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.mwc.docportal.R;
import com.mwc.docportal.Scanning.model.Page;
import com.thegrizzlylabs.geniusscan.sdk.pdf.OCREngine;
import com.thegrizzlylabs.geniusscan.sdk.pdf.OCREngineConfiguration;
import com.thegrizzlylabs.geniusscan.sdk.pdf.OCREngineInput;
import com.thegrizzlylabs.geniusscan.sdk.pdf.OCREngineProgressListener;
import com.thegrizzlylabs.geniusscan.sdk.pdf.OCREngineResult;
import com.thegrizzlylabs.geniusscan.sdk.pdf.PDFDocument;
import com.thegrizzlylabs.geniusscan.sdk.pdf.PDFGenerator;
import com.thegrizzlylabs.geniusscan.sdk.pdf.PDFGeneratorConfiguration;
import com.thegrizzlylabs.geniusscan.sdk.pdf.PDFGeneratorError;
import com.thegrizzlylabs.geniusscan.sdk.pdf.PDFImageProcessor;
import com.thegrizzlylabs.geniusscan.sdk.pdf.PDFLogger;
import com.thegrizzlylabs.geniusscan.sdk.pdf.PDFPage;
import com.thegrizzlylabs.geniusscan.sdk.pdf.PDFSize;
import com.thegrizzlylabs.geniusscan.sdk.pdf.TextLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by guillaume on 25/10/16.
 */

public class PdfGenerationTask extends AsyncTask<Void, Integer, PDFGeneratorError> {

    public interface OnPdfGeneratedListener {
        void onPdfGenerated(PDFGeneratorError result);
    }

    private static final String TAG = PdfGenerationTask.class.getSimpleName();

    private final static PDFSize A4_SIZE = new PDFSize(8.27f, 11.69f); // Size of A4 in inches

    private Context context;
    private String outputFilePath;
    private List<Page> pages;
    private boolean isOCREnabled;
    private OnPdfGeneratedListener listener;
    private ProgressDialog progressDialog;

    public PdfGenerationTask(Context context, List<Page> pages, String outputFilePath, boolean isOCREnabled, OnPdfGeneratedListener listener) {
        this.context = context;
        this.outputFilePath = outputFilePath;
        this.pages = pages;
        this.isOCREnabled = isOCREnabled;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        if (isOCREnabled) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(100);
            progressDialog.setMessage("OCR in progress");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    @Override
    protected PDFGeneratorError doInBackground(Void... params) {
        Logger logger = new Logger();

        if (isOCREnabled) {
            try {
                copyTessdataFiles();
            } catch (IOException e) {
                throw new RuntimeException("Cannot copy tessdata");
            }
        }

        ArrayList<PDFPage> pdfPages = new ArrayList<>();
        int pageIndex = 0;
        for (Page page : pages) {
            final int pageProgress = pageIndex * 100 / pages.size();
            String imagePath = page.getEnhancedImage().getAbsolutePath(context);

            TextLayout textLayout = null;
            if (isOCREnabled) {
                OCREngineConfiguration ocrConfiguration = new OCREngineConfiguration(new ArrayList<>(Arrays.asList("eng")), getTessdataDirectory().getPath());
                OCREngine ocrEngine = OCREngine.create(ocrConfiguration, logger, new OCREngineProgressListener() {
                    @Override
                    public void updateProgress(int progress) {
                        publishProgress(pageProgress + progress / pages.size());
                    }
                });
                OCREngineResult result = ocrEngine.recognizeText(new OCREngineInput(imagePath));
                textLayout = result.getTextLayout();
            }

            // Export all pages in A4
            pdfPages.add(new PDFPage(imagePath, A4_SIZE, textLayout));
            pageIndex++;
        }

        // Here we don't protect the PDF document with a password
        PDFDocument pdfDocument = new PDFDocument("test", null, null, pdfPages);

        PDFGenerator generator = PDFGenerator.createWithDocument(pdfDocument, new PDFGeneratorConfiguration(null, false), new PDFNoopImageProcessor(), logger);
        return generator.generatePDF(outputFilePath);
    }

    @Override
    protected void onPostExecute(PDFGeneratorError result) {
        super.onPostExecute(result);
        listener.onPdfGenerated(result);
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (progressDialog != null) {
            progressDialog.setProgress(values[0]);
        }
    }

    private void copyTessdataFiles() throws IOException {
        File tessdataDir = getTessdataDirectory();

        if (tessdataDir.exists()) {
            return;
        }

        tessdataDir.mkdir();

        InputStream in = context.getResources().openRawResource(R.raw.eng);
        File engFile = new File(tessdataDir, "eng.traineddata");
        OutputStream out = new FileOutputStream(engFile);

        byte[] buffer = new byte[1024];
        int len = in.read(buffer);
        while (len != -1) {
            out.write(buffer, 0, len);
            len = in.read(buffer);
        }
    }

    private File getTessdataDirectory() {
        return new File(context.getExternalFilesDir(null), "tessdata");
    }

    private class Logger extends PDFLogger {
        @Override
        public void log(String debug) {
            Log.d(TAG, debug);
        }
    }

    private class PDFNoopImageProcessor extends PDFImageProcessor {
        @Override
        public String process(String inputFilePath) {
            return inputFilePath;
        }
    }
}
