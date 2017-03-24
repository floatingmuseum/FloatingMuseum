package com.floatingmuseum.androidtest.functions.nsd;

import android.net.nsd.NsdServiceInfo;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.floatingmuseum.androidtest.R;

import java.util.ArrayList;

/**
 * Created by Floatingmuseum on 2017/3/24.
 */

public class Adapter extends RecyclerView.Adapter<Adapter.Holder> {
    private ArrayList<NsdServiceInfo> datas;
    private OnItemClickListener onItemClickListener;

    public Adapter(ArrayList<NsdServiceInfo> datas) {
        this.datas = datas;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item, null));
    }

    @Override
    public void onBindViewHolder(Holder holder, final int position) {
        holder.tv.setText(datas.get(position).getServiceName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener!= null){
                    onItemClickListener.onClick(datas.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public void add(NsdServiceInfo nsdServiceInfo) {
        datas.add(nsdServiceInfo);
        notifyItemInserted(datas.size());
    }


    class Holder extends RecyclerView.ViewHolder {
        TextView tv;
        public Holder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.tv_name);
        }
    }

    interface OnItemClickListener{
        void onClick(NsdServiceInfo nsdServiceInfo);
    }


}
