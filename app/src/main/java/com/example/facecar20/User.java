package com.example.facecar20;

public class User {
    private String name,phone,email,regNo,acctype;
    private String id;
    public User(String ID,String NAME,String PHONE,String EMAIL,String REGNO,String ACCTYPE){

        id=ID;
        name=NAME;
        phone=PHONE;
        email=EMAIL;
        regNo=REGNO;
        acctype=ACCTYPE;
    }
    public String getID(){
        return  id;
    }
    public String getName(){
        return  name;
    }
    public String getPhone(){
        return  phone;
    }
    public String getRegNo(){
        return  regNo;
    }
    public String getEmail(){
        return  email;
    }
    public String getAcctype(){
        return  acctype;
    }

    public void setRegNo(String REGNO){
        regNo=REGNO;
    }
    public void setID(String ID){
        id=ID;
    }
    public void setName(String NAME){
        name=NAME;
    }
    public void setPhone(String PHONE){
        phone=PHONE;
    }
    public void setEmail(String EMAIL){
        email=EMAIL;
    }
    public void setAcctype(String ACCTYPE){
        acctype=ACCTYPE;
    }

}
