package com.swaas.mwc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.swaas.mwc.Fragments.FTLPinVerificationFragment;

/**
 * Created by barath on 7/12/2018.
 */

public class ReadSms extends BroadcastReceiver {

    SmsMessage currentMessage;
    final SmsManager sms = SmsManager.getDefault();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == "android.provider.Telephony.SMS_RECEIVED") {

            final Bundle bundle = intent.getExtras();

            try {
                if (bundle != null) {
                    final Object[] pdusObj = (Object[]) bundle.get("pdus");
                    for (int i = 0; i < pdusObj.length; i++) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            String format = bundle.getString("format");
                            currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i], format);
                        } else {
                            currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                        }

                        String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                        String senderNum = phoneNumber;
                        String message = currentMessage.getDisplayMessageBody();

                        try {
                            if (senderNum.equals("YourPIN")) {
                                FTLPinVerificationFragment.receivedSms(message);
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
    }
}
