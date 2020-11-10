package com.example.facecar20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class GuestActivity extends AppCompatActivity {

    Context context;
    EditText txtGuestReg;
    private static String URL_GUEST="https://facecarserver.000webhostapp.com/facecar/guestLogin.php";
    SharedPreferences prf;
    String reg;
    InternetConnectivity ic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest);
        txtGuestReg = findViewById(R.id.txtGuestRegNo);
        reg=txtGuestReg.getText().toString().trim().toUpperCase();
        prf= getSharedPreferences("LogDetails",MODE_PRIVATE);
        ic=new InternetConnectivity(getApplicationContext());

    }

    public void backGuest(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

    //login to guest account
    public void Login(View view) {

        if(!ic.checkInternet()){
            Toasty.error(getApplicationContext(),"No internet Connection",Toast.LENGTH_SHORT).show();
        }else{
            context=this;
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_GUEST,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String success = jsonObject.getString("success");
                                if(success.equals("1")) {
                                    final SharedPreferences.Editor editor = prf.edit();
                                    editor.putString("USERNAME","CAR"+reg+"GUEST");
                                    editor.commit();
                                    Intent intent = new Intent(context, GuestDashboard.class);
                                    Toasty.success(context,"Logged to Guest account", Toast.LENGTH_SHORT).show();



                                    startActivity(intent);

                                }else{
                                    Toasty.success(context,"Guest account is not enabled for this vehicle", Toast.LENGTH_SHORT).show();
                                }
                            }
                            catch (JSONException e){
                                //Toasty.success(context,e.getMessage(),Toast.LENGTH_SHORT).show();

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
                    params.put("reg",txtGuestReg.getText().toString().trim().toUpperCase());
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);




        }
    }



}