package com.swaas.mwc.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by harika on 10-07-2018.
 */

public class GetTermsPageContentResponse {

    @SerializedName("terms_url")
    @Expose
    private String terms_url;

    @SerializedName("terms_title")
    @Expose
    private String terms_title;

    @SerializedName("terms_body")
    @Expose
    private String terms_body;

    public String getTerms_url() {
        return terms_url;
    }

    public void setTerms_url(String terms_url) {
        this.terms_url = terms_url;
    }

    public String getTerms_title() {
        return terms_title;
    }

    public void setTerms_title(String terms_title) {
        this.terms_title = terms_title;
    }

    public String getTerms_body() {
        return terms_body;
    }

    public void setTerms_body(String terms_body) {
        this.terms_body = terms_body;
    }
}
