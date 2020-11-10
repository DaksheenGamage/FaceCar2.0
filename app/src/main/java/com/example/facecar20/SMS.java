package com.example.facecar20;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;

import androidx.core.app.ActivityCompat;

public class SMS {
    private Activity activity;
    public SMS(Activity a){
        activity=a;
        ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.SEND_SMS}, PackageManager.PERMISSION_GRANTED);
    }
    void sendReqSMS(String phoneNo,String reqType,String status){

        String str = "FACE CAR: Your request to "+reqType+" has been "+status+".";


        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNo,null,str,null,null);


    }
    void sendProfSMS(String phoneNo,String reqType,String status){

        String str = "FACE CAR: Your "+reqType+" has been "+status+" by the Owner of vehicle. Please contact owner for further details.";


        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNo,null,str,null,null);



    }
}
