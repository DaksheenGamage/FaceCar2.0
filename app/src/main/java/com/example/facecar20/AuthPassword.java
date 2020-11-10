package com.example.facecar20;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class AuthPassword extends AppCompatActivity {
    Button btnLogin,btnCancel;
    ProgressBar loading;
    EditText txtPass;
    String enteredPass,username,userType;
    SharedPreferences prf;
    String URL="https://facecarserver.000webhostapp.com/facecar/passCheck.php";
    InternetConnectivity ic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_password);

        //shared preferences
        prf= getSharedPreferences("LogDetails",MODE_PRIVATE);
        username=prf.getString("USERNAME","");
        userType=prf.getString("LOGEDACC","");



        btnLogin=findViewById(R.id.btnAuthPass_login);
        btnCancel=findViewById(R.id.btnAuthPass_cancel);
        loading=findViewById(R.id.progressBarAuth);
        txtPass=findViewById(R.id.txtAuthPassword);

        ic= new InternetConnectivity(getApplicationContext());


        btnLogin.setOnClickListener(btnLoginListner);

        btnCancel.setOnClickListener(btnCancelListner);

    }

//Button cancel listener
    private View.OnClickListener btnCancelListner = new View.OnClickListener() {
        public void onClick(View view) {
            if(userType.equals("O")){   //if logged account is an owner account
                Intent intent = new Intent(getApplicationContext(), OwnerDashboard.class);
                startActivity(intent);

            }else if(userType.equals("M")){ //if logged account is a member account
                Intent intent = new Intent(getApplicationContext(), MemberDashboard.class);
                startActivity(intent);
            }
        }
    };

//Button loign listener
    private View.OnClickListener btnLoginListner = new View.OnClickListener() {
        public void onClick(View view) {

            //check internet connection using checkInternet method in InternetConnectivity class
            if(!ic.checkInternet()){
                Toasty.error(getApplicationContext(),"No Internet Connection",Toast.LENGTH_SHORT).show();
            }else {
                enteredPass=txtPass.getText().toString().trim();
                loading.setVisibility(View.VISIBLE);
                btnLogin.setEnabled(false);
                btnCancel.setEnabled(false);
                login(username,enteredPass,URL); //calling login()
            }
        }
    };




    //login()
    // checks entered password with the password in the database. if password matches user will be redirected to settings activity.
    private void login(final String username, final String Password, String URL) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if(success.equals("1")){
                                Intent intent = new Intent(getApplicationContext(), Settings.class);
                                startActivity(intent);

                            }else{
                                txtPass.setError("Incorrect password");
                                loading.setVisibility(View.INVISIBLE);
                                btnCancel.setEnabled(true);
                                btnLogin.setEnabled(true);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            loading.setVisibility(View.INVISIBLE);
                            btnCancel.setEnabled(true);
                            btnLogin.setEnabled(true);
                            Toasty.error(getApplicationContext(),"ErrorCATCH"+e.toString(),Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(getApplicationContext(),"Server Error",Toast.LENGTH_SHORT).show();

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("password",Password);
                params.put("username",username);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

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