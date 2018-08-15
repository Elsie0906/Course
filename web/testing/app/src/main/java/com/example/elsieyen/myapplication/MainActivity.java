package com.example.elsieyen.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;


import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    public static final String TAG = "PlacePhotosActivity";

    private static final String placeId = "ChIJa147K9HX3IAR-lwiGIQv9i4";
    private GeoDataClient geoDataClient;
    private List<PlacePhotoMetadata> photosDataList;
    private static final int  GOOGLE_API_CLIENT_ID  = 0 ;
    private LinkedList<ImageView> imageViewSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        geoDataClient = Places.getGeoDataClient(this, null);

        getPhotoMetadata(placeId);


    }
    private void getPhotoMetadata(String placeId) {

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

                        Log.d(TAG, "number of photos "+photoMetadataBuffer.getCount());

                        LinearLayout list = (LinearLayout) findViewById(R.id.LinearLayout1);


                        for(int i=0; i<photoMetadataBuffer.getCount(); i++)
                        {

                            photosDataList.add(photoMetadataBuffer.get(i).freeze());

                            ImageView image = new ImageView(getApplicationContext());
                            LayoutParams vp = (LayoutParams) new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

                            image.setLayoutParams(vp);
                            image.setMaxWidth(300);
                            image.setMaxHeight(300);
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
