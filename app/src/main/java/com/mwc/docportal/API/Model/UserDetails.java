package com.mwc.docportal.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by harika on 26-06-2018.
 */

public class UserDetails {

    @SerializedName("mobilephone")
    @Expose
    private String mobilephone;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("memorable_date")
    @Expose
    private String memorable_date;

    @SerializedName("memorable_word")
    @Expose
    private String memorable_word;

    @SerializedName("terms")
    @Expose
    private String terms;

    @SerializedName("mwc_email")
    @Expose
    private String mwc_email;

    @SerializedName("eu_ftl_welcome_msg")
    @Expose
    private String eu_ftl_welcome_msg;

    @SerializedName("terms_pdf_url")
    @Expose
    private String terms_pdf_url;

    @SerializedName("default_terms_url")
    @Expose
    private String default_terms_url;

    public String getMobilephone() {
        return mobilephone;
    }

    public void setMobilephone(String mobilephone) {
        this.mobilephone = mobilephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMemorable_date() {
        return memorable_date;
    }

    public void setMemorable_date(String memorable_date) {
        this.memorable_date = memorable_date;
    }

    public String getMemorable_word() {
        return memorable_word;
    }

    public void setMemorable_word(String memorable_word) {
        this.memorable_word = memorable_word;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public String getMwc_email() {
        return mwc_email;
    }

    public void setMwc_email(String mwc_email) {
        this.mwc_email = mwc_email;
    }

    public String getEu_ftl_welcome_msg() {
        return eu_ftl_welcome_msg;
    }

    public void setEu_ftl_welcome_msg(String eu_ftl_welcome_msg) {
        this.eu_ftl_welcome_msg = eu_ftl_welcome_msg;
    }

    public String getTerms_pdf_url() {
        return terms_pdf_url;
    }

    public void setTerms_pdf_url(String terms_pdf_url) {
        this.terms_pdf_url = terms_pdf_url;
    }

    public String getDefault_terms_url() {
        return default_terms_url;
    }

    public void setDefault_terms_url(String default_terms_url) {
        this.default_terms_url = default_terms_url;
    }
}
