package com.innovation.neha.tracklocation.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.innovation.neha.tracklocation.Pojos.Payment;
import com.innovation.neha.tracklocation.Pojos.SubOrder;
import com.innovation.neha.tracklocation.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by Neha on 12-12-2017.
 */

public class RecyclerViewPaymentAdapter extends RecyclerView.Adapter<RecyclerViewPaymentAdapter.Viewholder>  {

    Context context;
    private LayoutInflater mInflater;
    private List<Payment> mData = Collections.emptyList();
    private ItemClickListener mClickListener;

    public RecyclerViewPaymentAdapter(Context context, List<Payment> data){

        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context=context;

    }


    @Override
    public RecyclerViewPaymentAdapter.Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {

       Log.e("Viewholder","Createviewholder");
        View view = mInflater.inflate(R.layout.recyclerview_paydetl_item_card, parent, false);
        view.setOnClickListener(mClickListener);
        final Viewholder viewholder = new Viewholder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewPaymentAdapter.Viewholder holder, int position) {
        Log.e("Viewholder","bindviewholder pay");
        final Payment payobj = mData.get(position);
        Log.e("Viewholder first value",payobj.getMethod());
        holder.method.setText(payobj.getMethod());
        holder.rcvd.setText(String.valueOf(payobj.getRcvd()));
        holder.rest.setText(String.valueOf(payobj.getRest()));
        String[] s=payobj.getDate().split(" ");
        holder.date.setText(s[0]);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public class Viewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView method, rcvd, rest,date;


        public Viewholder(View itemView) {
            super(itemView);

            method = (TextView) itemView.findViewById(R.id.tv_pay_method);
            rcvd = (TextView) itemView.findViewById(R.id.tv_pay_rcvd);
            rest = (TextView) itemView.findViewById(R.id.tv_pay_rest);
            date=(TextView)itemView.findViewById(R.id.tv_pay_date);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

         //   Log.e("onClick","called");
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());

            //Log.e("View", String.valueOf(view.getId()));
         //   Log.e("view2", String.valueOf(R.id.btn_edit));

        }


    }




        // parent activity will implement this method to respond to click events
        public interface ItemClickListener extends View.OnClickListener {
            void onItemClick(View view, int position);

        }


}
