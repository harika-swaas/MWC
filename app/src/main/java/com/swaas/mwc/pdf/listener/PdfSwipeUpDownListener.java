package com.swaas.mwc.pdf.listener;

/**
 * Created by Hariharan on 15/6/17.
 */

public interface PdfSwipeUpDownListener {

    public void  onSwipeUp();
    public void  onSwipeDown();
    public void onScroll();
    public void onScrollEnding();

}
