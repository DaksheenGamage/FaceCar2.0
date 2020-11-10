package com.example.facecar20;

public class Request {
    private String USERID,REQID,NAME,REQTYPE,REGNO,STATUS,NUMBER;
    public Request(String uid, String reqid,String name,String reqtype,String regno,String status,String number ){
        USERID=uid;
        REQID=reqid;
        NAME=name;
        REQTYPE=reqtype;
        REGNO=regno;
        STATUS=status;
        NUMBER=number;
    }
    public String getUSERID(){return USERID;}
    public String getREQID(){return REQID;}
    public String getNAME(){return NAME;}
    public String getREQTYPE(){return REQTYPE;}
    public String getREGNO(){return REGNO;}
    public String getSTATUS(){return STATUS;}
    public String getNUMBER(){return NUMBER;}

    public void setUSERID(String userid){USERID=userid;}
    public void setREQID(String reqid){REQID=reqid;}
    public void setNAME(String name){NAME=name;}
    public void setREQTYPE(String reqtype){REQTYPE=reqtype;}
    public void setREGNO(String regno){REGNO=regno;}
    public void setSTATUS(String status){STATUS=status;}
    public void setNUMBER(String number){NUMBER=number;}
}
