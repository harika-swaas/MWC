package com.swaas.mwc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.swaas.mwc.Fragments.FTLPinVerificationFragment;

/**
 * Created by barath on 7/12/2018.
 */

public class ReadSms extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent)
    {

        final Bundle bundle = intent.getExtras();
        try {

            if (bundle != null)
            {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdusObj.length; i++)
                {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String senderNum = phoneNumber ;
                    String message = currentMessage .getDisplayMessageBody();

                    try
                    {

                        if (senderNum.equals("YourPIN"))
                        {

                            FTLPinVerificationFragment Sms = new FTLPinVerificationFragment();
                            Sms.recivedSms(message);
                        }
                    } catch(Exception e){}
                }
            }

        }
        catch (Exception e)
        {

        }
    }

}
