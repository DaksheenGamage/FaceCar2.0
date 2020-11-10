package com.example.facecar20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;

public class ChangeDetails extends AppCompatActivity {
    private Button btnChange,btnBack;
    private EditText txtName,txtPhone,txtEmail;
    private ProgressBar loading;
    SharedPreferences prf;
    String name,id,userType;
    String URL_SET="https://facecarserver.000webhostapp.com/facecar/set_change_details.php";
    String URL_GET="https://facecarserver.000webhostapp.com/facecar/get_change_details.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_details);

        prf=getSharedPreferences("LogDetails",MODE_PRIVATE);
        name = prf.getString("NAME","");
        id=prf.getString("ID","");
        userType=prf.getString("LOGEDACC","");



        txtEmail=findViewById(R.id.txtEmailChangeDetails);
        txtName=findViewById(R.id.txtFullNameChangeDetails);
        txtPhone=findViewById(R.id.txtPhoneNoChangeDetails);
        TextView tvTitle = findViewById(R.id.txtChangeDetails);
        tvTitle.setText("Change Details of\n"+name);
        btnBack=findViewById(R.id.btnBackChangeDetails);
        btnChange=findViewById(R.id.btnChangeDetails);
        loading=findViewById(R.id.loadingCreatingOwner);

        btnChange.setEnabled(false);
        btnBack.setEnabled(false);
        loading.setVisibility(View.VISIBLE);

        getDetails(URL_GET,id);

        btnChange.setOnClickListener(btnChangeListner);

        //Button Back Listener
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Settings.class);
                startActivity(intent);
            }
        });
    }

    //button Change Listener
    private View.OnClickListener btnChangeListner = new View.OnClickListener() {
        public void onClick(View view) {

            if(txtEmail.getText().toString().trim().length()>0 && txtName.getText().toString().trim().length()>0 && txtPhone.getText().toString().trim().length()==10){
                if(Patterns.EMAIL_ADDRESS.matcher(txtEmail.getText().toString().trim()).matches()){
                    btnChange.setEnabled(false);
                    btnBack.setEnabled(false);
                    loading.setVisibility(View.VISIBLE);

                    setDetails(URL_SET,id,txtName.getText().toString().trim(),txtEmail.getText().toString().trim(),txtPhone.getText().toString().trim());
                }else{
                    txtEmail.setError("Enter a valid email address");
                }
            }else{
                if(txtEmail.getText().toString().trim().length()==0){
                    txtEmail.setError("Please Enter an email");
                }
                if(txtName.getText().toString().trim().length()==0){
                    txtName.setError("Please Enter a name");
                }
                if(txtPhone.getText().toString().trim().length()==0){
                    txtPhone.setError("Please Enter a phone number");
                }
                if(txtPhone.getText().toString().trim().length()<10){
                    txtPhone.setError("Invalid Phone number");
                }

            }

        }
    };

    //getDetails()
    //Retrieves data relevant to given user id from the database and shows in editText boxes
    public void getDetails(String URL, final String uid){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray=jsonObject.getJSONArray("details");

                            if(success.equals("1")){
                                for(int i=0; i<jsonArray.length();i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String name = object.getString("name").trim();
                                    final String email = object.getString("email").trim();
                                    String number = object.getString("phone").trim();
                                    txtEmail.setText(email);
                                    txtName.setText(name);
                                    txtPhone.setText(number);
                                    btnChange.setEnabled(true);
                                    btnBack.setEnabled(true);
                                    loading.setVisibility(View.INVISIBLE);
                                }
                            }else{
                                btnChange.setEnabled(true);
                                btnBack.setEnabled(true);
                                loading.setVisibility(View.INVISIBLE);
                                Toasty.error(getApplicationContext(),"Invalid email or password", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            btnChange.setEnabled(true);
                            btnBack.setEnabled(true);
                            loading.setVisibility(View.INVISIBLE);
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
                params.put("id",uid);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }

    //setDetails()
    //updates the entered details to the database
    public void setDetails(String URL, final String uid, final String name, final String email, final String phone){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if(success.equals("1")){
                                btnChange.setEnabled(true);
                                btnBack.setEnabled(true);
                                loading.setVisibility(View.INVISIBLE);
                                Toasty.success(getApplicationContext(),"Changed",Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(getApplicationContext(), Settings.class);
                                startActivity(intent);

                            }else{
                                btnChange.setEnabled(true);
                                btnBack.setEnabled(true);
                                loading.setVisibility(View.INVISIBLE);
                                Toasty.error(getApplicationContext(),"Invalid email or password", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            btnChange.setEnabled(true);
                            btnBack.setEnabled(true);
                            loading.setVisibility(View.INVISIBLE);
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
                params.put("id",uid);
                params.put("name",name);
                params.put("phone",phone);
                params.put("email",email);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }

}