package com.innovation.neha.tracklocation.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.innovation.neha.tracklocation.Pojos.Customer;
import com.innovation.neha.tracklocation.Pojos.SubOrder;
import com.innovation.neha.tracklocation.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by Neha on 13-01-2018.
 */

public class RecyclerViewCustAdapter extends RecyclerView.Adapter<RecyclerViewCustAdapter.Viewholder>  {

    Context context;
    private LayoutInflater mInflater;
    private List<Customer> mData = Collections.emptyList();
    private ItemClickListener mClickListener;

    public RecyclerViewCustAdapter(Context context, List<Customer> data){

        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context=context;

    }


    @Override
    public RecyclerViewCustAdapter.Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {

       Log.e("Viewholder","Createviewholder");
        View view = mInflater.inflate(R.layout.recyclerview_cust_item_card, parent, false);
        view.setOnClickListener(mClickListener);
        final Viewholder viewholder = new Viewholder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewCustAdapter.Viewholder holder, int position) {
        Log.e("Viewholder","bindviewholder");
        final Customer custobj = mData.get(position);
        holder.name.setText(custobj.getCustName());
        holder.date.setText(custobj.getCustDate());

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
        TextView name, date;
        ImageButton edit;


        public Viewholder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.tv_ch_name);
           date = (TextView) itemView.findViewById(R.id.tv_ch_date);

           edit = (ImageButton) itemView.findViewById(R.id.btn_ch_edit);



            itemView.setOnClickListener(this);
            edit.setOnClickListener(this);







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
