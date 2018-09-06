package com.mwc.docportal.Firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.mwc.docportal.Database.PushNotificatoinSettings_Respository;
import com.mwc.docportal.Preference.PreferenceUtils;

/**
 * Created by barath on 8/24/2018.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    //this method will be called
    //when the token is generated
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        //now we will have the token
        String token = FirebaseInstanceId.getInstance().getToken();

        PushNotificatoinSettings_Respository pushNotificatoinSettings_respository = new PushNotificatoinSettings_Respository(getApplicationContext());
        pushNotificatoinSettings_respository.insertIntoPushNotificatonTable(token);

        //for now we are displaying the token in the log
        //copy it as this method is called only when the new token is generated
        //and usually new token is only generated when the app is reinstalled or the data is cleared
        Log.d("MyRefreshedToken", token);
    }

}
