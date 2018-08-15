package com.example.elsieyen.placessearch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Fragement_Favorite extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Log.d(TAG, "onCreateView()");

        View view = inflater.inflate(R.layout.favoritetab, container, false);


        return view;
    }
}
