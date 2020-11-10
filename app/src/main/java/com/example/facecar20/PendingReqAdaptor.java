package com.example.facecar20;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class PendingReqAdaptor extends ArrayAdapter<Request> {
    private Context context;
    private int resource;
    ArrayList<Request> requests;

    PendingReqAdaptor(Context context,int resource,ArrayList<Request> requests){
        super(context,resource,requests);
        this.context=context;
        this.resource=resource;
        this.requests=requests;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(resource,parent,false);
        TextView nameView=row.findViewById(R.id.txtPendingReqName);
        TextView typeView=row.findViewById(R.id.txtReqType);

        Request r=requests.get(position);
        nameView.setText(r.getNAME());
        if(r.getREQTYPE().equals("RESPW")) {
            typeView.setText("Password Reset Request");
        }else if(r.getREQTYPE().equals("DEL")){
            typeView.setText("Account Deletion Request");
        }else if(r.getREQTYPE().equals("ACC")){
            typeView.setText("Account Creation Request");
        }

        return row;
    }
}
