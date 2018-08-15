package com.example.elsieyen.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class photoAdapter extends RecyclerView.Adapter <photoAdapter.ViewHolder>{

    private List<PlacePhotoMetadata> photoDataSet;

    private Context context;

    public static final String TAG = "photoAdapter";
    private GeoDataClient geoDataClient;
    private ViewHolder holderTest;
    private ImageView placeImage;

    public photoAdapter(List<PlacePhotoMetadata> photoMetadata,Context context, GeoDataClient geoDataClient)
    {
        photoDataSet = photoMetadata;
        this.context = context;
        this.geoDataClient = geoDataClient;
    }
    @Override
    public photoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        ViewHolder viewholder = new ViewHolder(view);
        //Log.d(TAG, "onCreateViewHolder "+ viewholder.toString());
        return viewholder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {

        Log.d(TAG, "index "+position);

        //Log.d(TAG, "size "+photoDataSet.size());

        holderTest = holder;

        //Log.d(TAG, "onBindViewHolder "+ holderTest.toString());

        if(photoDataSet.isEmpty()){
            return;
        }

        PlacePhotoMetadata photoMetadata = photoDataSet.get(position);

        Task<PlacePhotoResponse> photoResponse = geoDataClient.getPhoto(photoMetadata);
        photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                PlacePhotoResponse photo = task.getResult();
                Bitmap photoBitmap = photo.getBitmap();
                //Log.d(TAG, "photo "+photo.toString());

                holderTest.mImageView.invalidate();
                holderTest.mImageView.setImageBitmap(photoBitmap);
            }
        });
    }
    @Override
    public int getItemCount()
    {
        return photoDataSet.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        public ImageView mImageView;

        public ViewHolder(View v) {
            super(v);

            mImageView = (ImageView)v.findViewById(R.id.imgItem);
            LinearLayout.LayoutParams vp = (LinearLayout.LayoutParams) new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            mImageView.setMaxHeight(200);
            mImageView.setMaxWidth(200);

        }
    }
}
