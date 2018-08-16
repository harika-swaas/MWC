
package com.swaas.mwc.pdf.source;

import android.content.Context;


import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;
import com.swaas.mwc.pdf.util.Util;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamSource implements DocumentSource {

    private InputStream inputStream;

    public InputStreamSource(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public PdfDocument createDocument(Context context, PdfiumCore core, String password) throws IOException {
        return core.newDocument(Util.toByteArray(inputStream), password);
    }
}
