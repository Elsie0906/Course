package com.example.elsieyen.placessearch;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;

public class reviewAdapter extends RecyclerView.Adapter <reviewAdapter.ViewHolder>{

    private List<String> nameDataSet, iconDataSet, dateDataSet, ratingDataSet, commentDataSet;

    private static final String TAG="reviewAdapter";

    private Context context;

    public reviewAdapter(List<String> name, List<String> icon, List<String> date, List<String> rating, List<String> comment,Context context)
    {
        iconDataSet = icon;
        nameDataSet = name;
        dateDataSet = date;
        ratingDataSet = rating;
        commentDataSet = comment;
        this.context = context;
    }

    @Override
    public reviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        reviewAdapter.ViewHolder viewholder = new reviewAdapter.ViewHolder(view);
        return viewholder;
    }
    @Override
    public void onBindViewHolder(reviewAdapter.ViewHolder holder, int position)
    {
        holder.mTextView.setText(nameDataSet.get(position));

        String timestamp = dateDataSet.get(position);

        String time = TimeChange(timestamp);

        holder.dTextView.setText(time);
        holder.cTextView.setText(commentDataSet.get(position));

        Glide.with(context).load(iconDataSet.get(position)).apply(new RequestOptions().override(150,150)).into(holder.imgView);

        RatingBar ratingBar = holder.mRating;
        ratingBar.setStepSize((float) 0.1);
        ratingBar.setIsIndicator(false);
        String rating = ratingDataSet.get(position);

        if(rating == null){
            ratingBar.setRating(0);
        }
        else{
            float value = Float.parseFloat(rating);
            ratingBar.setRating(value);
        }
    }

    public String TimeChange(String timestamp){

        String dummy = "No data";

        if(timestamp == null)
            return dummy;

        SimpleDateFormat format = new SimpleDateFormat(  "yyyy-MM-dd HH:mm:ss"  );

        long offset = 1000 * Long.parseLong(timestamp);

        String d = format.format(offset);
        //Date realDate=format.parse(d);
        //Log.d(TAG, "offset: " + timestamp);
        //Log.d(TAG, "Format To Date:" +realDate);

        return d;
    }

    @Override
    public int getItemCount()
    {
        return nameDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public ImageView imgView;
        public TextView dTextView;
        public RatingBar mRating;
        public TextView cTextView;
        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView)v.findViewById(R.id.name);
            imgView = (ImageView)v.findViewById(R.id.icon);
            dTextView = (TextView)v.findViewById(R.id.date);
            mRating = (RatingBar) v.findViewById(R.id.ratingBar);
            cTextView = (TextView)v.findViewById(R.id.comment);
        }
    }

}
