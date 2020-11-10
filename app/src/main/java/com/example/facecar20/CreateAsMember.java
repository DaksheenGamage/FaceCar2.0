package com.example.facecar20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;

public class CreateAsMember extends AppCompatActivity {
    final static String URL = "https://facecarserver.000webhostapp.com/facecar/register_member.php";
    final static String URL_REQ = "https://facecarserver.000webhostapp.com/facecar/get_user_id.php";
    final static String URL_CHECK_USERNAME="https://facecarserver.000webhostapp.com/facecar/checkUN.php";

    InternetConnectivity ic;

    private EditText txtbUserName,txtbPassword,txtbRegNo,txtbPhoneNumber,txtbEmail,txtbFullName,txtbCPass;
    private Button btnReg,btnBack;
    private ProgressBar loading;
    String email,regNo,id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_as_member);

        txtbFullName=(EditText)findViewById(R.id.txtFullNameCreMember);
        txtbUserName=(EditText)findViewById(R.id.txtUsernameCreMember);
        txtbEmail=(EditText)findViewById(R.id.txtEmailCreMember);
        txtbPassword=(EditText)findViewById(R.id.txtPasswordCreMember);
        txtbRegNo=(EditText)findViewById(R.id.txtVehicleNoCreMember);
        txtbPhoneNumber=(EditText)findViewById(R.id.txtPhoneNoCreMember);
        txtbCPass=(EditText)findViewById(R.id.txtConfirmPwCreMember);
        ic=new InternetConnectivity(getApplicationContext());
        loading = findViewById(R.id.loadingCreatingMember);
        btnBack=findViewById(R.id.btnBackOwnerMember);

        btnReg = findViewById(R.id.btnCreateMember);

        btnReg.setOnClickListener(btnRegListner);
    }

    //button Register click listener
    private View.OnClickListener btnRegListner = new View.OnClickListener() {
        public void onClick(View view) {

            if(!ic.checkInternet()){
                Toasty.error(getApplicationContext(),"No internet connection",Toast.LENGTH_SHORT).show();

            }else{
                String FullName=txtbFullName.getText().toString().trim();
                String UserName=txtbUserName.getText().toString().trim();
                String pw = txtbPassword.getText().toString().trim();
                String cpw = txtbCPass.getText().toString().trim();
                String PhoneNumber=txtbPhoneNumber.getText().toString().trim();
                String Email=txtbEmail.getText().toString().trim();
                String RegNo=txtbRegNo.getText().toString().trim();
                regNo = RegNo;
                email=Email;


                if (!FullName.isEmpty() && !UserName.isEmpty() && !cpw.isEmpty() && !pw.isEmpty() && !Email.isEmpty() && !PhoneNumber.isEmpty() && !RegNo.isEmpty())
                {
                    if (pw.length()>=6 && pw.equals(cpw) && Patterns.EMAIL_ADDRESS.matcher(Email).matches() && PhoneNumber.length()==10){
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
                            txtbEmail.setError("Enter a valid email");
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

    //registerMember()
    //inserts the new member into the database
    public void registerMember()
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

                                sendReq(URL_REQ,email,"ACC",regNo,getApplicationContext());

                                //Toast.makeText(getApplicationContext(),"Request has been sent successfully",Toast.LENGTH_SHORT).show();

                                txtbFullName.setText("");
                                txtbUserName.setText("");
                                txtbPassword.setText("");
                                txtbCPass.setText("");
                                txtbPhoneNumber.setText("");
                                txtbRegNo.setText("");
                                txtbEmail.setText("");




                                //Intent intent = new Intent(getApplicationContext(), Login.class);
                                //startActivity(intent);

                            }else if (success.equals("3")) {
                                loading.setVisibility(View.INVISIBLE);
                                btnReg.setEnabled(true);
                                btnBack.setEnabled(true);
                                txtbRegNo.setError("This vehicle has not been registered in the system");

                            }
                            else
                            {
                                loading.setVisibility(View.INVISIBLE);
                                btnReg.setEnabled(true);
                                btnBack.setEnabled(true);
                                Toast.makeText(getApplicationContext(),"User could not register. Account already exist.",Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e) {
                            loading.setVisibility(View.INVISIBLE);
                            btnReg.setEnabled(true);
                            btnBack.setEnabled(true);
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();


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

    public void back(View view){
        Intent intent = new Intent(this, CreateProfile.class);
        startActivity(intent);
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
                                txtbUserName.setError("Username exists");


                            }else
                            {
                                registerMember();
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                            Toasty.error(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
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

    //sendReq()
    //sends the account creation request to the owner of the vehicle
    public void sendReq(String URL, final String email, final String RequestType, final String Vehicle, final Context context){
        {
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try{
                                Log.e ( "response", "" + response );

                                JSONObject json = new JSONObject(response);
                                Log.d("JSONR1", json.toString());

                                String success = json.getString("success");


                                if(success.equals("1"))
                                {
                                    Toasty.success(context,"Account request has been sent successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(context, Login.class);
                                    startActivity(intent);
                                }
                                else
                                {
                                    loading.setVisibility(View.INVISIBLE);
                                    btnReg.setEnabled(true);
                                    btnBack.setEnabled(true);

                                    Toast.makeText(getApplicationContext(),"Account request has not been sent",Toast.LENGTH_SHORT).show();
                                }
                            }
                            catch (JSONException e) {
                                loading.setVisibility(View.INVISIBLE);
                                btnReg.setEnabled(true);
                                btnBack.setEnabled(true);
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();

                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Toast.makeText(getApplicationContext(),"error: "+error.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    params.put("email",email);
                    params.put("requestType",RequestType);
                    params.put("Regno",Vehicle);
                    return params;
                }
            };

            RequestQueue queue= Volley.newRequestQueue(this);
            queue.add(stringRequest);
        }
    }
}