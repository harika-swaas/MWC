package com.mwc.docportal.API.Model.UserProfileUpdateModel;

public class UserProfileRequestDataModel
{
    String username;
    String title;
    String firstname;
    String surname;
    String email;
    String workphone;
    String workphone_extension;
    String mobilephone;
    String address_line1;
    String address_line2;
    String country;
    String postcode;
    String town;

    public UserProfileRequestDataModel(String userName, String title, String firstName, String surname, String email, String workPhone, String extension,
                                       String mobilePhone, String address1, String address2, String town, String postCode, String country){
        this.username = userName;
        this.title = title;
        this.firstname = firstName;
        this.surname = surname;
        this.email = email;
        this.workphone = workPhone;
        this.workphone_extension = extension;
        this.mobilephone = mobilePhone;
        this.address_line1 = address1;
        this.address_line2 = address2;
        this.town = town;
        this.postcode = postCode;
        this.country = country;

    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWorkphone() {
        return workphone;
    }

    public void setWorkphone(String workphone) {
        this.workphone = workphone;
    }

    public String getWorkphone_extension() {
        return workphone_extension;
    }

    public void setWorkphone_extension(String workphone_extension) {
        this.workphone_extension = workphone_extension;
    }

    public String getMobilephone() {
        return mobilephone;
    }

    public void setMobilephone(String mobilephone) {
        this.mobilephone = mobilephone;
    }

    public String getAddress_line1() {
        return address_line1;
    }

    public void setAddress_line1(String address_line1) {
        this.address_line1 = address_line1;
    }

    public String getAddress_line2() {
        return address_line2;
    }

    public void setAddress_line2(String address_line2) {
        this.address_line2 = address_line2;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }
}
