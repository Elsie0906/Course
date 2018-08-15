package com.example.elsieyen.placessearch;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.ViewGroup;
import android.view.View;
import android.view.LayoutInflater;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import android.widget.*;
import java.util.List;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class recyclerViewAdapter extends RecyclerView.Adapter <recyclerViewAdapter.ViewHolder>{

    private List<String> nameDataSet, iconDataSet, addDataSet, placeIdDataSet;

    private Context context;

    public recyclerViewAdapter(List<String> icon, List<String> name, List<String> address, List<String> placeId,Context context)
    {
        iconDataSet = icon;
        nameDataSet = name;
        addDataSet = address;
        placeIdDataSet = placeId;
        this.context = context;
    }
    @Override
    public recyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        ViewHolder viewholder = new ViewHolder(view);
        return viewholder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        String text = nameDataSet.get(position) + "\n" + addDataSet.get(position);
        holder.mTextView.setText(text);
        holder.mTextView.setTag(placeIdDataSet.get(position));

        holder.mTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                String place_id = (String) v.getTag();

                //Toast.makeText(context, place_id, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(context, placeDetailActivity.class);
                intent.putExtra(SearchResultActivity.EXTRA_MESSAGE, place_id);
                context.startActivity(intent);

            }
        });
        Glide.with(context).load(iconDataSet.get(position)).apply(new RequestOptions().override(150,150)).into(holder.mImageView);

    }

    @Override
    public int getItemCount()
    {
        return nameDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public ImageView mImageView;
        public ImageButton mImageBtn;
        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView)v.findViewById(R.id.txtItem);
            mImageView = (ImageView)v.findViewById(R.id.imgItem);
            mImageBtn = (ImageButton)v.findViewById(R.id.imageButton);

        }
    }

}
