package com.example.facecar20;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

//custom ArrayAdaptor for userList

class CustomAdopterPL extends ArrayAdapter<User> {
    private Context context;
    private int resource;
    ArrayList<User> users;


    CustomAdopterPL(Context context, int resource, ArrayList<User> users){
        super(context,resource,users);
        this.context=context;
        this.resource=resource;
        this.users=users;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(resource,parent,false);

        TextView nameView = row.findViewById(R.id.txtPendingReqName);
        ImageView owner  = row.findViewById(R.id.imageOwner);
        ImageView member = row.findViewById(R.id.imageMember);




        User user = users.get(position);
        nameView.setText(user.getName());
        if(user.getAcctype().equals("O")){
            owner.setVisibility(View.VISIBLE);
        }else if(user.getAcctype().equals("M")){
            member.setVisibility(View.VISIBLE);
        }

        return row;
    }
}
