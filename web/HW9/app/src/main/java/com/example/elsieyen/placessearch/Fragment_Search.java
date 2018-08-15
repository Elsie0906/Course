package com.example.elsieyen.placessearch;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.view.View.OnClickListener;

import com.android.volley.toolbox.Volley;

public class Fragment_Search extends Fragment {

    private Context context;
    private static final String TAG="Fragment_Search";

    public Fragment_Search(Context context){
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Log.d(TAG, "onCreateView()");

        View view = inflater.inflate(R.layout.searchtab, container, false);


        return view;
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRadioButton();
        setupSpinner();
    }
    public void setupSpinner(){
        Spinner spinner = (Spinner) getView().findViewById(R.id.category);

        String[] category = getResources().getStringArray(R.array.category_array);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, category);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }
    private void setupRadioButton() {

        RadioButton rb1 = (RadioButton) getView().findViewById(R.id.here);

        rb1.setOnClickListener(radio_btn_listener);

        RadioButton rb2 = (RadioButton) getView().findViewById(R.id.other);

        rb2.setOnClickListener(radio_btn_listener);
    }
    private OnClickListener radio_btn_listener = new OnClickListener(){
        public void onClick(View v) {
            RadioGroup radioGroup = (RadioGroup) getView().findViewById(R.id.radio);

            int selectedId = radioGroup.getCheckedRadioButtonId();

            AutoCompleteTextView loc = (AutoCompleteTextView) getView().findViewById(R.id.location);

            if(selectedId == R.id.here){
                loc.setEnabled(false);
            }
            else{
                loc.setEnabled(true);
            }
        }
    };

}
