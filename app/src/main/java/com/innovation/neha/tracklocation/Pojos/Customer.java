package com.innovation.neha.tracklocation.Pojos;

import java.sql.Timestamp;

/**
 * Created by Neha on 12-12-2017.
 */

public class Customer {

    String id,cust_name,date,addr,cont,gst,pan,lat,lng;



    public Customer(String name,String date,String addr,String cont,String gst,String pan,String id,String lat,String lng)
    {
        this.cust_name=name;
        this.date=date;
        this.addr=addr;
        this.cont=cont;
        this.gst=gst;
        this.pan=pan;
        this.id=id;
        this.lat=lat;
        this.lng=lng;

    }

    public String getCustId(){ return id;}

    public String getCustName() { return cust_name;}

    public String getCustDate(){ return date.substring(0,10); }

    public String getCustAddr(){ return addr;}

    public String getCustCont(){ return cont;}

    public String getCustGst(){ return gst;}

    public String getCustPan(){ return pan;}

    public String getCustLat(){ return lat;}

    public String getCustLng(){ return lng; }
}
