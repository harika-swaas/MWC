package com.swaas.mwc.API.Model;

/**
 * Created by harika on 09-07-2018.
 */

public class AccountSettingsResponse {

    private String User_Id;
    private String User_Name;
    private String Email_id;
    private String Company_Name;
    private String Access_Token;
    private String Help_Guide_URL;
    private String Terms_URL;
    private String Is_Terms_Accepted;
    private String Is_Help_Accepted;
    private String Login_Complete_Status;
    private String Is_Local_Auth_Enabled;
    private String Is_Push_Notification_Enabled;

    public String getUser_Id() {
        return User_Id;
    }

    public void setUser_Id(String user_Id) {
        User_Id = user_Id;
    }

    public String getUser_Name() {
        return User_Name;
    }

    public void setUser_Name(String user_Name) {
        User_Name = user_Name;
    }

    public String getEmail_id() {
        return Email_id;
    }

    public void setEmail_id(String email_id) {
        Email_id = email_id;
    }

    public String getCompany_Name() {
        return Company_Name;
    }

    public void setCompany_Name(String company_Name) {
        Company_Name = company_Name;
    }

    public String getAccess_Token() {
        return Access_Token;
    }

    public void setAccess_Token(String access_Token) {
        Access_Token = access_Token;
    }

    public String getHelp_Guide_URL() {
        return Help_Guide_URL;
    }

    public void setHelp_Guide_URL(String help_Guide_URL) {
        Help_Guide_URL = help_Guide_URL;
    }

    public String getTerms_URL() {
        return Terms_URL;
    }

    public void setTerms_URL(String terms_URL) {
        Terms_URL = terms_URL;
    }

    public String getIs_Terms_Accepted() {
        return Is_Terms_Accepted;
    }

    public void setIs_Terms_Accepted(String is_Terms_Accepted) {
        Is_Terms_Accepted = is_Terms_Accepted;
    }

    public String getIs_Help_Accepted() {
        return Is_Help_Accepted;
    }

    public void setIs_Help_Accepted(String is_Help_Accepted) {
        Is_Help_Accepted = is_Help_Accepted;
    }

    public String getLogin_Complete_Status() {
        return Login_Complete_Status;
    }

    public void setLogin_Complete_Status(String login_Complete_Status) {
        Login_Complete_Status = login_Complete_Status;
    }

    public String getIs_Local_Auth_Enabled() {
        return Is_Local_Auth_Enabled;
    }

    public void setIs_Local_Auth_Enabled(String is_Local_Auth_Enabled) {
        Is_Local_Auth_Enabled = is_Local_Auth_Enabled;
    }

    public String getIs_Push_Notification_Enabled() {
        return Is_Push_Notification_Enabled;
    }

    public void setIs_Push_Notification_Enabled(String is_Push_Notification_Enabled) {
        Is_Push_Notification_Enabled = is_Push_Notification_Enabled;
    }
}
