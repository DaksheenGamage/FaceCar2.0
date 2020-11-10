package com.example.facecar20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class MemberDashboard extends AppCompatActivity {
    Context context;
    TextView welcome;
    SharedPreferences prf ;
    String URL="https://facecarserver.000webhostapp.com/facecar/sentRequest.php";
    String username,userType;
    Button btnUnlock,btnDelete;
    FloatingActionButton btnChangePIN;
    InternetConnectivity ic;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_dashboard);

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MemberDashboard.this,
                    new String[]{
                            Manifest.permission.CAMERA},
                    100);
        }
        context=this;

        prf = getSharedPreferences("LogDetails",MODE_PRIVATE);
        username = prf.getString("USERNAME","");
        userType=prf.getString("LOGEDACC","");
        ic=new InternetConnectivity(getApplicationContext());
        Intent intent = getIntent();
        welcome=findViewById(R.id.txtMemberWelcome);
        welcome.setText(prf.getString("NAME",""));

        String cc=prf.getString("PIN"+username,"");
        if(cc.equals("")){
            Intent i = new Intent(getApplicationContext(), NewPin.class);
            startActivity(i);
        }

        btnUnlock=findViewById(R.id.btnUnlockMember);
        btnDelete=findViewById(R.id.btnRemoveFaceMember);
        btnChangePIN=findViewById(R.id.btnMemberChangePin);
        btnChangePIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AuthPassword.class);
                startActivity(intent);
            }
        });


        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(!ic.checkInternet()){
                   Toasty.error(getApplicationContext(),"No internet connection",Toast.LENGTH_SHORT).show();
               }else{
                   Intent intent = new Intent(getApplicationContext(), EnterPin.class);
                   intent.putExtra("PINTYPE","DEL");
                   intent.putExtra("USERTYPE","M");
                   startActivity(intent);
               }
            }
        });


        btnUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!ic.checkInternet()){
                    Toasty.error(getApplicationContext(),"No internet connection",Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(getApplicationContext(), CameraScanFace.class);
                    startActivity(intent);
                }
            }
        });

    }

    public void addface(View view) {
        if(!ic.checkInternet()){
            Toasty.error(getApplicationContext(),"No internet connection",Toast.LENGTH_SHORT).show();
        }else{
            Intent intent = new Intent(this, EnterPin.class);
            intent.putExtra("PINTYPE","ADD");
            intent.putExtra("USERTYPE","M");
            startActivity(intent);
        }


    }

    public void chagePassword(View view) {
        if(!ic.checkInternet()){
            Toasty.error(getApplicationContext(),"No internet connection",Toast.LENGTH_SHORT).show();
        }else{
            Intent intent = new Intent(this, MemberChangePassword.class);
            intent.putExtra("ID",prf.getString("ID",""));
            startActivity(intent);
        }
    }

    public void DeleteReq(View view) {
        if(!ic.checkInternet()){
            Toasty.error(getApplicationContext(),"No internet connection",Toast.LENGTH_SHORT).show();
        }else{
            context = this;
            sendReq(URL,prf.getString("ID",""),"DEL",prf.getString("REG",""),context);
        }
    }

    public void signOutFromMember(View view) {
        context=this;
        SharedPreferences.Editor editor = prf.edit();

        editor.putString("LOGSTATUS","LOGGEDOUT");
        editor.putString("LOGEDACC","N");
        editor.commit();


        Intent intent = new Intent(this, MainActivity.class);
        Toasty.info(context,"SignOut Successfully",Toast.LENGTH_SHORT).show();
        startActivity(intent);

    }
    @Override
    public void onBackPressed() {

    }

    public void sendReq(String URL, final String userID, final String RequestType, final String Vehicle, final Context context){
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
                                    Toasty.success(context,"Account Deletion request has been sent successfully", Toast.LENGTH_SHORT).show();
                                    SharedPreferences.Editor editor= prf.edit();

                                    editor.putString("LOGSTATUS","LOGGEDOUT");
                                    editor.putString("LOGEDACC","N");
                                    editor.putString("PIN"+username,"");
                                    editor.commit();


                                    FirebaseStorage storage = FirebaseStorage.getInstance();
                                    final StorageReference storageRef = storage.getReference().child(username).child("Face.jpg");

                                    storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                        }
                                    });

                                    Intent intent = new Intent(context, MainActivity.class);
                                    Toasty.info(context,"You have been logged out", Toast.LENGTH_SHORT).show();
                                    startActivity(intent);

                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(),"Account Deletion request has not been sent",Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getApplicationContext(),"error: "+error.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    params.put("userid",userID);
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