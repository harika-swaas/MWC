package com.mwc.docportal.Firebase;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

import java.util.Objects;

public class FirebaseDataReceiver extends WakefulBroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        try{
            for (String key : Objects.requireNonNull(intent.getExtras()).keySet()) {
                if(key.equalsIgnoreCase("Lang")){
                    Object lang= Objects.requireNonNull(intent.getExtras()).get(key);
                    String language= String.valueOf(lang);

                }
                if(key.equalsIgnoreCase("from")){
                    Object value = intent.getExtras().get(key);


                }else{
                    Log.e("FirebaseDataReceiver", "Key: " + key);
                }
            }

        }catch (Exception e){
            e.getMessage();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}
