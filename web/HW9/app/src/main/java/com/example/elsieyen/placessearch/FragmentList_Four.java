package com.example.elsieyen.placessearch;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentList_Four extends Fragment{

    private static final String TAG="FragmentList_Four";

    private Context context;

    public FragmentList_Four(Context context){
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reviewtab, container, false);

        return view;
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String text = placeDetailActivity.getReviewInfo();

        if(text != null){
            setupReview(text);
        }
        else{
            RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);

            noRecordAdapter adapter = new noRecordAdapter(context);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
        }

    }

    public void setupReview(String text){

        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);

        ArrayList<String> nameDataset = new ArrayList<String>();
        ArrayList<String> iconDataset = new ArrayList<String>();
        ArrayList<String> dateDataset = new ArrayList<String>();
        ArrayList<String> ratingDataset = new ArrayList<String>();
        ArrayList<String> commentDataset = new ArrayList<String>();

        try{
            JSONArray data = new JSONArray(text);

            if(data.length() == 0 || data == null) return;

            int len = (data.length()>5)?5:data.length();

            for(int i=0; i<len; i++){

                JSONObject c = data.getJSONObject(i);

                String icon = c.getString("profile_photo_url");
                String name = c.getString("author_name");
                String time = c.getString("time");
                String comment = c.getString("text");
                String rating = c.getString("rating");

                nameDataset.add(name);
                iconDataset.add(icon);
                dateDataset.add(time);
                ratingDataset.add(rating);
                commentDataset.add(comment);
            }

            reviewAdapter myAdapter = new reviewAdapter(nameDataset, iconDataset, dateDataset, ratingDataset,commentDataset ,context);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(myAdapter);
        }
        catch(final JSONException e){
            String msg = "[Func]setupReview error: " + e.getMessage();
            Log.e(TAG, msg);
            Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
        }
    }
}
