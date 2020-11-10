package com.example.facecar20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

public class CarModuleSettings extends AppCompatActivity {

    private EditText txtID;
    private Button btnAdd,btnDel,btnBack;
    private LinearLayout layout;
    private ProgressBar loading;
    String URLCheck="https://facecarserver.000webhostapp.com/facecar/checkRPI.php";
    String URLAdd="https://facecarserver.000webhostapp.com/facecar/addModule.php";
    String URLDel="https://facecarserver.000webhostapp.com/facecar/delModule.php";
    SharedPreferences prf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_module_settings);

        prf=getSharedPreferences("LogDetails",MODE_PRIVATE);
        final String reg = prf.getString("REG","");
        final String userid = prf.getString("ID","");

        layout=findViewById(R.id.layoutCarModuleSettingsAdd);
        loading=findViewById(R.id.progressBarCarModuleSettings);

        btnAdd=findViewById(R.id.carModuleSettingsAdd);
        btnDel=findViewById(R.id.carModuleSettingsDel);
        btnBack=findViewById(R.id.carModuleSettingsBack);
        txtID=findViewById(R.id.txtcarModuleID);
        checkModule(URLCheck,reg);



        //button Delete listener
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading.setVisibility(View.VISIBLE);
                DeleteRPI(URLDel,reg);
            }
        });

        //button add listener
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading.setVisibility(View.VISIBLE);
                String RPIid = txtID.getText().toString().trim();
                AddRPI(URLAdd,reg,userid,RPIid);
            }
        });

        //button back listener
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(), Settings.class);
                startActivity(intent);
            }
        });






    }

    //DeleteRPI()
    //deletes the relevant RPI record from the database
    public void DeleteRPI(String URL, final String reg){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if(success.equals("1")) {
                                Toasty.success(getApplicationContext(),"DELETED SUCCESSFULLY").show();
                                Intent intent= new Intent(getApplicationContext(), Settings.class);
                                startActivity(intent);
                            }
                        }
                        catch (JSONException e){
                            loading.setVisibility(View.INVISIBLE);
                            Toasty.error(getApplicationContext(),e.getMessage()).show();

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.setVisibility(View.INVISIBLE);
                Toasty.success(getApplicationContext(),error.getMessage()).show();


            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("reg",reg);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    //AddRPI()
    //Add RPI record to the database
    public void AddRPI(String URL, final String reg,final String ID, final String RPI){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if(success.equals("1")) {
                                Toasty.success(getApplicationContext(),"ADDED SUCCESSFULLY").show();
                                Intent intent= new Intent(getApplicationContext(), Settings.class);
                                startActivity(intent);
                            }
                        }
                        catch (JSONException e){
                            loading.setVisibility(View.INVISIBLE);
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
                params.put("reg",reg);
                params.put("userid",ID);
                params.put("RID",RPI);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    //CheckModule()
    //checks whether RPI record available or not for the relevant car.
    public void checkModule(String url, final String rego)
    {

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            //JSONArray jsonArray=jsonObject.getJSONArray("RPID");

                            Log.d("JSONR", success);

                            if(success.equals("1"))
                            {
                                btnDel.setVisibility(View.VISIBLE);
                                loading.setVisibility(View.INVISIBLE);
                            }else
                            {
                                layout.setVisibility(View.VISIBLE);
                                loading.setVisibility(View.INVISIBLE);
                            }
                        }
                        catch (JSONException e) {
                            Toast.makeText(getApplicationContext(),"error:"+e,Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"error:"+error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("rego",rego);
                return params;
            }
        };

        RequestQueue queue= Volley.newRequestQueue(this);
        queue.add(stringRequest);


    }

    @Override
    public void onBackPressed() {
        Intent intent= new Intent(getApplicationContext(), Settings.class);
        startActivity(intent);
    }
}