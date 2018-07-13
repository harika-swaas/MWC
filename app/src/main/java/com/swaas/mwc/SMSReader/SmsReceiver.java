package com.swaas.mwc.SMSReader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import static android.R.attr.format;

/**
 * Created by harika on 12-07-2018.
 */

public class SmsReceiver extends BroadcastReceiver {

    private static SmsListener mListener;
    Boolean smsReaderCheck;
    String otp, xyz;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data = intent.getExtras();
        Object[] pdus = (Object[]) data.get("pdus");
        for (int i = 0; i < pdus.length; i++) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
            String sender = smsMessage.getDisplayOriginatingAddress();
            // b=sender.endsWith("WNRCRP");  //Just to fetch otp sent from WNRCRP
            String messageBody = smsMessage.getMessageBody();
            otp = messageBody.replaceAll("[^0-9]", "");   // here abcd contains otp
            //Pass on the text to our listener.
            if (smsReaderCheck == true) {
                mListener.messageReceived(otp);  // attach value to interface
            } else {
            }
        }
    }

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }
}
