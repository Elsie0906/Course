package com.example.elsieyen.placessearch;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class noRecordAdapter extends RecyclerView.Adapter <recyclerViewAdapter.ViewHolder>{

    private Context context;

    public noRecordAdapter(Context context)
    {
        this.context = context;
    }

    @Override
    public recyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_norecord, parent, false);
        recyclerViewAdapter.ViewHolder viewholder = new recyclerViewAdapter.ViewHolder(view);
        return viewholder;
    }

    public int getItemCount()
    {
        return 1;
    }

    @Override
    public void onBindViewHolder(recyclerViewAdapter.ViewHolder holder, int position)
    {
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView)v.findViewById(R.id.txtItem);
        }
    }
}
