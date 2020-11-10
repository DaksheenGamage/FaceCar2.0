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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ChangePin extends AppCompatActivity {
    Button btnChange,btnCancel;
    EditText oldPin,newPin,conPin;
    SharedPreferences prf;
    String USERTYPE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pin);
        Intent i = getIntent();
        USERTYPE = i.getStringExtra("USERTYPE");

        prf = getSharedPreferences("LogDetails",MODE_PRIVATE);
        oldPin=findViewById(R.id.txtChangePIN_oldPin);
        newPin=findViewById(R.id.txtChangePin_newpin);
        conPin=findViewById(R.id.txtChangePin_confirmpin);
        btnCancel=findViewById(R.id.btnChangePin_cancel);
        btnChange=findViewById(R.id.btnChangePin_change);


        btnChange.setOnClickListener(btnChangeListner);

        //button cancel Listener
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Settings.class);
                startActivity(intent);
            }
        });

    }

    //button change Listener
    //changes old pin to entered new pin
    private View.OnClickListener btnChangeListner = new View.OnClickListener() {
        public void onClick(View view) {
            String oldpin  = oldPin    .getText().toString().trim();
            String newpin   = newPin.getText().toString().trim();
            String conpin = conPin.getText().toString().trim();

            if(!oldpin.equals("") && oldpin.length()==4 && !newpin.equals("") && newpin.length()==4 && !conpin.equals("") && conpin.length()==4){
                String username = prf.getString("USERNAME","");
                String userPin=prf.getString("PIN"+username,"");

                if(newpin.equals(conpin)){
                    if(oldpin.equals(userPin)){
                        SharedPreferences.Editor editor = prf.edit();
                        editor.putString("PIN"+username,newpin);
                        editor.commit();
                        Toast.makeText(getApplicationContext(), "PIN Changed", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), Settings.class);
                        startActivity(intent);
                    }
                    else{
                        oldPin.setError("Wrong PIN");
                    }
                }else{
                    newPin.setError("PINs don't match");
                    conPin.setError("PINs don't match");
                }
            }
            else {
                if(newpin.equals("")){
                    newPin.setError("PIN cannot be empty");

                }

                if(newpin.length()<4){
                    newPin.setError("PIN must be 4 digits");
                }
                if(oldpin.equals("")){
                    oldPin.setError("PIN cannot be empty");

                }

                if(oldpin.length()<4){
                    oldPin.setError("PIN must be 4 digits");
                }
                if(conpin.equals("")){
                    conPin.setError("PIN cannot be empty");

                }

                if(conpin.length()<4){
                    conPin.setError("PIN must be 4 digits");
                }
            }
        }
    };
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Settings.class);
        startActivity(intent);
    }
}