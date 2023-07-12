package com.example.a12thproject;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.a12thproject.activities.MainActivity;

public class SmsManager
{

    public static String sendSms(Context c, String phoneNumber, String message) {
        // check if the phoneNumber is valid
        if (phoneNumber.length() < 10 || phoneNumber.length() > 13 || message.length() == 0)
            return null;
        // check if we have permission to send sms
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(c.checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
             //   ActivityCompat.requestPermissions((Activity) c, new String[]{Manifest.permission.SEND_SMS}, 1);
                return "";
            }
        }
        // send the sms
        try {
            android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
        } catch (Exception e) {
            Toast.makeText(c, "Failed to send SMS", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return null;
        }
        return message;
    }



}
