package com.example.facecar20;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    String loggedStat,dashboardType;
    private long back;
    private int count=0;

    @Override
    public void onBackPressed() {
        count++;

        if(back+2000>System.currentTimeMillis()&&count>1){

            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle("Do you really want to close the application?");
            b.setPositiveButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    count=0;
                }
            });
            b.setNeutralButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);;
                }
            });
            b.show();
        }
        Toasty.info(getApplicationContext(),"Press back again to exit",500).show();
        back=System.currentTimeMillis();

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{
                            Manifest.permission.CAMERA},
                    100);
        }

        SharedPreferences prf = getSharedPreferences("LogDetails",MODE_PRIVATE);
        loggedStat = prf.getString("LOGSTATUS","LOGGEDOUT");
        dashboardType = prf.getString("LOGEDACC","N");

        if(loggedStat.equals("LOGGED")){
            if(dashboardType.equals("O")){
                Intent intent = new Intent(this, OwnerDashboard.class);
                Toasty.info(getApplicationContext(),"Welcome Back", Toast.LENGTH_SHORT).show();
                startActivity(intent);

            }else if(dashboardType.equals("M")){
                Intent intent = new Intent(this, MemberDashboard.class);
                Toasty.info(getApplicationContext(),"Welcome Back", Toast.LENGTH_SHORT).show();
                startActivity(intent);

            }else{
//                Intent intent = new Intent(this, GuestDashboard.class);
//                Toasty.info(getApplicationContext(),"Welcome Back", Toast.LENGTH_SHORT).show();
//                startActivity(intent);
            }
        }

    }


    public void createProfile(View view){
        Intent intent = new Intent(this, CreateProfile.class);
        startActivity(intent);
    }

    public void login(View view) {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    public void guest(View view) {
        Intent intent = new Intent(this, GuestActivity.class);
        startActivity(intent);
    }



}