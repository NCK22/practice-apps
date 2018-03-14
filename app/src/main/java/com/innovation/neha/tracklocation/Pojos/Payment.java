package com.innovation.neha.tracklocation.Pojos;

/**
 * Created by Neha on 20-12-2017.
 */

public class Payment {

    String method,date;
    Double rcvd,rest;

    public Payment(String method,Double rcvd, Double rest,String date)
    {
        this.method=method;
        this.rcvd=rcvd;
        this.rest=rest;
        this.date=date;
    }

    public String getMethod()
    {
        return method;
    }

    public Double getRcvd(){ return rcvd; }

    public Double getRest(){return rest;}

    public String getDate(){return date;}
}
