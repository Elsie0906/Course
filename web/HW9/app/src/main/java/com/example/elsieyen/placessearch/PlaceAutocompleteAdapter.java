package com.example.elsieyen.placessearch;

import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;

import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.Filter;
import android.widget.Toast;

import android.content.Context;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.util.Log;

public class PlaceAutocompleteAdapter extends ArrayAdapter < PlaceAutocompleteAdapter . PlaceAutocomplete > implements Filterable {

    private static final String  TAG  = "PlaceAutocompleteAdapter" ;
    private ArrayList < PlaceAutocomplete >  mResultList ;
    private GoogleApiClient  mGoogleApiClient ;
    private LatLngBounds  mBounds ;
    private AutocompleteFilter  mPlaceFilter ;

    public PlaceAutocompleteAdapter ( Context  context , int  resource , GoogleApiClient  googleApiClient ,
                                      LatLngBounds  bounds , AutocompleteFilter  filter ) {
        super ( context ,  resource );
        mGoogleApiClient  =  googleApiClient ;
        mBounds  =  bounds ;
        mPlaceFilter  =  filter ;
    }

    public void  setBounds ( LatLngBounds  bounds ) {
        mBounds  =  bounds ;
    }

    @Override
    public int  getCount () {
        return  mResultList . size ();
    }

    @Override
    public PlaceAutocomplete  getItem ( int  position ) {
        return  mResultList . get ( position );
    }

    @Override
    public Filter  getFilter () {
        Filter  filter  = new Filter () {
            @Override
            protected FilterResults  performFiltering ( CharSequence  constraint ) {
                FilterResults  results  = new FilterResults ();
                // Skip the autocomplete query if no constraints are given.
                if ( constraint  != null ) {
                    // Query the autocomplete API for the (constraint) search string.
                    mResultList  =  getAutocomplete ( constraint );
                    if ( mResultList  != null ) {
                        // The API successfully returned results.
                        results . values  =  mResultList ;
                        results . count  =  mResultList . size ();
                    }
                }
                return  results ;
            }

            @Override
            protected void  publishResults ( CharSequence  constraint , FilterResults  results ) {
                if ( results  != null &&  results . count  > 0 ) {
                    // The API returned at least one result, update the data.
                    notifyDataSetChanged ();
                } else {
                    // The API did not return any results, invalidate the data set.
                    notifyDataSetInvalidated ();
                }
            }
        };
        return  filter ;
    }

    private ArrayList < PlaceAutocomplete >  getAutocomplete ( CharSequence  constraint ) {
        if ( mGoogleApiClient . isConnected ()) {
            Log . i ( TAG , "Starting autocomplete query for: " +  constraint );

            // Submit the query to the autocomplete API and retrieve a PendingResult that will
            // contain the results when the query completes.
            PendingResult < AutocompletePredictionBuffer >  results  =
                    Places . GeoDataApi
                            . getAutocompletePredictions ( mGoogleApiClient ,  constraint . toString (),
                                    mBounds ,  mPlaceFilter );

            // This method should have been called off the main UI thread. Block and wait for at most 60s
            // for a result from the API.
            AutocompletePredictionBuffer  autocompletePredictions  =  results
                    . await ( 60 , TimeUnit . SECONDS );

            // Confirm that the query completed successfully, otherwise return null
            final Status  status  =  autocompletePredictions . getStatus ();
            if (! status . isSuccess ()) {
                Toast . makeText ( getContext (), "Error contacting API: " +  status . toString (),
                        Toast . LENGTH_SHORT ). show ();
                Log . e ( TAG , "Error getting autocomplete prediction API call: " +  status . toString ());
                autocompletePredictions . release ();
                return null ;
            }

            Log . i ( TAG , "Query completed. Received " +  autocompletePredictions . getCount ()
                    + " predictions." );

            // Copy the results into our own data structure, because we can't hold onto the buffer.
            // AutocompletePrediction objects encapsulate the API response (place ID and description).

            Iterator< AutocompletePrediction > iterator  =  autocompletePredictions . iterator ();
            ArrayList  resultList  = new ArrayList <>( autocompletePredictions . getCount ());
            while ( iterator . hasNext ()) {
                AutocompletePrediction  prediction  =  iterator . next ();
                // Get the details of this prediction and copy it into a new PlaceAutocomplete object.
                resultList . add ( new PlaceAutocomplete ( prediction . getFullText (null)));
            }

            // Release the buffer now that all data has been copied.
            autocompletePredictions . release ();

            return  resultList ;
        }
        Log . e ( TAG , "Google API client is not connected for autocomplete query." );
        return null ;
    }
    class PlaceAutocomplete {

        public CharSequence  fullText ;

        PlaceAutocomplete ( CharSequence  fullText) {
            this . fullText  =  fullText ;
        }

        @Override
        public String  toString () {
            return  fullText . toString ();
        }
    }
}
