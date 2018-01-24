package com.innovation.neha.tracklocation.Adapters;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.innovation.neha.tracklocation.Pojos.Order;
import com.innovation.neha.tracklocation.R;

import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Neha on 12-12-2017.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.Viewholder>  {

    Context context;
    private LayoutInflater mInflater;
    private List<Order> mData = Collections.emptyList();
    private ItemClickListener mClickListener;

    public RecyclerViewAdapter(Context context, List<Order> data){

        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context=context;

    }


    @Override
    public RecyclerViewAdapter.Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {

       Log.e("Viewholder","Createviewholder");
        View view = mInflater.inflate(R.layout.recyclerview_item_card, parent, false);
        view.setOnClickListener(mClickListener);
        final Viewholder viewholder = new Viewholder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.Viewholder holder, int position) {
        Log.e("Viewholder","bindviewholder");
        final Order orderobj = mData.get(position);
        holder.ord.setText(orderobj.getOrdNo());
        holder.clnt.setText(orderobj.getClntName());
        holder.tot.setText("Tot:"+String.valueOf(orderobj.getTot()));



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
        TextView ord, clnt, tot;
        ImageButton edit, del;
        Button det;


        public Viewholder(View itemView) {
            super(itemView);

            ord = (TextView) itemView.findViewById(R.id.tv_h_ord);
            clnt = (TextView) itemView.findViewById(R.id.tv_h_name);
            tot = (TextView) itemView.findViewById(R.id.tv_h_tot);
            det=(Button)itemView.findViewById(R.id.btn_h_det);
            edit = (ImageButton) itemView.findViewById(R.id.btn_edit);
            del = (ImageButton) itemView.findViewById(R.id.btn_del);


            itemView.setOnClickListener(this);
            det.setOnClickListener(this);
            edit.setOnClickListener(this);
            del.setOnClickListener(this);






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
