package com.swaas.mwc.API.Model;

/**
 * Created by harika on 10-07-2018.
 */

public class WhiteLabelResponse {

    private String Item_Selected_Color;
    private String Item_Unselected_Color;
    private String Splash_Screen_Color;
    private String Folder_Color;

    public String getItem_Selected_Color() {
        return Item_Selected_Color;
    }

    public void setItem_Selected_Color(String item_Selected_Color) {
        Item_Selected_Color = item_Selected_Color;
    }

    public String getItem_Unselected_Color() {
        return Item_Unselected_Color;
    }

    public void setItem_Unselected_Color(String item_Unselected_Color) {
        Item_Unselected_Color = item_Unselected_Color;
    }

    public String getSplash_Screen_Color() {
        return Splash_Screen_Color;
    }

    public void setSplash_Screen_Color(String splash_Screen_Color) {
        Splash_Screen_Color = splash_Screen_Color;
    }

    public String getFolder_Color() {
        return Folder_Color;
    }

    public void setFolder_Color(String folder_Color) {
        Folder_Color = folder_Color;
    }
}
