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

public class ResetPassword extends AppCompatActivity {
    Context context;
    TextView password, cpass;
    SharedPreferences prf;
    String pass;
    String URL = "https://facecarserver.000webhostapp.com/facecar/updatePass.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prf = getSharedPreferences("LogDetails", MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        password = findViewById(R.id.txtResetPassword);
        cpass = findViewById(R.id.txtResetConfirmPassword);


    }

    public void back(View view) {
        Intent intent = new Intent(this, ProfileList.class);
        startActivity(intent);
    }

    public void resetPassword(View view) {
        context = this;
        String id = prf.getString("ID", "");
        pass = password.getText().toString().trim();
        if (!pass.isEmpty() && !cpass.getText().toString().trim().isEmpty() && pass.equals(cpass.getText().toString().trim())) {
            Toasty.success(context, "Password Reset successfully.", Toast.LENGTH_SHORT).show();

            changePassword(context, id, pass, URL, "Password has been changed");


            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        } else {
            if (pass.isEmpty()) {
                password.setError("Enter password");
            }
            if (cpass.getText().toString().trim().isEmpty()) {
                cpass.setError("Enter password");
            }
            if (!pass.equals(cpass.getText().toString().trim())) {
                password.setError("Not matching");
                cpass.setError("Not matching");
            }

        }
    }


    public void changePassword(final Context context, final String id, final String password, String URL, final String prompt) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if (success.equals("1")) {
                                Toasty.success(context, prompt, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            //Toasty.success(context,e.getMessage()).show();

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toasty.success(context,error.getMessage()).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                params.put("password", password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

}