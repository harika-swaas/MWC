package com.swaas.mwc.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by harika on 26-06-2018.
 */

public class SetTermsAcceptanceResponse {

    @SerializedName("terms_accept")
    @Expose
    private int terms_accept;

    public int getTerms_accept() {
        return terms_accept;
    }

    public void setTerms_accept(int terms_accept) {
        this.terms_accept = terms_accept;
    }
}
