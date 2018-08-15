package com.example.elsieyen.placessearch;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import android.location.*;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.Manifest;

import android.content.Intent;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.android.volley.Response;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import java.util.ArrayList;
import java.util.List;

import android.view.View.OnClickListener;



import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.ConnectionResult;


import android.util.Log;

public class MainActivity extends AppCompatActivity implements GoogleApiClient . OnConnectionFailedListener{

    private static final int PERMISSION_REQUEST_GPS = 0;
    private static final String ACTIVITY_TAG="MainActivity";
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    public static final String myScript = "http://cs-server.usc.edu:16651/hw9.php";

    private android.support.design.widget.TabLayout mTabs;
    private RequestQueue queue;

    private double latitude = 0;    // current location
    private double longitude = 0;
    private String latFromQuery = "";   // geoLocation
    private String lonFromQuery = "";

    private TabLayout mTablayout;
    private ViewPager mViewPager;
    private String mcategory;
    private int[] tabIcons = {R.drawable.search, R.drawable.heart_fill_white};
    private List<String> titles;

    protected GoogleApiClient  mGoogleApiClient ;
    private AutoCompleteTextView  mAutocompleteView ;
    private PlaceAutocompleteAdapter  mAdapter ;
    private static final int  GOOGLE_API_CLIENT_ID  = 0 ;
    private static final LatLngBounds  BOUNDS_GREATER_US  = new LatLngBounds (
            new LatLng (32.6393 , -122.14351988 ), new LatLng (44.901184 , -67.32254 ));

    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID , this)
                .addApi(Places.GEO_DATA_API)
                .build();

        mAdapter  = new PlaceAutocompleteAdapter ( this ,  android . R . layout . simple_list_item_1 ,
                mGoogleApiClient ,  BOUNDS_GREATER_US , null );

        initLayout();
        getCurrentLocation();
        queue = Volley.newRequestQueue(this);

    }

    @Override
    public void  onConnectionFailed ( ConnectionResult  connectionResult ) {

        Log.e( ACTIVITY_TAG , "onConnectionFailed: ConnectionResult.getErrorCode() = "
                +  connectionResult . getErrorCode ());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast . makeText ( this ,
                "Could not connect to Google API Client: Error " +  connectionResult . getErrorCode (),
                Toast . LENGTH_SHORT ). show ();
        MainActivity . this . finish ();
    }
    public void clearForm(View view){
        TextInputLayout textInputLayout1 = (TextInputLayout)findViewById(R.id.textInputLayout1);
        EditText keyword = (EditText)findViewById(R.id.keyword);
        keyword.setText("");
        textInputLayout1.setError(null);

        Spinner spinner = (Spinner) findViewById(R.id.category);
        spinner.setSelection(0);

        EditText distance = (EditText)findViewById(R.id.distance);
        distance.setText("");


        RadioButton rb1 = (RadioButton) findViewById(R.id.here);
        RadioButton rb2 = (RadioButton) findViewById(R.id.other);

        rb1.setChecked(true);
        rb2.setChecked(false);


        TextInputLayout textInputLayout2 = (TextInputLayout)findViewById(R.id.textInputLayout2);
        AutoCompleteTextView location = (AutoCompleteTextView)findViewById(R.id.location);
        location.setText("");
        location.setEnabled(false);
        textInputLayout2.setError(null);
    }
    public void submitForm(View view){

        //Toast.makeText(MainActivity.this,"submitForm()",Toast.LENGTH_LONG).show();
        //Log.v(ACTIVITY_TAG, "submitForm()");

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio);
        int selectedId = radioGroup.getCheckedRadioButtonId();

        //validation check
        TextInputLayout textInputLayout1 = (TextInputLayout)findViewById(R.id.textInputLayout1);
        EditText keyword = (EditText)findViewById(R.id.keyword);
        String sKeyword = keyword.getText().toString().trim();

        TextInputLayout textInputLayout2 = (TextInputLayout)findViewById(R.id.textInputLayout2);
        AutoCompleteTextView location = (AutoCompleteTextView)findViewById(R.id.location);
        String sLocation = location.getText().toString().trim();

        if(selectedId == R.id.here && sKeyword.matches("")){
            textInputLayout1.setError("Please enter maindatory field");
            Toast.makeText(MainActivity.this,"Please fix all fields with errors",Toast.LENGTH_LONG).show();
            return;
        }
        else if(selectedId == R.id.other){
            if(sKeyword.matches(""))textInputLayout1.setError("Please enter maindatory field");
            if(sLocation.matches(""))textInputLayout2.setError("Please enter maindatory field");

            if(sKeyword.matches("") || sLocation.matches("")){
                Toast.makeText(MainActivity.this,"Please fix all fields with errors",Toast.LENGTH_LONG).show();
                return;
            }
        }

        dialog = ProgressDialog.show(MainActivity.this,"", "fetching results", true);
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

        if(selectedId == R.id.here){
            String lat = String.valueOf(latitude);
            String lon = String.valueOf(longitude);
            requestSearch(lat, lon);
        }
        else{   // http request

            getGeoCoding();
        }
    }
    private void triggerActivity(String msg){
        Intent intent = new Intent(this, SearchResultActivity.class);
        intent.putExtra(EXTRA_MESSAGE, msg);
        startActivity(intent);
    }
    private void requestSearch(String lat, String lng){

        EditText key = (EditText)findViewById(R.id.keyword);
        EditText dist = (EditText)findViewById(R.id.distance);


        String category = "category=" + mcategory;
        String location = "loc=" + lat + "," + lng;
        String keyword = "keyword=" + key.getText().toString();;
        String miles = dist.getText().toString(); // mile to meter, need to be changed, 1 mile = 1609 meter
        //int unit = (miles == "" || miles == null)?10:Integer.parseInt(miles);
        int unit = 10;
        if(miles != null && !miles.equals("")){
            unit = Integer.parseInt(miles);
        }
        unit *= 1609;
        unit = (unit>50000)?50000:unit;
        String meters = Integer.toString(unit);
        String distance = "dist=" + meters;


        /*
        String category = "category=" + "Cafe";
        String location = "loc=" + lat + "," + lng;
        String keyword = "keyword=" + "usc";
        String distance = "dist=" + "16090";
        */

        String url = myScript + "?" + location + "&" + distance + "&" + category + "&" + keyword;
        //Toast.makeText(MainActivity.this,url,Toast.LENGTH_LONG).show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d(ACTIVITY_TAG, response.toString());
                        triggerActivity(response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String msg = "[Func]requestSearch error: " + error.getMessage();
                        Log.e(ACTIVITY_TAG, msg);
                        Toast.makeText(MainActivity.this,msg,Toast.LENGTH_LONG).show();

                    }
                });

        queue.add(jsonObjectRequest);


    }
    private void getGeoCoding(){

        AutoCompleteTextView source = (AutoCompleteTextView) findViewById(R.id.location);
        String Source = source.getText().toString();

        //String url = myScript + "?geoLocation=" + "University+of+Southern+California";
        String url = myScript + "?geoLocation=" + Source;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(ACTIVITY_TAG, response.toString());
                        try{
                            JSONArray temp = response.getJSONArray("results");

                            if(temp.length() == 0){
                                Toast.makeText(MainActivity.this,"[Error] Cannot find geolocation",Toast.LENGTH_LONG).show();
                                return;
                            }

                            JSONObject results  = temp.getJSONObject(0).getJSONObject("geometry").getJSONObject("location");

                            //Log.v(ACTIVITY_TAG, results.toString());
                            latFromQuery = new String(results.getString("lat"));
                            lonFromQuery = new String(results.getString("lng"));

                            requestSearch(latFromQuery, lonFromQuery);

                            //String msg = "lat: " + latFromQuery + ", lng: " + lonFromQuery;
                            //Toast.makeText(MainActivity.this,msg,Toast.LENGTH_LONG).show();
                        }
                        catch(final JSONException e){
                            String msg = "[Func]getGeoCoding error: " + e.getMessage();
                            Log.e(ACTIVITY_TAG, msg);
                            Toast.makeText(MainActivity.this,msg,Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.e("[Func]getGeoCoding: onErrorResponse ", error.toString());

                    }
                });

        queue.add(jsonObjectRequest);
    }
    private void initLayout(){
        Fragment_Search fragment1 = new Fragment_Search(getApplicationContext());
        Fragement_Favorite fragment2 = new Fragement_Favorite();

        //setupSpinner(page1);

        ArrayList<Fragment> pageList = new ArrayList<>();
        pageList.add(fragment1);
        pageList.add(fragment2);

        initTab();

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(new SamplePagerFragmentAdapter(getSupportFragmentManager(), pageList));
        initListener();

    }
    private void initTab(){
        mTablayout = (TabLayout) findViewById(R.id.tabs);
        mTablayout.addTab(mTablayout.newTab());
        mTablayout.addTab(mTablayout.newTab());
        mTablayout.getTabAt(0).setCustomView(R.layout.custom_tab_title);
        mTablayout.getTabAt(1).setCustomView(R.layout.custom_tab_title);
        View tab1_view = mTablayout.getTabAt(0).getCustomView();
        View tab2_view = mTablayout.getTabAt(1).getCustomView();
        TextView tab1_title = (TextView) tab1_view.findViewById(R.id.textView);
        ImageView img1 = (ImageView) tab1_view.findViewById(R.id.imgItem);
        TextView tab2_title = (TextView) tab2_view.findViewById(R.id.textView);
        ImageView img2 = (ImageView) tab2_view.findViewById(R.id.imgItem);
        tab1_title.setText("SEARCH");
        img1.setImageResource(R.drawable.search);
        tab2_title.setText("FAVORITES");
        img2.setImageResource(R.drawable.heart_fill_white);
        mTablayout.setBackgroundColor(Color.parseColor("#00bfa5"));
    }
    private void initListener() {
        mTablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTablayout));
    }

    private void setupSpinner(){
        Spinner spinner = (Spinner) findViewById(R.id.category);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                mcategory = new String(item);
                //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                mcategory = "Default";
            }
        });
    }

    private void getCurrentLocation(){

        //Log.v(ACTIVITY_TAG, "getCurrentLocation");

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                String msg = "Need permission to get current location";
                Toast.makeText(MainActivity.this,msg,Toast.LENGTH_LONG).show();

            } else {

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQUEST_GPS);

            }
        }
        else{
            Log.v(ACTIVITY_TAG, "Permission has already been granted");
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {

        //Log.v(ACTIVITY_TAG, "onRequestPermissionsResult");

        if (requestCode == PERMISSION_REQUEST_GPS
                && grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //  gps functionality

            LocationManager locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);

            LocationListener locationListener = new LocationListener() {

                public void onLocationChanged(Location location) {  //onCreate(); Activity doesn't have onCreateView()
                    // 1. get current location
                    latitude=location.getLatitude();
                    longitude=location.getLongitude();

                    // 2. spinner listener
                    setupSpinner();

                    // 3. turn on submit button
                    Button search = (Button) findViewById(R.id.submitBtn);
                    search.setEnabled(true);
/*
                    search.setOnClickListener(new OnClickListener() {
                        public void onClick(View v)
                        {
                            submitForm(v);
                        }
                    });
*/
                    // 5. autocomplete

                    mAutocompleteView  = ( AutoCompleteTextView )findViewById ( R.id.location );
                    mAutocompleteView . setAdapter ( mAdapter );

                }

                public void onStatusChanged(String provider, int status, Bundle extras) {}

                public void onProviderEnabled(String provider) {}

                public void onProviderDisabled(String provider) {}
            };

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 5, locationListener);
        }
    }
}
