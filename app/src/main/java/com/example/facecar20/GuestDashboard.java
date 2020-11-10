package com.example.facecar20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.facecar20.Camera.CameraAddFace;
import com.example.facecar20.Camera.CameraScanFace;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import es.dmoral.toasty.Toasty;

public class GuestDashboard extends AppCompatActivity {
    Context context;
    SharedPreferences prf ;
    String username;
    Button btnUnlock,btnAddFace,btnDelFace;
    InternetConnectivity ic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_dashboard);
        prf = getSharedPreferences("LogDetails",MODE_PRIVATE);

        btnAddFace=findViewById(R.id.btnAddFaceGuest);
        btnDelFace=findViewById(R.id.btnRemoveFaceGuest);
        btnUnlock=findViewById(R.id.btnUnlockGuest);
        username = prf.getString("USERNAME","");
        ic= new InternetConnectivity(getApplicationContext());

        final SharedPreferences.Editor editor = prf.edit();


        btnUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!ic.checkInternet()){
                    Toasty.error(getApplicationContext(),"No Internet Connection",Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(getApplicationContext(), CameraScanFace.class);
                    startActivity(intent);
                }
            }
        });

        btnAddFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!ic.checkInternet()){
                    Toasty.error(getApplicationContext(),"No Internet Connection",Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(getApplicationContext(), CameraAddFace.class);
                    intent.putExtra("mode","add");
                    startActivity(intent);
                }
            }
        });

        btnDelFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!ic.checkInternet()){
                    Toasty.error(getApplicationContext(),"No Internet Connection",Toast.LENGTH_SHORT).show();
                }else{
                    deleteFace(username,"Face");
                }

            }
        });

    }
    @Override
    public void onBackPressed() {
    }

    public void LogOut(View view) {
        context=this;
        SharedPreferences.Editor editor = prf.edit();

        editor.putString("LOGSTATUS","LOGGEDOUT");
        editor.putString("LOGEDACC","N");
        editor.commit();


        Intent intent = new Intent(this, MainActivity.class);
        Toasty.info(context,"SignOut Successfully", Toast.LENGTH_SHORT).show();
        startActivity(intent);

    }

    public void deleteFace(String user,String i){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReference().child(user).child(i+".jpg");

        storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Face Deleted", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Face Not Deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }
}