package com.mwc.docportal;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.mwc.docportal.Fragments.FTLPinVerificationFragment;

/**
 * Created by barath on 7/12/2018.
 */

public class ReadSms extends BroadcastReceiver {

    SmsMessage currentMessage;
    final SmsManager sms = SmsManager.getDefault();

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int readSMSPersmission = ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS);
            if (readSMSPersmission == PackageManager.PERMISSION_GRANTED) {
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
                                    //    FTLPinVerificationFragment.receivedSms(message);
                                    }
                                } catch (Exception e) {
                                }
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            } else {
                Toast.makeText(context, "Cannot read SMS automatically, because SMS read permission denied",Toast.LENGTH_LONG).show();
            }
        } else {
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
                                //    FTLPinVerificationFragment.receivedSms(message);
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


}
