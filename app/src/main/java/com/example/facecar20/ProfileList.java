package com.example.facecar20;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class ProfileList extends AppCompatActivity {

    Context context;
    ListView listView;
    ProgressBar loading;
    String URL="https://facecarserver.000webhostapp.com/facecar/get_all.php";
    String URL_DELETE="https://facecarserver.000webhostapp.com/facecar/delete_profile.php";


    static ArrayList<User> userList;
    CustomAdopterPL adopter;
    SMS sms;

    String intReg,username;
    SharedPreferences prf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context =this;
        userList = new ArrayList<User>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_list);
        prf = getSharedPreferences("LogDetails",MODE_PRIVATE);
        username = prf.getString("USERNAME","");


        Intent i = getIntent();

        intReg=i.getStringExtra("REG");
        getProfiles(intReg);
        loading=findViewById(R.id.loadingProfileList);

        listView = findViewById(R.id.lvProfileList);
        adopter =new CustomAdopterPL(context,R.layout.profile_list_single_row,userList);

        listView.setAdapter(adopter);
        sms=new SMS(ProfileList.this);




        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(userList.get(i).getName());
                builder.setMessage("\tEmail\t: "+userList.get(i).getEmail()+"\n\tPhone\t: "+userList.get(i).getPhone()+"\n\tVehicle\t: "+userList.get(i).getRegNo());

                builder.setNegativeButton("ResetPW", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int c) {
                        //Toasty.success(context,"resat"+i,Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(context, ChangePW.class);
                        intent.putExtra("ID", userList.get(i).getID());
                        intent.putExtra("NUMBER",userList.get(i).getPhone());
                        intent.putExtra("REG",intReg);
                        startActivity(intent);


                    }
                });
                builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int d) {
                        AlertDialog.Builder b = new AlertDialog.Builder(context);
                        b.setTitle("YOU ARE GOING TO DELETE THIS ACCOUNT. ARE YOU SURE?");
                        b.setPositiveButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int d) {

                            }
                        });
                        b.setNeutralButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int d) {

                                String type=userList.get(i).getAcctype();
                                deleteAcc(userList.get(i).getID(),userList.get(i).getAcctype(),userList.get(i).getRegNo(),"Account Deleted Successfuly",URL_DELETE);
                                if(type.equals("O")){
                                    prf = getSharedPreferences("LogDetails",MODE_PRIVATE);
                                    SharedPreferences.Editor editor = prf.edit();
                                    editor.putString("LOGSTATUS","LOGGEDOUT");
                                    editor.putString("LOGEDACC","N");
                                    editor.putString("PIN"+username,"");
                                    editor.commit();

                                    //Deleting existing Faces
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
                                    //*************************

                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    Toasty.info(context,"You have been logged out",Toast.LENGTH_SHORT).show();
                                    startActivity(intent);

                                }else{
                                    sms.sendProfSMS(userList.get(i).getPhone(),"account","deleted");
                                    Intent intent = new Intent(context, ProfileList.class);
                                    intent.putExtra("REG",intReg);

                                    startActivity(intent);}

                            }
                        });
                        b.show();


                    }
                });
                builder.setPositiveButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });


                builder.show();
            }
        });

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
                                adopter.notifyDataSetChanged();
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


    public void back(View view) {
        Intent intent = new Intent(this, OwnerDashboard.class);
        startActivity(intent);


    }

    public void getProfiles(final String REG){
        //userList.clear();
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
                                    String id=object.getString("id").trim().toString();
                                    String name = object.getString("name").trim();
                                    String phone = object.getString("phone").trim();
                                    String email = object.getString("email").trim();
                                    String regNo= object.getString("regno").trim();
                                    String accType = object.getString("acctype").trim();


                                    //User u=new User(id,name,phone,email,regNo);
                                    userList.add(new User(id,name,phone,email,regNo,accType));
                                    adopter.notifyDataSetChanged();

                                }

                                loading.setVisibility(View.INVISIBLE);
                                if(userList.size()==1){
                                    Toast.makeText(getApplicationContext(),"NO Member accounts",Toast.LENGTH_SHORT).show();
                                }




//                                Toasty.success(getApplicationContext(),"Successfull Connection"+jsonArray.length(), Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toasty.error(getApplicationContext(),"Unsuccessful Connection", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toasty.error(getApplicationContext(),"ErrorCATCH"+e.toString(),Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(getApplicationContext(),"ErrorCATCH ",Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("regNo",REG);
                return params;
            }
        };
        RequestQueue queue= Volley.newRequestQueue(this);
        queue.add(stringRequest);


    }

}


