package com.example.facecar20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.facecar20.Camera.CameraScanFace;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class OwnerDashboard extends AppCompatActivity {
    Context context;
    TextView welcome;
    Button btnUnlock,btnDelete;
    FloatingActionButton btnChangePIN;
    InternetConnectivity ic;

    Switch guest;
    private static String URL_GUEST="https://facecarserver.000webhostapp.com/facecar/guestSetup.php";


    String reg ,guestSt,welcomStr,username,userType,pin;

    SharedPreferences prf ;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_dashboard);

        prf = getSharedPreferences("LogDetails",MODE_PRIVATE);
        final SharedPreferences.Editor editor = prf.edit();
        ic=new InternetConnectivity(getApplicationContext());



        reg = prf.getString("REG","");
        guestSt = prf.getString("GUEST","");
        welcomStr = prf.getString("NAME","");
        username = prf.getString("USERNAME","");
        userType=prf.getString("LOGEDACC","");





        btnUnlock=findViewById(R.id.btnUnlockOwner);
        btnDelete=findViewById(R.id.btnRemoveFaceOwner);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!ic.checkInternet()){
                    Toasty.error(getApplicationContext(),"No internet connection",Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(getApplicationContext(), EnterPin.class);
                    intent.putExtra("PINTYPE","DEL");
                    intent.putExtra("USERTYPE","O");
                    startActivity(intent);
                }

            }
        });

        btnChangePIN=findViewById(R.id.btnOwnerChangePin);
        btnChangePIN.setOnClickListener(btnSettingsListner);

        welcome=findViewById(R.id.txtOwnerWelcome);
        welcome.setText(welcomStr);

        String cc=prf.getString("PIN"+username,"");
        if(cc.equals("")){
            Intent intent = new Intent(getApplicationContext(), NewPin.class);
            startActivity(intent);
        }

        guest=findViewById(R.id.switchGuest);

        if(guestSt.equals("0")){
            guest.setChecked(false);
        }else{
            guest.setChecked(true);
        }

        btnUnlock.setOnClickListener(btnUnlockListner);

        guest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b==true){
                    guestSetup(reg,"1","Guest account enabled");
                    editor.putString("GUEST","1");

                }else{
                    guestSetup(reg,"0","Guest account disabled");
                    editor.putString("GUEST","0");
                } editor.commit();
            }
        });
    }

    private View.OnClickListener btnUnlockListner = new View.OnClickListener() {
        public void onClick(View view) {
            if(!ic.checkInternet()){
                Toasty.error(getApplicationContext(),"No internet connection",Toast.LENGTH_SHORT).show();
            }else{
                btnUnlock.setEnabled(false);
                Intent intent = new Intent(getApplicationContext(), CameraScanFace.class);
                startActivity(intent);
            }
        }
    };

    private View.OnClickListener btnSettingsListner = new View.OnClickListener() {
        public void onClick(View view) {
            if(!ic.checkInternet()){
                Toasty.error(getApplicationContext(),"No internet connection",Toast.LENGTH_SHORT).show();
            }else{
                Intent intent = new Intent(getApplicationContext(), AuthPassword.class);
                startActivity(intent);
            }
        }
    };

    public void guestSetup(final String reg, final String status, final String prompt){
        context=this;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_GUEST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if(success.equals("1")) {
                                Toasty.success(context, prompt,Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e){
                            //Toasty.success(context,e.getMessage()).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // Toasty.success(context,error.getMessage()).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("regNo",reg);
                params.put("status",status);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }



    public void addface(View view) {
        if(!ic.checkInternet()){
            Toasty.error(getApplicationContext(),"No internet connection",Toast.LENGTH_SHORT).show();
        }else{
            Intent intent = new Intent(this, EnterPin.class);
            intent.putExtra("PINTYPE","ADD");
            intent.putExtra("USERTYPE","O");
            startActivity(intent);
        }
    }

    public void openPendingReqs(View view) {
        if(!ic.checkInternet()){
            Toasty.error(getApplicationContext(),"No internet connection",Toast.LENGTH_SHORT).show();
        }else{
            Intent intent = new Intent(this, PendingRequests.class);
            intent.putExtra("REG",reg);
            startActivity(intent);
        }
    }

    public void openProfileList(View view) {
        if(!ic.checkInternet()){
            Toasty.error(getApplicationContext(),"No internet connection",Toast.LENGTH_SHORT).show();
        }else{
            Intent intent = new Intent(this, ProfileList.class);
            intent.putExtra("REG",reg);
            startActivity(intent);
        }
    }
    public void signOutFromOwner(View view) {
        context=this;
        SharedPreferences.Editor editor = prf.edit();


        editor.putString("LOGSTATUS","LOGGEDOUT");
        editor.putString("LOGEDACC","N");
        editor.commit();


        Toasty.info(context,"SignOut Successfully",Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
    }


    public void back(View view) {
        Intent intent = new Intent(this, OwnerDashboard.class);
        startActivity(intent);
    }








}

