package com.example.facecar20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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

public class MemberChangePassword extends AppCompatActivity {
    Context context;
    TextView oldpass,newpass,cnewpass;

    String id;
    String URL="https://facecarserver.000webhostapp.com/facecar/member_change_pw.php";
    SharedPreferences prf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent=getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_change_password);
        id=intent.getStringExtra("ID");

        oldpass=findViewById(R.id.txtOldPasswordMemChangePW);
        newpass=findViewById(R.id.txtNewPasswordMemChangePW);
        cnewpass=findViewById(R.id.txtConfirmPasswordMemChangePW);
        prf = getSharedPreferences("LogDetails",MODE_PRIVATE);
    }

    public void passwordChange(View view) {
        context = this;
        String stroldpass=oldpass.getText().toString().trim();
        String strnewpass=newpass.getText().toString().trim();
        String strcnewpass=cnewpass.getText().toString().trim();
        if(!strcnewpass.isEmpty() && !stroldpass.isEmpty() && !strnewpass.isEmpty() && strcnewpass.equals(strnewpass)){
            passwordFunc("Password Changed Successfully",id,stroldpass,strnewpass,oldpass);
        }
        else{
            if(stroldpass.isEmpty()){
                oldpass.setError("Enter the old password");
            }
            if(strnewpass.isEmpty()){
                newpass.setError("Enter new password");
            }
            if(strcnewpass.isEmpty()){
                cnewpass.setError("Enter confirm password");
            }
            if(!strcnewpass.equals(strnewpass)){
                newpass.setError("Password not match");
                cnewpass.setError("Password not match");
            }
        }




    }

    public void back(View view) {
        Intent intent = new Intent(this, MemberDashboard.class);
        startActivity(intent);

    }
    public void passwordFunc(final String prompt, final String id, final String oldpw, final String newpw, final TextView tv){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if(success.equals("1")) {
                                Toasty.success(context, prompt, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MemberDashboard.class);
                                startActivity(intent);
                            }else if(success.equals("3")){
                                tv.setError("Password is wrong");
                            }

                        }
                        catch (JSONException e){
                            Toasty.success(context,e.getMessage()).show();

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.success(context,error.getMessage()).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("id",id);
                params.put("oldpw",oldpw);
                params.put("newpw",newpw);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


}