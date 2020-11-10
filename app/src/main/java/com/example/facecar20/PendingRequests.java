package com.example.facecar20;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class PendingRequests extends AppCompatActivity {

    Context context;
    ListView listView;
    ProgressBar loading;
    TextView noReqs;

    String URL_DELETE="https://facecarserver.000webhostapp.com/facecar/delete_profile.php";
    String URL_ACC="https://facecarserver.000webhostapp.com/facecar/update_user_status.php";
    String URL_RESET="https://facecarserver.000webhostapp.com/facecar/resetpass.php";
    String URL_DECLINE="https://facecarserver.000webhostapp.com/facecar/declineRequests.php";
    String URL="https://facecarserver.000webhostapp.com/facecar/get_all_reqs.php";

    static ArrayList<Request> reqList;
    PendingReqAdaptor adopter;
    SMS sms;
    SharedPreferences prf;


    String intReg,guestSt,welcomStr;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        context = this;
        reqList=new ArrayList<Request>();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_requests);

        prf = getSharedPreferences("LogDetails",MODE_PRIVATE);

        Intent i=getIntent();
        intReg=i.getStringExtra("REG");
        guestSt=i.getStringExtra("GUEST");
        welcomStr=i.getStringExtra("NAME");
        loading=findViewById(R.id.loadingPendingReq);
        noReqs = findViewById(R.id.txtNoReqs);
        getReqs(intReg);

        sms=new SMS(PendingRequests.this);



        listView = findViewById(R.id.lvPendingReqs);
        adopter =new PendingReqAdaptor(context,R.layout.pending_req_single_row,reqList);
        listView.setAdapter(adopter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(reqList.get(i).getNAME());


                if (reqList.get(i).getREQTYPE().equals("RESPW")){

                    builder.setMessage("is requesting a password reset.");
                    builder.setNegativeButton("Accept", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int d) {
                            AlertDialog.Builder b = new AlertDialog.Builder(context);
                            b.setTitle("ARE YOU SURE?");
                            b.setPositiveButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int d) {

                                }
                            });
                            b.setNeutralButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int d) {

                                    resetPW(reqList.get(i).getUSERID(),reqList.get(i).getREQID(),"Password reset",URL_RESET);
                                    Intent intent = new Intent(context, PendingRequests.class);
                                    sms.sendReqSMS(reqList.get(i).getNUMBER(),"Password Reset","accepted");

                                    intent.putExtra("REG",intReg);
                                    adopter.notifyDataSetChanged();

                                    //Toasty.success(context,"",Toast.LENGTH_SHORT).show();

                                    startActivity(intent);

                                }
                            });
                            b.show();

                        }
                    });
                    builder.setPositiveButton("Decline", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int d) {
                            AlertDialog.Builder b = new AlertDialog.Builder(context);
                            b.setTitle("ARE YOU SURE?");
                            b.setPositiveButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int d) {

                                }
                            });
                            b.setNeutralButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int d) {
                                    declineReq(URL_DECLINE,reqList.get(i).getREQID(),"Request Decline");
                                    Intent intent = new Intent(context, PendingRequests.class);
                                    sms.sendReqSMS(reqList.get(i).getNUMBER(),"Password Reset","declined");
                                    intent.putExtra("REG",intReg);
                                    adopter.notifyDataSetChanged();
                                    startActivity(intent);


                                }
                            });
                            b.show();

                        }
                    });
                    builder.show();

                }else if (reqList.get(i).getREQTYPE().equals("DEL")){

                    builder.setMessage("is requesting permission to create account");
                    builder.setNegativeButton("Accept", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int d) {
                            AlertDialog.Builder b = new AlertDialog.Builder(context);
                            b.setTitle("ARE YOU SURE?");
                            b.setPositiveButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int d) {

                                }
                            });
                            b.setNeutralButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int d) {

                                    delete(URL_DELETE,reqList.get(i).getUSERID(),"Account Deleted");
                                    sms.sendReqSMS(reqList.get(i).getNUMBER(),"deletion of account","accepted");
                                    Intent intent = new Intent(context, PendingRequests.class);
                                    intent.putExtra("REG",intReg);

                                    //Toasty.success(context,"",Toast.LENGTH_SHORT).show();

                                    startActivity(intent);


                                }
                            });
                            b.show();

                        }
                    });
                    builder.setPositiveButton("Decline", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int d) {
                            AlertDialog.Builder b = new AlertDialog.Builder(context);
                            b.setTitle("ARE YOU SURE?");
                            b.setPositiveButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int d) {

                                }
                            });
                            b.setNeutralButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int d) {
                                    declineReq(URL_DECLINE,reqList.get(i).getREQID(),"Request Decline");
                                    sms.sendReqSMS(reqList.get(i).getNUMBER(),"deletion of account","declined");
                                    Intent intent = new Intent(context, PendingRequests.class);
                                    intent.putExtra("REG",intReg);
                                    adopter.notifyDataSetChanged();
                                    startActivity(intent);


                                }
                            });
                            b.show();



                        }
                    });
                    builder.show();

                }else if(reqList.get(i).getREQTYPE().equals("ACC")){
                    builder.setMessage("is requesting Account ");
                    builder.setNegativeButton("Accept", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int d) {
                            AlertDialog.Builder b = new AlertDialog.Builder(context);
                            b.setTitle("ARE YOU SURE?");
                            b.setPositiveButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int d) {

                                }
                            });
                            b.setNeutralButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int d) {
                                    AccCre(URL_ACC,reqList.get(i).getREQID(),"A",reqList.get(i).getUSERID(),"A","Accepted");
                                    sms.sendReqSMS(reqList.get(i).getNUMBER(),"create account","accepted");

                                    Intent intent = new Intent(context, PendingRequests.class);

                                    intent.putExtra("REG",intReg);

                                    //Toasty.success(context,"",Toast.LENGTH_SHORT).show();

                                    startActivity(intent);

                                }
                            });
                            b.show();

                        }
                    });
                    builder.setPositiveButton("Decline", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, final int d) {
                            AlertDialog.Builder b = new AlertDialog.Builder(context);
                            b.setTitle("ARE YOU SURE?");
                            b.setPositiveButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int d) {

                                }
                            });
                            b.setNeutralButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int d) {
                                    AccCre(URL_ACC,reqList.get(i).getREQID(),"D",reqList.get(i).getUSERID(),"D","Declined");

                                    sms.sendReqSMS(reqList.get(i).getNUMBER(),"create account","declined");

                                    Intent intent = new Intent(context, PendingRequests.class);

                                    intent.putExtra("REG",intReg);

                                    startActivity(intent);


                                }
                            });
                            b.show();

                        }
                    });
                    builder.show();

                }



            }
        });


    }

    public void back(View view) {
        Intent intent = new Intent(this, OwnerDashboard.class);
        startActivity(intent);
    }

    public void delete(String URL, final String id, final String prompt){
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST,URL,
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
                params.put("acctype","M");
                params.put("reg","");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }
    public void AccCre(String URL, final String reqid, final String reqStatus, final String uid,final String userStatus, final String prompt){
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST,URL,
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
                               // Toasty.success(context, "error",Toast.LENGTH_SHORT).show();
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
                params.put("userid",uid);
                params.put("reqid",reqid);
                params.put("userstatus",userStatus);
                params.put("reqstatus",reqStatus);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }


    public void getReqs(final String REG){
        //userList.clear();
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, URL,
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
                                    String userid=object.getString("userid").trim().toString();
                                    String reqid=object.getString("reqid").trim().toString();
                                    String name = object.getString("name").trim();
                                    String reqtype = object.getString("reqType").trim();
                                    String status = object.getString("status").trim();
                                    String regno= object.getString("regno").trim();
                                    String number = object.getString("number").trim();


                                    //User u=new User(id,name,phone,email,regNo);
                                    reqList.add(new Request(userid,reqid,name,reqtype,regno,status,number));
                                    adopter.notifyDataSetChanged();

                                }

                                loading.setVisibility(View.GONE);
                                if(reqList.size()==0){

                                    noReqs.setText("(No pending Requests)");

                                }




                                //Toasty.success(getApplicationContext(),"Successfull Connection"+jsonArray.length(), Toast.LENGTH_SHORT).show();
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
                Toasty.error(getApplicationContext(),"ErrorCATCH "+error.toString(),Toast.LENGTH_SHORT).show();
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

    public void resetPW(final String uid,final String reqid, final String prompt,String URL){
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST,URL,
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
                params.put("userid",uid);
                params.put("reqid",reqid);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void declineReq(String URL, final String id, final String prompt) {
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if (success.equals("1")) {
                                Toasty.success(context, prompt, Toast.LENGTH_SHORT).show();
                                adopter.notifyDataSetChanged();
                            } else {
                                Toasty.success(context, "error", Toast.LENGTH_SHORT).show();
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
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}
