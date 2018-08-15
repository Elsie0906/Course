package com.example.elsieyen.placessearch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;
import android.widget.Toast;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.net.Uri;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;

import java.util.ArrayList;

public class SearchResultActivity extends AppCompatActivity {

    private static final String ACTIVITY_TAG="SearchResultActivity";
    public static final String EXTRA_MESSAGE = "com.example.recyclerView.MESSAGE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        if(savedInstanceState == null){
            listSearchResult(message);
        }
    }
    private void listSearchResult(String message){

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        try{
            //Log.d(ACTIVITY_TAG, message.toString());
            JSONObject response = new JSONObject(message);
            JSONArray results  = response.getJSONArray("results");

            //LinearLayout layout = (LinearLayout) findViewById(R.id.searchResult);

            ArrayList<String> nameDataset = new ArrayList<String>();
            ArrayList<String> iconDataset = new ArrayList<String>();
            ArrayList<String> addressDataset = new ArrayList<String>();
            ArrayList<String> placeIdDataset = new ArrayList<String>();

            if(results.length() == 0){

                noRecordAdapter adapter = new noRecordAdapter(getApplicationContext());
                LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

                //recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);

                RelativeLayout layout = (RelativeLayout) findViewById(R.id.bottomBtn);
                layout.setVisibility(RelativeLayout.GONE);

                return;
            }

            for(int i=0; i<results.length(); i++){
                JSONObject c = results.getJSONObject(i);

                String icon = c.getString("icon");
                String name = c.getString("name");
                String vicinity = c.getString("vicinity");
                String place_id = c.getString("place_id");

                nameDataset.add(name);
                iconDataset.add(icon);
                addressDataset.add(vicinity);
                placeIdDataset.add(place_id);

            }

            recyclerViewAdapter myAdapter = new recyclerViewAdapter(iconDataset,nameDataset,addressDataset,placeIdDataset,getApplicationContext());
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

            //recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(myAdapter);

        }
        catch(final JSONException e){
            String msg = "[Func]requestSearch error: " + e.getMessage();
            Log.e(ACTIVITY_TAG, msg);
            Toast.makeText(SearchResultActivity.this,msg,Toast.LENGTH_LONG).show();
        }
    }
}
