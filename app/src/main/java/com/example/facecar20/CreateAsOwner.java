package com.example.facecar20;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class CreateAsOwner extends AppCompatActivity {
    final static String URL = "https://facecarserver.000webhostapp.com/facecar/register_owner.php";
    final static String URL_CHECK_USERNAME="https://facecarserver.000webhostapp.com/facecar/checkUN.php";
    private EditText txtbUserName,txtbPassword,txtbRegNo,txtbPhoneNumber,txtbEmail,txtbFullName,txtbCPass;
    private Button btnReg,btnBack;
    private ProgressBar loading;
    InternetConnectivity ic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_as_owner);
        txtbFullName=(EditText)findViewById(R.id.txtFullNameCreOwner);
        txtbUserName=(EditText)findViewById(R.id.txtUsernameCreOwner);
        txtbEmail=(EditText)findViewById(R.id.txtEmailCreOwner);
        txtbPassword=(EditText)findViewById(R.id.txtPasswordCreOwner);
        txtbRegNo=(EditText)findViewById(R.id.txtVehicleNoCreOwner);
        txtbPhoneNumber=(EditText)findViewById(R.id.txtPhoneNoCreOwner);
        txtbCPass=(EditText)findViewById(R.id.txtConfirmPwCreOwner);
        ic=new InternetConnectivity(getApplicationContext());
        loading=findViewById(R.id.loadingCreatingOwner);
        btnBack=findViewById(R.id.btnBackOwnerCreate);

        btnReg = findViewById(R.id.btnOwnerCreate);

        btnReg.setOnClickListener(btnRegListener);

    }

    //button Register click listener
    private View.OnClickListener btnRegListener = new View.OnClickListener() {
        public void onClick(View view) {
            if(!ic.checkInternet()){

            }else{
                String FullName=txtbFullName.getText().toString().trim();
                String UserName=txtbUserName.getText().toString().trim().toLowerCase();
                String Email=txtbEmail.getText().toString().trim();
                String PhoneNumber=txtbPhoneNumber.getText().toString().trim();
                String RegNo=txtbRegNo.getText().toString().trim();
                String pw = txtbPassword.getText().toString().trim();
                String cpw = txtbCPass.getText().toString().trim();
                //Boolean boolUN =


                if (!FullName.isEmpty() && !UserName.isEmpty() && !cpw.isEmpty() && !pw.isEmpty() && !Email.isEmpty() && !PhoneNumber.isEmpty() && !RegNo.isEmpty() )
                {
                    if ( pw.equals(cpw) && Patterns.EMAIL_ADDRESS.matcher(Email).matches() && PhoneNumber.length()==10 && pw.length()>=6){
                        loading.setVisibility(View.VISIBLE);
                        btnReg.setEnabled(false);
                        btnBack.setEnabled(false);
                        checkUsername(URL_CHECK_USERNAME,UserName);

                    }
                    else {
                        if(!pw.equals(cpw)){
                            txtbCPass.setError("Passwords not matching");
                            txtbPassword.setError("Passwords not matching");
                        }


                        //Toasty.error(getApplicationContext(),"password and confirmation password doesn't match.",Toast.LENGTH_SHORT).show();

                        if(!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
                            txtbEmail.setError("Enter a valid email address");
                        }
                        if(PhoneNumber.length()<10){
                            txtbPhoneNumber.setError("Invalid Phone Number");
                        }
                        if(pw.length()<6){
                            txtbPassword.setError("Password must be at least 6 characters");
                        }

                    }
                }
                else
                {
                    if(FullName.isEmpty()){
                        txtbFullName.setError("Please enter Name");
                    }
                    if(UserName.isEmpty()){
                        txtbUserName.setError("Please enter an User Name");
                    }
                    if(pw.isEmpty()){
                        txtbPassword.setError("Please enter password");
                    }
                    if(cpw.isEmpty()){
                        txtbCPass.setError("Please enter confirmation password");
                    }
                    if(RegNo.isEmpty()){
                        txtbRegNo.setError("Please enter a registration number");
                        }
                    if(Email.isEmpty()){
                        txtbEmail.setError("Please enter an email");
                    }
                    if(PhoneNumber.isEmpty()){
                        txtbPhoneNumber.setError("Please enter a phone number");
                    }

                }
            }

        }
    };

    //Inserts the new owner into the database
    public void registerOwner()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            Log.d("JSONR", jsonObject.toString());

                            String success = jsonObject.getString("success");

                            if(success.equals("1"))
                            {

                                Toast.makeText(getApplicationContext(),"User Registered successfully",Toast.LENGTH_SHORT).show();
                                txtbFullName.setText("");
                                txtbUserName.setText("");
                                txtbPassword.setText("");
                                txtbCPass.setText("");
                                txtbPhoneNumber.setText("");
                                txtbRegNo.setText("");
                                txtbEmail.setText("");
                                Intent intent = new Intent(getApplicationContext(), Login.class);
                                startActivity(intent);

                            }
                            else if (success.equals("3"))
                            {
                                loading.setVisibility(View.INVISIBLE);
                                btnReg.setEnabled(true);
                                btnBack.setEnabled(true);
                                txtbRegNo.setError("This vehicle has already been registered");
                            }else
                            {
                                loading.setVisibility(View.INVISIBLE);
                                btnReg.setEnabled(true);
                                btnBack.setEnabled(true);
                                Toast.makeText(getApplicationContext(),"User could not register.",Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loading.setVisibility(View.INVISIBLE);
                    btnReg.setEnabled(true);
                    btnBack.setEnabled(true);
                    Toast.makeText(getApplicationContext(),"error:"+error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("username",txtbUserName.getText().toString());
                params.put("fullname",txtbFullName.getText().toString());
                params.put("RegistrationNumber",txtbRegNo.getText().toString());
                params.put("password",txtbPassword.getText().toString());
                params.put("phonenumber",txtbPhoneNumber.getText().toString());
                params.put("email",txtbEmail.getText().toString());
                return params;
            }
        };

        RequestQueue queue= Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    //checkUsername()
    //checks whether the entered username exists or not in the database
    public void checkUsername(String url, final String username)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            Log.d("JSONR", jsonObject.toString());

                            String success = jsonObject.getString("success");

                            if(success.equals("1"))
                            {
                                loading.setVisibility(View.INVISIBLE);
                                btnReg.setEnabled(true);
                                btnBack.setEnabled(true);
                                txtbUserName.setError("Username already exists");

                            }else
                            {
                                loading.setVisibility(View.INVISIBLE);
                                btnReg.setEnabled(true);
                                btnBack.setEnabled(true);
                                registerOwner();
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
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
                params.put("username",username);
                return params;
            }
        };

        RequestQueue queue= Volley.newRequestQueue(this);
        queue.add(stringRequest);

    }


    public void back(View view){
        Intent intent = new Intent(this, CreateProfile.class);
        startActivity(intent);
    }
}