package com.example.facecar20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
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

public class Login extends AppCompatActivity {

    private Button back;
    private ProgressBar loading;
    private Button btnlogin,btnCancel;
    private EditText email;
    private EditText password;
    private TextView status;
    private static String URL_LOGIN="https://facecarserver.000webhostapp.com/facecar/login.php";
    String URL_DELETE="https://facecarserver.000webhostapp.com/facecar/delete_profile.php";
    Context context;
    InternetConnectivity ic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context=this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ic=new InternetConnectivity(context);

        btnlogin=findViewById(R.id.btnLoginSubmit);
        btnCancel=findViewById(R.id.btnBack);
        email=findViewById(R.id.txtEmailLogin);
        password=findViewById(R.id.txtPassword);
        loading = findViewById(R.id.loginLoading);

        btnlogin.setOnClickListener(btnLoginListener);

        TextView fpText = findViewById(R.id.txtReqPWreset);
        String text = "Forgot your password?";
        int len = text.length()-1;

        SpannableString ss = new SpannableString(text);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent intent = new Intent(getApplicationContext(),ForgotPassword.class);
                startActivity(intent);
            }
        };
        ss.setSpan(clickableSpan,0,len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        fpText.setText(ss);
        fpText.setMovementMethod(LinkMovementMethod.getInstance());



    }

    private View.OnClickListener btnLoginListener = new View.OnClickListener() {
        public void onClick(View view) {

            String enEmail = email.getText().toString().trim();
            String enPass = password.getText().toString().trim();

            if(ic.checkInternet()){
                if(!enEmail.isEmpty()&&!enPass.isEmpty()) {
                    if(Patterns.EMAIL_ADDRESS.matcher(enEmail).matches()){
                        btnlogin.setEnabled(false);
                        btnCancel.setEnabled(false);
                        loading.setVisibility(View.VISIBLE);
                        login(enEmail,enPass);
                    }else{
                        email.setError("Enter a valid email");
                        email.requestFocus();
                    }

                }
                else{
                    email.setError("Enter an Email");
                    password.setError("Enter a password");
                }
            }else if(!ic.checkInternet()){
                Toasty.error(getApplicationContext(),"No internet Connection",Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void login(final String Email, final String Password) {

        SharedPreferences prf = getSharedPreferences("LogDetails",MODE_PRIVATE);
        final SharedPreferences.Editor editor = prf.edit();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray=jsonObject.getJSONArray("login");

                            if(success.equals("1")){
                                for(int i=0; i<jsonArray.length();i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    final String username = object.getString("username").trim();
                                    final String id = object.getString("id").trim();
                                    String name = object.getString("name").trim();
                                    final String reg = object.getString("reg").trim();
                                    String accType = object.getString("type").trim();
                                    String status = object.getString("status").trim();
                                    String guest = object.getString("guest").trim();




                                    if (accType.equals("O")){

                                        editor.putString("ID",id);
                                        editor.putString("USERNAME",username);
                                        editor.putString("NAME",name);
                                        editor.putString("REG",reg);
                                        editor.putString("GUEST",guest);
                                        editor.putString("LOGEDACC","O");
                                        editor.putString("LOGSTATUS","LOGGED");
                                        editor.commit();

                                        if(status.equals("R")){
                                            Intent intent = new Intent(getApplicationContext(), ResetPassword.class);
                                            startActivity(intent);
                                        }else{
                                            Intent intent = new Intent(getApplicationContext(), OwnerDashboard.class);
                                            startActivity(intent);
                                        }

                                    }
                                    else{
                                        editor.putString("ID",id);
                                        editor.putString("USERNAME",username);
                                        editor.putString("NAME",name);
                                        editor.putString("REG",reg);
                                        editor.putString("GUEST",guest);

                                        editor.commit();

                                        if(status.equals("A")){
                                            Intent intent = new Intent(getApplicationContext(), MemberDashboard.class);
                                            editor.putString("LOGEDACC","M");
                                            editor.putString("LOGSTATUS","LOGGED");
                                            editor.commit();

                                            startActivity(intent);

                                        }else if(status.equals("P")){

                                            btnlogin.setEnabled(true);
                                            btnCancel.setEnabled(true);
                                            loading.setVisibility(View.INVISIBLE);

                                            AlertDialog.Builder builder = new AlertDialog.Builder(context);

                                            builder.setTitle("Account has not been approved");
                                            builder.setMessage("Owner has not approve this account yet.\nWait for approval to login");

                                            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {



                                                }
                                            });

                                            builder.show();

                                        }else if(status.equals("D")){
                                            AlertDialog.Builder builder = new AlertDialog.Builder(context);

                                            builder.setTitle("Account Creation Request rejected");
                                            builder.setMessage("Owner has declined your account creation request");

                                            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                    deleteAcc(id,"M",reg,"Account removed",URL_DELETE);
                                                    email.setText("");
                                                    password.setText("");


                                                }
                                            });

                                            builder.show();
                                        }
                                        else if(status.equals("R")){
                                            Intent intent = new Intent(getApplicationContext(), ResetPassword.class);
                                            startActivity(intent);

                                        }


                                    }
                                    //Toasty.success(getApplicationContext(),"Successfull", Toast.LENGTH_SHORT).show();

                                }
                            }else{
                                btnlogin.setEnabled(true);
                                btnCancel.setEnabled(true);
                                loading.setVisibility(View.INVISIBLE);
                                Toasty.error(getApplicationContext(),"Invalid email or password", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            btnlogin.setEnabled(true);
                            btnCancel.setEnabled(true);
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
                params.put("email",Email);
                params.put("password",Password);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    public void backLogin(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

    public void forgotPW(View view) {
    }

    public void deleteAcc(final String id,final String acctype,final String reg, final String prompt, String URL_PAR){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_PAR,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if(success.equals("1")) {
                                Toasty.success(context, prompt,Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toasty.success(context, "error",Toast.LENGTH_SHORT).show();
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
                params.put("acctype",acctype);
                params.put("reg",reg);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
