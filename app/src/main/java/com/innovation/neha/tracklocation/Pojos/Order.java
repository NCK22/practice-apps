package com.innovation.neha.tracklocation.Pojos;

/**
 * Created by Neha on 12-12-2017.
 */

public class Order {

    String ord_no,clnt_name;
    Double tot_amt;

    public Order(String no,String name,Double tot)
    {
        this.ord_no=no;
        this.clnt_name=name;
        this.tot_amt=tot;
    }

    public String getOrdNo()
    {
        return ord_no;
    }

    public String getClntName() { return clnt_name;}

    public Double getTot(){ return tot_amt; }
}
