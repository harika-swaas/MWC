package com.mwc.docportal.ForgotPassword;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyForgotPasswordBroadCastReceiver extends BroadcastReceiver
{

    private OTPMessageReceiveListener otpReceiverData;

    public void setOTPListener(OTPMessageReceiveListener otpListener) {
        this.otpReceiverData = otpListener;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            if(extras != null)
            {
                Status status =(Status) extras.get(SmsRetriever.EXTRA_STATUS);
                switch(status.getStatusCode()) {
                    case CommonStatusCodes.SUCCESS :
                        // Get SMS message contents
                        String message =(String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);

                        if (otpReceiverData != null) {

                            Pattern pattern = Pattern.compile("(\\d{8})");
                            //   \d is for a digit
                            //   {} is the number of digits here 8.
                            Matcher matcher = pattern.matcher(message);
                            String pinNumber = "";
                            if (matcher.find()) {
                                pinNumber = matcher.group(0);  // 4 digit number
                            }
                            otpReceiverData.onOTPMessageReceived(pinNumber);
                        }

                        break;
                    case CommonStatusCodes.TIMEOUT :
                        // Waiting for SMS timed out (5 minutes)
                        // Handle the error ...
                        if(otpReceiverData != null)
                        {
                            otpReceiverData.onOTPMessageTimeOut();
                        }

                        break;

                }
            }

        }
    }

    public interface OTPMessageReceiveListener
    {
        void onOTPMessageReceived(String otp);
        void onOTPMessageTimeOut();
    }

}
