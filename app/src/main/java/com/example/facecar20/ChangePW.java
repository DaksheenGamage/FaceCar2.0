package com.example.facecar20;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class ChangePW extends AppCompatActivity {
    String URL="https://facecarserver.000webhostapp.com/facecar/updatePass.php";
    EditText password,c_password;
    String intReg,s_password;
    Context context;
    Button btnChange;
    SMS sms;
    SharedPreferences prf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context=this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_p_w);
        final Intent intent = getIntent();
        intReg = intent.getStringExtra("REG");
        sms=new SMS(ChangePW.this);
        prf = getSharedPreferences("LogDetails",MODE_PRIVATE);





        password= findViewById(R.id.txtChngPass);
        c_password=findViewById(R.id.txtConfChngPw);

        btnChange=findViewById(R.id.btnConfirmCP);

        //btnChange clickListener
        btnChange.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


                if(!password.getText().toString().trim().isEmpty() && !c_password.getText().toString().trim().isEmpty()){
                    if(password.getText().toString().trim().equals(c_password.getText().toString().trim())){
                        AlertDialog.Builder b = new AlertDialog.Builder(context);
                        b.setTitle("YOU ARE GOING TO CHANGE THE PW FOR THIS ACCOUNT. ARE YOU SURE?");
                        b.setPositiveButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int d) {

                            }
                        });
                        b.setNeutralButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int d) {
                                s_password = password.getText().toString().trim() ;
                                changePassword(context,intent.getStringExtra("ID"),s_password,URL,"Password changed successfully");
                                sms.sendProfSMS(intent.getStringExtra("NUMBER"),"account password","changed");
                                Intent intent = new Intent(getApplicationContext(), ProfileList.class);
                                intent.putExtra("REG",intReg);
                                startActivity(intent);

                            }
                        });
                        b.show();
                    }else {
                        password.setError("Passwords is not matching");
                        c_password.setError("Passwords is not matching");
                    }
                }else{
                    password.setError("Please enter a password");
                    c_password.setError("Please enter a password");
                }
            }
        });




    }

    public void backtolist(View view) {
        Intent intent= new Intent(this, ProfileList.class);
        intent.putExtra("REG",intReg);
        startActivity(intent);
    }

    //changePassword()
    //change existing password to new password
    public void changePassword(final Context context, final String id, final String password, String URL, final String prompt){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if(success.equals("1")) {
                                Toasty.success(context, prompt, Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e){
                            //Toasty.success(context,e.getMessage()).show();

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toasty.success(context,error.getMessage()).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("id",id);
                params.put("password",password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }
}