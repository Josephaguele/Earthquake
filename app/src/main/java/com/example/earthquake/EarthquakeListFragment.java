package com.example.earthquake;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;

public class EarthquakeListFragment extends Fragment {

    private ArrayList<Earthquake> mEarthquakes = new ArrayList<Earthquake>();
    public EarthquakeListFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_earthquake_list, container, false);
        return view;
    }


}
