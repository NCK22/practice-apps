package com.innovation.neha.tracklocation.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.innovation.neha.tracklocation.Pojos.Order;
import com.innovation.neha.tracklocation.Pojos.SubOrder;
import com.innovation.neha.tracklocation.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by Neha on 12-12-2017.
 */

public class RecyclerViewDetailAdapter extends RecyclerView.Adapter<RecyclerViewDetailAdapter.Viewholder>  {

    Context context;
    private LayoutInflater mInflater;
    private List<SubOrder> mData = Collections.emptyList();
    private ItemClickListener mClickListener;

    public RecyclerViewDetailAdapter(Context context, List<SubOrder> data){

        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context=context;

    }


    @Override
    public RecyclerViewDetailAdapter.Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {

       Log.e("Viewholder","Createviewholder");
        View view = mInflater.inflate(R.layout.recyclerview_details_item_card, parent, false);
        view.setOnClickListener(mClickListener);
        final Viewholder viewholder = new Viewholder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewDetailAdapter.Viewholder holder, int position) {
        Log.e("Viewholder","bindviewholder");
        final SubOrder orderobj = mData.get(position);
        holder.prod.setText(orderobj.getProd());
        holder.wt.setText(String.valueOf(orderobj.getWt())+" gms");
        holder.qty.setText("Qty:"+String.valueOf(orderobj.getQty()));
        holder.price.setText("Price:"+String.valueOf(orderobj.getPrice()));



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
        TextView prod, wt, qty,price;
        ImageButton edit, del;


        public Viewholder(View itemView) {
            super(itemView);

            prod = (TextView) itemView.findViewById(R.id.tv_eh_prod);
            wt = (TextView) itemView.findViewById(R.id.tv_eh_wt);
            qty = (TextView) itemView.findViewById(R.id.tv_eh_qty);
            price=(TextView)itemView.findViewById(R.id.tv_eh_price);
            edit = (ImageButton) itemView.findViewById(R.id.btn_eh_edit);
           // del = (ImageButton) itemView.findViewById(R.id.btn_eh_del);


            itemView.setOnClickListener(this);
            edit.setOnClickListener(this);
           // del.setOnClickListener(this);






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
