package com.innovation.neha.tracklocation.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.innovation.neha.tracklocation.Adapters.RecyclerViewAdapter;
import com.innovation.neha.tracklocation.Adapters.RecyclerViewDetailAdapter;
import com.innovation.neha.tracklocation.AppController;
import com.innovation.neha.tracklocation.Pojos.Order;
import com.innovation.neha.tracklocation.Pojos.SubOrder;
import com.innovation.neha.tracklocation.R;
import com.innovation.neha.tracklocation.Storage.SPrefUserInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView,recyclerViewDetail;
    RecyclerViewAdapter adapter;
    RecyclerViewDetailAdapter adapterDetail;
    List<Order> ord_hist_list=new ArrayList<Order>();
    List<SubOrder> subord_hist_list=new ArrayList<SubOrder>();
    String tag_string_req = "jsonobj_req",ordforintent,custforintent,totforintent;
    public static Dialog dialog;
    public static boolean flag=false;
    public static int posfordelete;
    Intent intent;
    ProgressDialog progressDialog;
    PopupWindow popupWindow;
    private TextView dialog_result;
    View customView;
    Button yes,no;
    public static String dh_ord,dh_cust,dh_tot,u_id;
    private SPrefUserInfo sPrefUserInfo;


    public static int mainAdapterPosition=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);



         dialog = new Dialog(this, R.style.DialogSlideAnim);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.edit_history);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();



        progressDialog =new ProgressDialog(this);
        progressDialog.setMessage("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
        recyclerViewDetail = (RecyclerView) dialog.findViewById(R.id.rv_hist_detail);
        recyclerViewDetail.setHasFixedSize(true);
        recyclerViewDetail.setLayoutManager(new LinearLayoutManager(this));
      //  recyclerViewDetail.addItemDecoration(new SimpleDividerItemDecoration(context, R.drawable.divider));



        recyclerView = (RecyclerView) findViewById(R.id.rv_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        // Inflate the custom layout/view
        customView = inflater.inflate(R.layout.activity_del_dialog, null);

        dialog_result = (TextView) customView.findViewById(R.id.tv_del_dialog);
        yes = (Button) customView.findViewById(R.id.btn_del_dialog_yes);
        no = (Button) customView.findViewById(R.id.btn_del_dialog_no);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);

       intent=new Intent(getApplicationContext(),PlaceOrderActivity.class);

       sPrefUserInfo=new SPrefUserInfo(HistoryActivity.this);
        //Log.e("ResponseSize", String.valueOf(ord_hist_list.size()));


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(dialog.isShowing())
            dialog.dismiss();

        // populating main orders history
        populateHistory();
       // Toast.makeText(getApplicationContext(),"populate history called Resume",Toast.LENGTH_SHORT).show();
    }

    /*
    *  function to setup main adapter
    * */
    public void setupAdapter()
    {
        adapter = new RecyclerViewAdapter(getApplicationContext(), ord_hist_list);
        recyclerView.setAdapter(adapter);


        adapter.setClickListener(new RecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                mainAdapterPosition=position;
                Log.e("onItemClick", "called");
                if(view.getId()==R.id.btn_edit)
                {
                    Log.e("onItemClick", "Edit");

                    editHistory(position);

                }
                else if(view.getId()==R.id.btn_del)
                {
                    posfordelete=position;
                    Log.e("onItemClick", "Delete");
                    dialog_result.setText("Do you really want to delete this record?");
                    no.setText("NO");
                    yes.setVisibility(View.VISIBLE);
                    popupWindow = new PopupWindow(
                            customView,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                    );
                    //  popupWindow.setAnimationStyle(R.style.animationdialog);
                    // popupWindow.setElevation(20.5f);
                    //Toast.makeText(getApplicationContext(),"Elevation:"+String.valueOf(popupWindow.getElevation()), Toast.LENGTH_SHORT).show();
                    popupWindow.setOutsideTouchable(true);
                    popupWindow.showAtLocation(getCurrentFocus(), Gravity.CENTER, 0, 0);
                    if(flag==true)
                       // delHistory(position);
                    Log.e("list before notify", String.valueOf(ord_hist_list.size()));
                    //adapter.notifyDataSetChanged();
                }

                else if(view.getId()==R.id.btn_h_det)
                {
                    Log.e("onItemClick", "Details");

                    populateSubHistory(position);

                }
                Log.e("onItemClickposition", String.valueOf(position));
            }

            @Override
            public void onClick(View view) {

                Log.e("onAClick", "called");
            }
        });

        if(progressDialog.isShowing())
            progressDialog.dismiss();
    }

    /*
    *  function to setup detail adapter
    * */
    public void setupDetailAdapter()
    {
        adapterDetail = new RecyclerViewDetailAdapter(getApplicationContext(), subord_hist_list);
        recyclerViewDetail.setAdapter(adapterDetail);

        adapterDetail.setClickListener(new RecyclerViewDetailAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(view.getId()==R.id.btn_eh_edit)
                {
                    Log.e("onItemClickSub", "Edit");
                    editSubHistory(position);
                }
               /* else if(view.getId()==R.id.btn_eh_del)
                {
                    Log.e("onItemClickSub", "Delete");
                    deleteSubHistory(position);

                }*/
            }

            @Override
            public void onClick(View view) {

            }
        });


        if(progressDialog.isShowing())
            progressDialog.dismiss();
    }

    /*
    *  function to populate details-suborder history of selected main ordr
    * */
    public void populateSubHistory(int position)
    {
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if(subord_hist_list!=null)
            subord_hist_list.clear();

        Log.e("onItemClick",String.valueOf(ord_hist_list.get(position).getClntName()));
        String ord=ord_hist_list.get(position).getOrdNo();
        Log.e("onItemClickord",ord);

        try {
            ord= URLEncoder.encode(String.valueOf(ord), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = "http://www.thinkbank.co.in/Rajeshahi_app/editSubOrder.php?ord="+ord;
        Log.e("URL",url);

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {


                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Response",String.valueOf(response));

                        try {

                            for(int i = 0; i<response.names().length(); i++){

                                Log.e("Responselength",String.valueOf(response.names().length()));
                                Log.e("Response",String.valueOf(response));
                                JSONArray a= (JSONArray) response.get(response.names().getString(i));
                                SubOrder objOrder=new SubOrder(a.getString(0),a.getInt(1),a.getInt(2),a.getDouble(3));
                                Log.e("ResponseObject",String.valueOf(objOrder));

                                subord_hist_list.add(objOrder);
                                Log.e("ResponseSize", String.valueOf(subord_hist_list.size()));
                                Log.e("onItemlist",subord_hist_list.get(i).getProd());

                                setupDetailAdapter();

                            }



                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("ResponseCatch",String.valueOf(e));
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.e("ResponseError",String.valueOf(error));
                    }
                });

        AppController.getInstance().addToRequestQueue(jsonRequest, tag_string_req);
        dialog.show();

    }
    public void editHistory(int position)
    {

        //dialog.show();
        Log.e("onItemClick",String.valueOf(ord_hist_list.get(position).getClntName()));

         ordforintent=ord_hist_list.get(position).getOrdNo();
        custforintent=ord_hist_list.get(position).getClntName();
        Double tot=ord_hist_list.get(position).getTot();

        totforintent=String.valueOf(tot);

        try {
            ordforintent= URLEncoder.encode(String.valueOf(ordforintent), "UTF-8");
            custforintent= URLEncoder.encode(String.valueOf(custforintent), "UTF-8");
            totforintent= URLEncoder.encode(String.valueOf(totforintent), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = "http://www.thinkbank.co.in/Rajeshahi_app/editOrder.php?ord="+ordforintent+"&&cust="+custforintent+"&&tot="+tot;
        Log.e("URL",url);

        JsonArrayRequest jsonRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {


                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("Response",String.valueOf(response));
                       // Toast.makeText(getBaseContext(),"Response:"+response,Toast.LENGTH_SHORT).show();
                        // the response is already constructed as a JSONObject!
                        try {


                          //  for(int i = 0; i<response.length(); i++){

                                intent.putExtra("ord",String.valueOf(response.get(0)));
                                intent.putExtra("cust",String.valueOf(response.get(1)));
                                intent.putExtra("tot",String.valueOf(response.get(2)));
                                intent.putExtra("method",String.valueOf(response.get(3)));
                                intent.putExtra("rcvd",String.valueOf(response.get(4)));
                                intent.putExtra("rest",String.valueOf(response.get(5)));
                                intent.putExtra("cheque",String.valueOf(response.get(6)));
                                intent.putExtra("dd",String.valueOf(response.get(7)));
                                intent.putExtra("flag","main");
                                intent.putExtra("count",String.valueOf(response.get(8)));


                                    startActivity(intent);



                                Log.e("ResponseObject",String.valueOf(response.get(0)));
                           // }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("ResponseCatch",String.valueOf(e));
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.e("ResponseError",String.valueOf(error));
                        progressDialog.dismiss();
                        yes.setVisibility(View.GONE);
                        dialog_result.setText("Unable to process the Request");
                        no.setText("OK");

                        popupWindow = new PopupWindow(
                                customView,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                        );

                        popupWindow.setOutsideTouchable(true);
                        if(popupWindow.isShowing())
                            popupWindow.dismiss();
                        popupWindow.showAtLocation(getCurrentFocus(), Gravity.CENTER, 0, 0);


                    }
                });

        AppController.getInstance().addToRequestQueue(jsonRequest, tag_string_req);


    }

    public void editSubHistory(int position)
    {

        ordforintent=ord_hist_list.get(mainAdapterPosition).getOrdNo();
        custforintent=ord_hist_list.get(mainAdapterPosition).getClntName();
        Double tot=ord_hist_list.get(mainAdapterPosition).getTot();
        totforintent=String.valueOf(tot);

        String p=subord_hist_list.get(position).getProd();
        String w=String.valueOf(subord_hist_list.get(position).getWt());
        String q=String.valueOf(subord_hist_list.get(position).getQty());
        String pr=String.valueOf(subord_hist_list.get(position).getPrice());

        intent.putExtra("ord",ordforintent);
        intent.putExtra("cust",custforintent);
        intent.putExtra("tot",totforintent);
        intent.putExtra("p",p);
        intent.putExtra("w",w);
        intent.putExtra("q",q);
        intent.putExtra("pr",pr);
        intent.putExtra("flag","sub");

        startActivity(intent);


    }
    public void deleteSubHistory(int position)
    {
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        String url="http://www.thinkbank.co.in/Rajeshahi_app/delSubOrder.php";
        final String ord=ord_hist_list.get(mainAdapterPosition).getOrdNo();
        final String prod=subord_hist_list.get(position).getProd();
        final int wt=subord_hist_list.get(position).getWt();
        final Double price=subord_hist_list.get(position).getPrice();
        subord_hist_list.remove(position);
        Log.e("wt",String.valueOf(wt));
        StringRequest stringRequest=new StringRequest
                (Request.Method.POST,
                        url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                Log.e("ResponseDelete",response);
                                Log.e("list before notify", String.valueOf(subord_hist_list.size()));

                                adapterDetail.notifyDataSetChanged();
                                progressDialog.dismiss();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }){

                         @Override
                      protected Map<String, String> getParams() throws AuthFailureError {
                             Map<String, String> params = new HashMap<>();
                             params.put("ord", ord);
                             params.put("prod", prod);
                             params.put("wt",String.valueOf(wt));
                             params.put("price",String.valueOf(price));
                             return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);
        adapterDetail.notifyDataSetChanged();
    }

    /*
    *  function to delete main order
    * */
    public void delHistory(final int position) {

        if (!ord_hist_list.isEmpty()) {
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            final String o = String.valueOf(ord_hist_list.get(position).getOrdNo());

            // final String cust="",tot="";
            Log.e("Ord", String.valueOf(ord_hist_list.get(position).getOrdNo()));
            Log.e("cust", String.valueOf(ord_hist_list.get(position).getClntName()));
            Log.e("tot", String.valueOf(ord_hist_list.get(position).getTot()));

            try {
                dh_ord = URLEncoder.encode(String.valueOf(ord_hist_list.get(position).getOrdNo()), "UTF-8");
                dh_cust = URLEncoder.encode(String.valueOf(ord_hist_list.get(position).getClntName()), "UTF-8");
                dh_tot = URLEncoder.encode(String.valueOf(ord_hist_list.get(position).getTot()), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            ord_hist_list.remove(position);

            // String url = "http://www.thinkbank.co.in/Rajeshahi_app/delOrder.php?ord="+dh_ord+"&&cust="+dh_cust+"&&tot="+dh_tot;
            String url = "http://www.thinkbank.co.in/Rajeshahi_app/delOrder.php";
            Log.e("URL", url);

            StringRequest strReq = new StringRequest(Request.Method.POST,
                    url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {

                    Log.e("Response", response.toString());
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    adapter.notifyDataSetChanged();

                    yes.setVisibility(View.GONE);
                    dialog_result.setText("Record Deleted!");
                    no.setText("OK");

                    popupWindow = new PopupWindow(
                            customView,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                    );

                    popupWindow.setOutsideTouchable(true);
                    popupWindow.showAtLocation(getCurrentFocus(), Gravity.CENTER, 0, 0);
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("TAG", "Error: " + error.getMessage());
                    Log.e("ResponseError", error.toString());

                    progressDialog.dismiss();
                    yes.setVisibility(View.GONE);
                    dialog_result.setText("Unable to delete Record !");
                    no.setText("OK");

                    popupWindow = new PopupWindow(
                            customView,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                    );

                    popupWindow.setOutsideTouchable(true);
                    if(popupWindow.isShowing())
                        popupWindow.dismiss();
                    popupWindow.showAtLocation(getCurrentFocus(), Gravity.CENTER, 0, 0);



                }
            }) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("ord", o);
                    params.put("cust", dh_cust);
                    params.put("tot", String.valueOf(dh_tot));

                    return params;
                }
            };

            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        }
    }

    /*
    *  function to fetch & populate main order history
    * */
    public void populateHistory()
    {

        try {
            u_id= URLEncoder.encode(String.valueOf(/*SplashActivity.*/sPrefUserInfo.getUserInfo()), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        progressDialog.setMessage("please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        if(ord_hist_list!=null)
            ord_hist_list.clear();

        Log.e("populateWtSpinner","called");
        String url = "http://www.thinkbank.co.in/Rajeshahi_app/GetOrderHistory.php?u_id="+u_id;
        Log.e("URL",url);

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Response",String.valueOf(response));


                                            //  Toast.makeText(getBaseContext(),"Response:"+response,Toast.LENGTH_SHORT).show();
                        // the response is already constructed as a JSONObject!

                            try {

                                for (int i = 0; i < response.names().length(); i++) {

                                    JSONArray a = (JSONArray) response.get(response.names().getString(i));
                                    Order objOrder = new Order(a.getString(0), a.getString(1), a.getDouble(2));
                                    Log.e("ResponseObject", String.valueOf(objOrder));

                                    ord_hist_list.add(objOrder);
                                    Log.e("ResponseSize", String.valueOf(ord_hist_list.size()));
                                }

                            /*Log.e("ResponseSize", String.valueOf(ord_hist_list.size()));
                            adapter = new RecyclerViewAdapter(getApplicationContext(), ord_hist_list);
                            recyclerView.setAdapter(adapter);
*/
                                setupAdapter();


                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("ResponseCatch", String.valueOf(e));
                            }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.e("ResponseError",String.valueOf(error));
                        progressDialog.dismiss();



                        if(error.toString().contains("Value [] of type org.json.JSONArray cannot be converted to JSONObject"))
                        {
                            //Toast.makeText(getApplicationContext(),"No Records",Toast.LENGTH_SHORT).show();
                        yes.setVisibility(View.GONE);

                        dialog_result.setText("No Records Found");
                        no.setText("OK");

                        popupWindow = new PopupWindow(
                                customView,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                        );

                        popupWindow.setOutsideTouchable(true);
                        if(popupWindow.isShowing())
                            popupWindow.dismiss();
                        popupWindow.showAtLocation(getCurrentFocus(), Gravity.CENTER, 0, 0);
                        }
                        else
                            Toast.makeText(getApplicationContext(),"Unable to fetch.Please try again",Toast.LENGTH_SHORT).show();


                    }
                });

        AppController.getInstance().addToRequestQueue(jsonRequest, tag_string_req);



    }


    @Override
    public void onClick(View view) {

        switch(view.getId()){
            case R.id.btn_del_dialog_yes:

                    flag = true;
                    popupWindow.dismiss();
                    delHistory(posfordelete);


            break;

            case R.id.btn_del_dialog_no:flag=false;
            popupWindow.dismiss();
            finish();
            break;
        }
    }
}
