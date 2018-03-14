package com.innovation.neha.tracklocation.Pojos;

/**
 * Created by Neha on 20-12-2017.
 */

public class SubOrder {

    String prod;
    int wt,qty;
    Double price,rest;

    public SubOrder(String p,int w,int q,Double pr,Double rest)
    {
        this.prod=p;
        this.wt=w;
        this.qty=q;
        this.price=pr;
        this.rest=rest;
    }

    public String getProd()
    {
        return prod;
    }

    public int getWt() { return wt;}

    public Double getPrice(){ return price; }

    public int getQty(){ return qty;}

    public Double getRest(){return rest;}
}
