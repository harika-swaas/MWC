
package com.swaas.mwc.pdf.listener;

/**
 * Implements this interface to receive events from PDFView
 * when a page has been scrolled
 */
public interface OnPageScrollListener {

    void onPageScrolled(int page, float positionOffset);
}
