package com.example.facecar20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Settings extends AppCompatActivity {
    Button btnBack,btnPIN,btnDetails,btnCarModule;
    SharedPreferences prf;
    String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        prf= getSharedPreferences("LogDetails",MODE_PRIVATE);
        userType=prf.getString("LOGEDACC","");

        btnBack=findViewById(R.id.btnSettingsBack);
        btnDetails=findViewById(R.id.btnSettingsChangeDetails);
        btnPIN=findViewById(R.id.btnSettingsChangePIN);
        btnCarModule=findViewById(R.id.btnSettingsRaspberryPi);
        if(userType.equals("O")){
            btnCarModule.setVisibility(View.VISIBLE);
        }

        btnCarModule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CarModuleSettings.class);
                startActivity(intent);
            }
        });



        btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChangeDetails.class);
                startActivity(intent);

            }
        });

        btnPIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userType.equals("O")){
                    Intent intent = new Intent(getApplicationContext(), ChangePin.class);
                    intent.putExtra("USERTYPE","O");
                    startActivity(intent);

                }else if(userType.equals("M")){
                    Intent intent = new Intent(getApplicationContext(), ChangePin.class);
                    intent.putExtra("USERTYPE","M");
                    startActivity(intent);
                }

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userType.equals("O")){
                    Intent intent = new Intent(getApplicationContext(), OwnerDashboard.class);
                    startActivity(intent);

                }else if(userType.equals("M")){
                    Intent intent = new Intent(getApplicationContext(), MemberDashboard.class);
                    startActivity(intent);
                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        if(userType.equals("O")){
            Intent intent = new Intent(getApplicationContext(), OwnerDashboard.class);
            startActivity(intent);

        }else if(userType.equals("M")){
            Intent intent = new Intent(getApplicationContext(), MemberDashboard.class);
            startActivity(intent);
        }
    }

}