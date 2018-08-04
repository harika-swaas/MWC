package com.swaas.mwc.API.Model;

/**
 * Created by harika on 09-07-2018.
 */

public class GetUserPreferencesResponse {

    private String preview_pane ;
    private String view_type;
    private String shared_preview_pane;
    private String shared_view_type;
    private String assistance_popup;

    public String getPreview_pane() {
        return preview_pane;
    }

    public void setPreview_pane(String preview_pane) {
        this.preview_pane = preview_pane;
    }

    public String getView_type() {
        return view_type;
    }

    public void setView_type(String view_type) {
        this.view_type = view_type;
    }

    public String getShared_preview_pane() {
        return shared_preview_pane;
    }

    public void setShared_preview_pane(String shared_preview_pane) {
        this.shared_preview_pane = shared_preview_pane;
    }

    public String getShared_view_type() {
        return shared_view_type;
    }

    public void setShared_view_type(String shared_view_type) {
        this.shared_view_type = shared_view_type;
    }

    public String getAssistance_popup() {
        return assistance_popup;
    }

    public void setAssistance_popup(String assistance_popup) {
        this.assistance_popup = assistance_popup;
    }
}
