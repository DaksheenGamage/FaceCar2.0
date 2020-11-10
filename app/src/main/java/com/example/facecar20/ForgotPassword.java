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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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

public class ForgotPassword extends AppCompatActivity {
    TextView email,reg;
    String semail,sreg;
    ProgressBar loading;
    Context context;
    Button btnBack,btnForgot;
    String URL="https://facecarserver.000webhostapp.com/facecar/forgotpw.php";
    InternetConnectivity ic;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        email = findViewById(R.id.txtFPEmail);
        reg = findViewById(R.id.txtFPReg);
        ic=new InternetConnectivity(getApplicationContext());
        loading=findViewById(R.id.progressBarForgotPw);
        btnBack=findViewById(R.id.btnFPRequest);
        btnForgot=findViewById(R.id.btnFPback);

    }

    public void backFP(View view) {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    public void forgotPWreq(View view) {
        if(!ic.checkInternet()){
            Toasty.error(getApplicationContext(),"No Internet connection",Toast.LENGTH_SHORT).show();
        }else{
            context=this;
            semail=email.getText().toString().trim();
            sreg=reg.getText().toString().trim();

            if(!semail.isEmpty() && !sreg.isEmpty()){
                loading.setVisibility(View.VISIBLE);
                btnForgot.setEnabled(false);
                btnBack.setEnabled(false);

                reqFPW(semail,sreg,URL,context);

            }
            else {
                if(semail.isEmpty()){
                    email.setError("Please enter an email");
                }
                if(sreg.isEmpty()){
                    reg.setError("Enter the Registration number of the vehicle");
                }
            }

        }

    }

    //reqFPW() method
    //send the request for forget password to the database
    public void reqFPW(final String email, final String reg, String URL, final Context context){
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST,URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if(success.equals("1")) {
                                String successType = jsonObject.getString("message");
                                if(successType.equals("success_owner")){

                                    String prompt="Email has been sent to your email successfully.";
                                    AlertDialog.Builder b = new AlertDialog.Builder(context);
                                    b.setTitle(prompt);
                                    b.setMessage("Please check inbox and follow the instructions.");
                                    b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int d) {
                                            Intent intent = new Intent(context, Login.class);
                                            startActivity(intent);  
                                        }
                                    });
                                    b.show();


                                }else{
                                    String prompt="Request has been sent to the owner of the vehicle";

                                    AlertDialog.Builder b = new AlertDialog.Builder(context);
                                    b.setTitle(prompt);
                                    b.setMessage("Once your request is accepted, you can use your email as the password to login.");
                                    b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int d) {
                                            Intent intent = new Intent(context, Login.class);
                                            startActivity(intent);
                                        }
                                    });
                                    b.show();
                                }

                            }
                            else {
                                loading.setVisibility(View.INVISIBLE);
                                btnForgot.setEnabled(true);
                                btnBack.setEnabled(true);
                                Toasty.error(context, "there is no matching account",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, Login.class);
                                startActivity(intent);
                            }
                        }
                        catch (JSONException e){
                            loading.setVisibility(View.INVISIBLE);
                            btnForgot.setEnabled(true);
                            btnBack.setEnabled(true);
                            Toasty.error(context,e.getMessage()).show();

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
                params.put("email",email);
                params.put("reg",reg);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }
}