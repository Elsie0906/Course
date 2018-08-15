package com.example.elsieyen.placessearch;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FragmentList_One extends Fragment{

    private static final String TAG="FragmentList_One";
    private String place_id;
    public static final String myScript = "http://cs-server.usc.edu:16651/hw9.php";
    private RequestQueue queue;
    private Context context;
    private Toolbar toolbar;

    private String tName;
    private String tAddress;
    private String tWebsite;


    public FragmentList_One(Toolbar toolbar, String place_id, Context context){
        this.place_id = place_id;
        this.context = context;
        this.toolbar = toolbar;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Log.d(TAG, "onCreateView()");

        View view = inflater.inflate(R.layout.infotab, container, false);


        return view;
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Log.d(TAG, "onViewCreated()");

        queue = Volley.newRequestQueue(context);

        requestPlaceDetail(this.place_id);
    }

    @Override

    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        //Log.d(TAG, "onActivityCreated()");

    }

    public void requestPlaceDetail(String place_id){

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

                            tName = name;

                            setupInfoTab(result);
                            setupTwitter();
                            sendReviewInfo(result);

                        }
                        catch(final JSONException e){
                            String msg = "[Func]requestPlaceDetail error: " + e.getMessage();
                            Log.e(TAG, msg);
                            Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String msg = "[Func]requestPlaceDetail error: " + error.getMessage();
                        Log.e(TAG, msg);
                        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();

                    }
                });

        queue.add(jsonObjectRequest);
    }
    public void sendReviewInfo(JSONObject result){

        try{

            //Log.d(TAG, result.toString());

            if(result.isNull("reviews")){
                //Toast.makeText(context,"no reviews",Toast.LENGTH_LONG).show();
                Log.d(TAG, "no reviews");
                return;
            }

            JSONArray reviews  = result.getJSONArray("reviews");

            String text = reviews.toString();

            placeDetailActivity.setReivewInfo(text);
        }
        catch(final JSONException e){
            String msg = "[Error]sendReviewInfo: " + e.getMessage();
            Log.e(TAG, msg);
            Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
        }

    }
    public void setupTwitter(){
        Menu mMenu = toolbar.getMenu();
        MenuItem item = mMenu.findItem(R.id.ItemAccount);

        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String url = "https://twitter.com/intent/tweet?text=Check out "+tName+" located at "+tAddress
                                +" Website: "+"&url="+tWebsite+"&hashtags=TravelAndEntertainmentSearch";

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);

                return true;
            }
        });

    }
    public void setupInfoTab(JSONObject result){
        String blank = "No data";
        String address = getInfo(result, "formatted_address");
        String phone = getInfo(result,"international_phone_number");
        String price = getInfo(result,"price_level");
        String rating = getInfo(result,"rating");
        String googlePage = getInfo(result,"url");
        String website = getInfo(result,"website");

        tAddress = address;
        //tWebsite = (website.equals(""))?googlePage:website;
        if(website == null || website.equals("")){
            tWebsite = googlePage;
        }
        else{
            tWebsite = website;
        }

        /*String msg = "address: " + address + " phone: " + phone + " price: " + price + " rating: " + rating
                                   + " googlePage: " + googlePage + " website: " + website;
          Log.v(ACTIVITY_TAG, msg);*/
        TextView addr = (TextView) getView().findViewById(R.id.address);
        String input = (address==null)? blank:address;
        addr.setText(input);

        TextView phoneNum = (TextView) getView().findViewById(R.id.phone);
        if(phone == null){
            phoneNum.setText(blank);
        }
        else{
            phoneNum.setText(phone);
            phoneNum.setAutoLinkMask(Linkify.PHONE_NUMBERS);
            phoneNum.setMovementMethod(LinkMovementMethod.getInstance());
        }

        TextView priceLevel = (TextView) getView().findViewById(R.id.price);
        if(price == null){
            priceLevel.setText(blank);
        }
        else{
            String level = reform(price);
            priceLevel.setText(level);
        }

        RatingBar ratingBar = (RatingBar) getView().findViewById(R.id.ratingBar);
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

        TextView gool = (TextView) getView().findViewById(R.id.url);
        if(googlePage == null){
            gool.setText(blank);
        }
        else{
            gool.setText(googlePage);
            gool.setAutoLinkMask(Linkify.WEB_URLS);
            gool.setMovementMethod(LinkMovementMethod.getInstance());
        }

        TextView web = (TextView) getView().findViewById(R.id.website);
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
            Log.e(TAG, msg);
            Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
        }

        return null;
    }
}
