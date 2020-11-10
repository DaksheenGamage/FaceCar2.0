package com.example.facecar20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.facecar20.Camera.CameraAddFace;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class EnterPin extends AppCompatActivity {
    EditText txtPin;
    Button btnPin,btnCancel;
    SharedPreferences prf;
    String PINTYPE,USERTYPE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pin);
        Intent i = getIntent();
        PINTYPE = i.getStringExtra("PINTYPE");
        USERTYPE = i.getStringExtra("USERTYPE");

        prf = getSharedPreferences("LogDetails",MODE_PRIVATE);
        txtPin=findViewById(R.id.txtEnterPIN);
        btnPin=findViewById(R.id.btnEnterPinOK);
        btnCancel=findViewById(R.id.btnEnterPinCancel);

        btnPin.setOnClickListener(btnPinListener);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(USERTYPE.equals("O")){
                    Intent intent = new Intent(getApplicationContext(), OwnerDashboard.class);
                    startActivity(intent);
                }else if(USERTYPE.equals("M")){
                    Intent intent = new Intent(getApplicationContext(), MemberDashboard.class);
                    startActivity(intent);
                }
            }
        });

    }
    private View.OnClickListener btnPinListener = new View.OnClickListener() {
        public void onClick(View view) {
            String pin  = txtPin    .getText().toString().trim();

            if(!pin.equals("") && pin.length()==4){
                String username = prf.getString("USERNAME","");
                String userPin=prf.getString("PIN"+username,"");

                if(!userPin.equals("")){
                    if(pin.equals(userPin)){
                        if(PINTYPE.equals("ADD")){
                            Intent intent = new Intent(getApplicationContext(), CameraAddFace.class);
                            intent.putExtra("mode","add");
                            startActivity(intent);

                        }else if(PINTYPE.equals("DEL")){
                            deleteFace(username,"Face");
                        }
                    }
                    else{
                        txtPin.setError("Wrong PIN");
                    }
                }
            }
            else {
                if(pin.equals("")){
                    txtPin.setError("PIN cannot be empty");

                }

                if(pin.length()<4){
                    txtPin.setError("PIN must be 4 digits");
                }
            }
        }
    };

    //deleteFace() method
    //delete the relevant face image
    public void deleteFace(String user,String i){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReference().child(user).child(i+".jpg");

        storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Face Deleted", Toast.LENGTH_SHORT).show();
                if(USERTYPE.equals("O")){
                    Intent intent = new Intent(getApplicationContext(), OwnerDashboard.class);
                    startActivity(intent);
                }else if(USERTYPE.equals("M")){
                    Intent intent = new Intent(getApplicationContext(), MemberDashboard.class);
                    startActivity(intent);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Face Not Deleted", Toast.LENGTH_SHORT).show();
                if(USERTYPE.equals("O")){
                    Intent intent = new Intent(getApplicationContext(), OwnerDashboard.class);
                    startActivity(intent);
                }else if(USERTYPE.equals("M")){
                    Intent intent = new Intent(getApplicationContext(), MemberDashboard.class);
                    startActivity(intent);
                }

            }
        });
    }
    @Override
    public void onBackPressed() {}
}