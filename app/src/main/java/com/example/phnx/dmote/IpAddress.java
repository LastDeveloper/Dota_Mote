package com.example.phnx.dmote;

/**
 * Created by Josiah on 4/2/2016.
 */
public class IpAddress {

    int id;
    public String name;
    public String address;

    IpAddress(){

    }
    public IpAddress( int id, String name, String address){
        this.id = id;
        this.name = name;
        this.address = address;
    }

    public IpAddress(String name, String address){
        this.name = name;
        this.address = address;
    }
    public int getId(){
        return this.id;
    }

}


