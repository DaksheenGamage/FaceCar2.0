package com.example.facecar20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NewPin extends AppCompatActivity {

    EditText txtPin,txtCPin;
    Button btnPin;
    SharedPreferences prf;
    String USETTYPE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_pin);
        prf = getSharedPreferences("LogDetails",MODE_PRIVATE);
        txtCPin=findViewById(R.id.txtNewPinConfirm);
        txtPin=findViewById(R.id.txtNewPinPin);
        btnPin=findViewById(R.id.btnNewPinCreate);
        USETTYPE=prf.getString("LOGEDACC","");

        btnPin.setOnClickListener(btnPinListner);

    }

    private View.OnClickListener btnPinListner = new View.OnClickListener() {
        public void onClick(View view) {
            String pin  = txtPin    .getText().toString().trim();
            String cpin = txtCPin   .getText().toString().trim();

            if(!pin.equals("") && !cpin.equals("") && pin.length()==4 && cpin.length()==4){
                if(cpin.equals(pin)){
                    String username = prf.getString("USERNAME","");
                    SharedPreferences.Editor editor = prf.edit();
                    editor.putString("PIN"+username,pin);
                    editor.commit();

                    if(USETTYPE.equals("O")){
                        Intent intent = new Intent(getApplicationContext(), OwnerDashboard.class);
                        startActivity(intent);

                    }else if(USETTYPE.equals("M")){
                        Intent intent = new Intent(getApplicationContext(), MemberDashboard.class);
                        startActivity(intent);
                    }

                }else {
                    txtPin.setError("PINs don't match");
                    txtCPin.setError("PINs don't match");

                }

            }
            else {
                if(pin.equals("")){
                    txtPin.setError("PIN cannot be empty");

                }
                if(cpin.equals("")){
                    txtCPin.setError("confirm PIN cannot be empty");
                }

                if(pin.length()<4){
                    txtPin.setError("PIN must be 4 digits");
                }
                if(cpin.length()<4){
                    txtCPin.setError("PIN must be 4 digits");
                }
            }
        }
    };

    @Override
    public void onBackPressed() {}
}