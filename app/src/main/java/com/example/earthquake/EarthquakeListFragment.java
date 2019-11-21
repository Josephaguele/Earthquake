package com.example.earthquake;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class EarthquakeListFragment extends Fragment {

    private SwipeRefreshLayout mSwipeToRefreshView;
    protected  EarthquakeViewModel earthquakeViewModel;
    private RecyclerView mRecyclerView;
    private ArrayList<Earthquake> mEarthquakes = new ArrayList<Earthquake>();
    private EarthquakeRecyclerViewAdapter mEarthquakeAdapter = new EarthquakeRecyclerViewAdapter(mEarthquakes);

    public EarthquakeListFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_earthquake_list, container, false);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.list);
        mSwipeToRefreshView = view.findViewById(R.id.swiperefresh);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrieve the Earthquake View Model for the parent Activity.
        earthquakeViewModel = ViewModelProviders.of(getActivity()).get(EarthquakeViewModel.class);

        //Get the data from the View Model, and observe any changes.
        earthquakeViewModel.getEarthquakes().observe(this, new Observer<List<Earthquake>>() {
            @Override
            public void onChanged(List<Earthquake> earthquakes) {
                // When the View Model changes update the list
                if (earthquakes != null){
                    setEarthquakes(earthquakes);
                }
            }
        });

        // Set the Recycler View adapter
        Context context = view.getContext();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.setAdapter(mEarthquakeAdapter);


        // Set up the Swipe to Refresh view
        mSwipeToRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateEarthquakes();
            }
        });
    }


    // An earthquake method that takes a list of Earthquakes, checks for duplicates, and then adds
    // each new Earthquake to the Array list. It should also notify the Recycler View Adapter that
    // a new item has been inserted
    public void setEarthquakes(List<Earthquake>earthquakes){
        for(Earthquake earthquake: earthquakes){
            if(!mEarthquakes.contains(earthquake)){
                mEarthquakes.add(earthquake);
                mEarthquakeAdapter.notifyItemInserted(mEarthquakes.indexOf(earthquake));
            }
        }
        // This method disables the "refreshing" visual indicator when an update has been received.
        // This is because the update itself will be performed by the Earthquake View Model, which
        // will communicate through the parent Activity.
        mSwipeToRefreshView.setRefreshing(false);
    }

    public interface OnListFragmentInteractionListener{
        void onListFragmentRefreshRequested();
    }

    private OnListFragmentInteractionListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (OnListFragmentInteractionListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    protected void updateEarthquakes(){
        if(mListener !=null){
            mListener.onListFragmentRefreshRequested();
        }

    }


}
