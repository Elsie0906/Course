package com.example.elsieyen.placessearch;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.view.Menu;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;

import android.graphics.Color;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import android.widget.RatingBar;

public class placeDetailActivity extends AppCompatActivity {

    private static final String ACTIVITY_TAG="placeDetailActivity";
    public static final String myScript = "http://cs-server.usc.edu:16651/hw9.php";
    //private RequestQueue queue;

    private TabLayout tabLayout;
    private ViewPager myViewPager;
    private Toolbar toolbar;

    private String PlaceId;
    private Dialog dialog;
    private static String mReview;

    private int[] IconResID = {R.drawable.info_outline,R.drawable.photos,R.drawable.maps, R.drawable.review};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        //Log.d(ACTIVITY_TAG, "onCreate()");

        Intent intent = getIntent();
        String message = intent.getStringExtra(SearchResultActivity.EXTRA_MESSAGE);
        //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        PlaceId = message;

        setToolBar();

        dialog = ProgressDialog.show(placeDetailActivity.this,"", "fetching place detail", true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    dialog.dismiss();
                }
            }
        }).start();

        myViewPager = (ViewPager) findViewById(R.id.myViewPager);
        tabLayout = (TabLayout) findViewById(R.id.TabLayout);
        tabLayout.setBackgroundColor(Color.parseColor("#00bfa5"));
        setViewPager();
        tabLayout.setupWithViewPager(myViewPager);  // connect tab with viewPager; data missing
        setTabLayoutIcon();

    }
    public void setToolBar(){
        toolbar = (Toolbar) findViewById(R.id.ToolBar);
        //toolbar.setTitle("Temp");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setBackgroundColor(Color.parseColor("#00bfa5"));
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.menu_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // navigation back to parent activity
        getSupportActionBar().setHomeButtonEnabled(true);
    }
    public void setTabLayoutIcon(){
        for(int i =0; i < IconResID.length;i++){
            tabLayout.getTabAt(i).setIcon(IconResID[i]);
        }
    }
    public void setViewPager(){
        FragmentList_One myFragment1 = new FragmentList_One(toolbar, PlaceId, getApplicationContext());
        FragmentList_Two myFragment2 = new FragmentList_Two(PlaceId, getApplicationContext());
        FragmentList_Three myFragment3 = new FragmentList_Three();
        FragmentList_Four myFragment4 = new FragmentList_Four(getApplicationContext());
        List<Fragment> fragmentList = new ArrayList<Fragment>();
        fragmentList.add(myFragment1);
        fragmentList.add(myFragment2);
        fragmentList.add(myFragment3);
        fragmentList.add(myFragment4);
        ViewPagerFragmentAdapter myFragmentAdapter = new ViewPagerFragmentAdapter(getSupportFragmentManager(), fragmentList);
        myViewPager.setAdapter(myFragmentAdapter);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public static String getReviewInfo(){
        return mReview;
    }

    public static void setReivewInfo(String review){
        mReview = review;
    }
/*
    private void requestPlaceDetail(String place_id){

        String param = "x=" + place_id;

        String url = myScript + "?" + param;
        //Toast.makeText(MainActivity.this,url,Toast.LENGTH_LONG).show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.v(ACTIVITY_TAG, response.toString());
                        try{
                            JSONObject result = response.getJSONObject("result");

                            String name = getInfo(result, "name");
                            toolbar.setTitle(name);

                            setupInfoTab(result);

                        }
                        catch(final JSONException e){
                            String msg = "[Func]requestPlaceDetail error: " + e.getMessage();
                            Log.e(ACTIVITY_TAG, msg);
                            Toast.makeText(placeDetailActivity.this,msg,Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String msg = "[Func]requestPlaceDetail error: " + error.getMessage();
                        Log.e(ACTIVITY_TAG, msg);
                        Toast.makeText(placeDetailActivity.this,msg,Toast.LENGTH_LONG).show();

                    }
                });

        queue.add(jsonObjectRequest);
    }
    public void setupInfoTab(JSONObject result){
        String blank = "No data";
        String address = getInfo(result, "formatted_address");
        String phone = getInfo(result,"international_phone_number");
        String price = getInfo(result,"price_level");
        String rating = getInfo(result,"rating");
        String googlePage = getInfo(result,"url");
        String website = getInfo(result,"website");

        String msg = "address: " + address + " phone: " + phone + " price: " + price + " rating: " + rating
                                   + " googlePage: " + googlePage + " website: " + website;
          Log.v(ACTIVITY_TAG, msg);
        TextView addr = (TextView) findViewById(R.id.address);
        String input = (address==null)? blank:address;
        addr.setText(input);

        TextView phoneNum = (TextView) findViewById(R.id.phone);
        if(phone == null){
            phoneNum.setText(blank);
        }
        else{
            phoneNum.setText(phone);
            phoneNum.setAutoLinkMask(Linkify.PHONE_NUMBERS);
            phoneNum.setMovementMethod(LinkMovementMethod.getInstance());
        }

        TextView priceLevel = (TextView) findViewById(R.id.price);
        if(price == null){
            priceLevel.setText(blank);
        }
        else{
            String level = reform(price);
            priceLevel.setText(level);
        }

        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        //ratingBar.setNumStars(5);
        ratingBar.setStepSize((float) 0.1);
        ratingBar.setIsIndicator(false);
        if(rating == null){
            ratingBar.setRating(0);
        }
        else{
            float value = Float.parseFloat(rating);
            ratingBar.setRating(value);
        }

        TextView gool = (TextView) findViewById(R.id.url);
        if(googlePage == null){
            gool.setText(blank);
        }
        else{
            gool.setText(googlePage);
            gool.setAutoLinkMask(Linkify.WEB_URLS);
            gool.setMovementMethod(LinkMovementMethod.getInstance());
        }

        TextView web = (TextView) findViewById(R.id.website);
        if(website == null){
            web.setText(blank);
        }
        else{
            web.setText(website);
            web.setAutoLinkMask(Linkify.WEB_URLS);
            web.setMovementMethod(LinkMovementMethod.getInstance());
        }


    }
    public String reform(String level){
        int foo = Integer.parseInt(level);
        String text = "";
        for(int i=0; i<foo; i++){
            text += "$";
        }

        return text;
    }
    public String getInfo(JSONObject result, String search){

        try{
            if(!result.isNull(search)){
                return result.getString(search);
            }
        }
        catch (final JSONException e){
            String msg = "[Func]requestPlaceDetail error: " + e.getMessage();
            Log.e(ACTIVITY_TAG, msg);
            Toast.makeText(placeDetailActivity.this,msg,Toast.LENGTH_LONG).show();
        }

        return null;
    }
*/
}
