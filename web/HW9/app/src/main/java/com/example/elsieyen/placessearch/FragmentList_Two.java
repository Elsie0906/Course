package com.example.elsieyen.placessearch;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FragmentList_Two extends Fragment{

    private String place_id;
    private Context context;

    private static final String TAG="FragmentList_Two";

    private GeoDataClient geoDataClient;
    private List<PlacePhotoMetadata> photosDataList;
    private LinkedList<ImageView> imageViewSet;
    private static final int mWidth = 500;
    private static final int mHeight = 500;

    public FragmentList_Two(String place_id, Context context){
        this.place_id = place_id;
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.phototab, container, false);

        return view;
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Log.d(TAG, "onViewCreated()");
        geoDataClient = Places.getGeoDataClient(context, null);
        getPhotoMetadata(place_id);

    }
    public void getPhotoMetadata(String placeId) {

        final Task<PlacePhotoMetadataResponse> photoResponse =
                geoDataClient.getPlacePhotos(placeId);

        photoResponse.addOnCompleteListener
                (new OnCompleteListener<PlacePhotoMetadataResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {

                        photosDataList = new ArrayList<>();
                        imageViewSet = new LinkedList<>();
                        PlacePhotoMetadataResponse photos = task.getResult();
                        PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();

                        //Log.d(TAG, "number of photos "+photoMetadataBuffer.getCount());

                        LinearLayout list = (LinearLayout) getView().findViewById(R.id.photoTab);

                        if(photoMetadataBuffer.getCount() == 0){
                            TextView dummy = new TextView(context);
                            LayoutParams vp = (LayoutParams) new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                            dummy.setText("No data");
                            list.addView(dummy);
                        }


                        for(int i=0; i<photoMetadataBuffer.getCount(); i++)
                        {

                            photosDataList.add(photoMetadataBuffer.get(i).freeze());

                            ImageView image = new ImageView(context);
                            LayoutParams vp = (LayoutParams) new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                            //LayoutParams vp = (LayoutParams) new LayoutParams(mWidth, mHeight);

                            image.setLayoutParams(vp);
                            image.setMaxWidth(mWidth);
                            image.setMaxHeight(mHeight);
                            imageViewSet.add(image);

                            list.addView(image,vp);
                            displayPhoto(i);

                        }

                        photoMetadataBuffer.release();

                    }
                });
    }
    private void displayPhoto(int index){
        //Log.d(TAG, "index "+currentPhotoIndex);
        //Log.d(TAG, "size "+photosDataList.size());
        if(photosDataList.isEmpty()){
            return;
        }
        getPhoto(photosDataList.get(index));
    }

    private void getPhoto(PlacePhotoMetadata photoMetadata){

        Task<PlacePhotoResponse> photoResponse = geoDataClient.getPhoto(photoMetadata);
        photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                PlacePhotoResponse photo = task.getResult();
                Bitmap photoBitmap = photo.getBitmap();
                //Log.d(TAG, "photo "+photo.toString());

                if(imageViewSet.size() == 0)
                    return;

                ImageView placeImage = (ImageView) imageViewSet.remove(0);
                //Log.d(TAG, "imageView "+placeImage.toString());
                placeImage.invalidate();
                placeImage.setImageBitmap(photoBitmap);
            }
        });
    }


}
