
package com.mwc.docportal.pdf;

import android.graphics.PointF;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.mwc.docportal.pdf.listener.OnSingleTapTouchListener;
import com.mwc.docportal.pdf.listener.PdfSwipeUpDownListener;
import com.mwc.docportal.pdf.scroll.ScrollHandle;

import static com.mwc.docportal.pdf.util.Constants.Pinch.MAXIMUM_ZOOM;
import static com.mwc.docportal.pdf.util.Constants.Pinch.MINIMUM_ZOOM;


/**
 * This Manager takes care of moving the PDFView,
 * set its zoom track user actions.
 */
class DragPinchManager implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener, ScaleGestureDetector.OnScaleGestureListener, View.OnTouchListener {

    private PDFView pdfView;
    private AnimationManager animationManager;
    private boolean SinglePageViewer;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    public PdfSwipeUpDownListener pdfswipeupdown;

    private boolean isSwipeEnabled;
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;


    private OnSingleTapTouchListener onSingleTapTouchListener;

    private boolean swipeVertical;

    private boolean scrolling = false;
    private boolean scaling = false;

    public DragPinchManager(PDFView pdfView, AnimationManager animationManager) {
        this.pdfView = pdfView;
        this.animationManager = animationManager;
        this.isSwipeEnabled = false;
        this.SinglePageViewer = false;
        this.swipeVertical = pdfView.isSwipeVertical();
        gestureDetector = new GestureDetector(pdfView.getContext(), this);
        scaleGestureDetector = new ScaleGestureDetector(pdfView.getContext(), this);
        pdfView.setOnTouchListener(this);
    }

    public void enableDoubletap(boolean enableDoubletap) {
        if (enableDoubletap) {
            gestureDetector.setOnDoubleTapListener(this);
        } else {
            gestureDetector.setOnDoubleTapListener(null);
        }
    }

    public boolean isZooming() {
        return pdfView.isZooming();
    }

    private boolean isPageChange(float distance) {
        return Math.abs(distance) > Math.abs(pdfView.toCurrentScale(swipeVertical ? pdfView.getOptimalPageHeight() : pdfView.getOptimalPageWidth()) / 2);
    }

    public void setSwipeEnabled(boolean isSwipeEnabled) {
        this.isSwipeEnabled = isSwipeEnabled;
    }

    public void  setIsSinglePageViewer(boolean isSinglePageViewer){

        this.SinglePageViewer = isSinglePageViewer;
    }


    public void setSwipeVertical(boolean swipeVertical) {
        this.swipeVertical = swipeVertical;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if (onSingleTapTouchListener!=null){
            onSingleTapTouchListener.onSingletapTouch();
        }
        ScrollHandle ps = pdfView.getScrollHandle();
        if (ps != null && !pdfView.documentFitsView()) {
            if (!ps.shown()) {
                ps.show();
            } else {
                ps.hide();
            }
        }
        pdfView.performClick();
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        if (pdfView.getZoom() < pdfView.getMidZoom()) {
            pdfView.zoomWithAnimation(e.getX(), e.getY(), pdfView.getMidZoom());
        } else if (pdfView.getZoom() < pdfView.getMaxZoom()) {
            pdfView.zoomWithAnimation(e.getX(), e.getY(), pdfView.getMaxZoom());
        } else {
            pdfView.resetZoomWithAnimation();
        }
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        animationManager.stopFling();
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        scrolling = true;
        if (pdfswipeupdown!=null){
            pdfswipeupdown.onScroll();
        }
        if (isSwipeEnabled) {
            pdfView.moveRelativeTo(-distanceX, -distanceY);
        }
        if (!scaling || pdfView.doRenderDuringScale()) {
          pdfView.loadPageByOffset();
          return true;
        }
        return true;
    }

    public void onScrollEnd(MotionEvent event) {
        if (pdfswipeupdown!=null){
            pdfswipeupdown.onScrollEnding();
        }
        pdfView.loadPages();
        hideHandle();

    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        if (SinglePageViewer){

            int xOffset = (int) pdfView.getCurrentXOffset();
            int yOffset = (int) pdfView.getCurrentYOffset();
            animationManager.startFlingAnimation(xOffset,
                    yOffset, (int) (velocityX),
                    (int) (velocityY),
                    xOffset * (swipeVertical ? 2 : pdfView.getPageCount()), 0,
                    yOffset * (swipeVertical ? pdfView.getPageCount() : 2), 0);

              try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                    }
                }
                else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {

                        if (!pdfView.isZooming()){
                            onSwipeBottom();
                        }

                    } else {
                        if (!pdfView.isZooming()){
                            onSwipeTop();
                        }
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }

        }else {

            int xOffset = (int) pdfView.getCurrentXOffset();
            int yOffset = (int) pdfView.getCurrentYOffset();
            animationManager.startFlingAnimation(xOffset,
                    yOffset, (int) (velocityX),
                    (int) (velocityY),
                    xOffset * (swipeVertical ? 2 : pdfView.getPageCount()), 0,
                    yOffset * (swipeVertical ? pdfView.getPageCount() : 2), 0);


        }
        return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float dr = detector.getScaleFactor();
        float wantedZoom = pdfView.getZoom() * dr;
        if (wantedZoom < MINIMUM_ZOOM) {
            dr = MINIMUM_ZOOM / pdfView.getZoom();
        } else if (wantedZoom > MAXIMUM_ZOOM) {
            dr = MAXIMUM_ZOOM / pdfView.getZoom();
        }
        pdfView.zoomCenteredRelativeTo(dr, new PointF(detector.getFocusX(), detector.getFocusY()));
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        scaling = true;
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        pdfView.loadPages();
        hideHandle();
        scaling = false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        boolean retVal = scaleGestureDetector.onTouchEvent(event);
        retVal = gestureDetector.onTouchEvent(event) || retVal;
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (scrolling) {
                scrolling = false;
                onScrollEnd(event);
            }
        }
        return retVal;

//       return retVal;
 }

    private void hideHandle() {
        if (pdfView.getScrollHandle() != null && pdfView.getScrollHandle().shown()) {
            pdfView.getScrollHandle().hideDelayed();
        }
    }

    public void enableSingletap(OnSingleTapTouchListener onSingleTapTouchListener) {

        this.onSingleTapTouchListener = onSingleTapTouchListener;
    }

    public void onSwipeRight() {


    }

    public void onSwipeLeft() {
    }

    public void onSwipeTop() {

        if (pdfswipeupdown!=null){
            pdfswipeupdown.onSwipeUp();
        }

    }

    public void onSwipeBottom() {

        if (pdfswipeupdown!=null){
            pdfswipeupdown.onSwipeDown();
        }

    }

    public void setUpDownSwipe(PdfSwipeUpDownListener pdfSwipeUpDownListener) {

            pdfswipeupdown = pdfSwipeUpDownListener;


    }
}
