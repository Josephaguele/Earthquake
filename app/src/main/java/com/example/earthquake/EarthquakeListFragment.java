package com.example.earthquake;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

    protected EarthquakeViewModel earthquakeViewModel;
    private SwipeRefreshLayout mSwipeToRefreshView;
    private RecyclerView mRecyclerView;
    private ArrayList<Earthquake> mEarthquakes = new ArrayList<Earthquake>();
    private EarthquakeRecyclerViewAdapter mEarthquakeAdapter = new EarthquakeRecyclerViewAdapter(mEarthquakes);
    private OnListFragmentInteractionListener mListener;
    // This method reads the Shared Preference minimum magnitude value
    private int mMinMagnitude = 0;

    public EarthquakeListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_earthquake_list, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
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
                if (earthquakes != null) {
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
    public void setEarthquakes(List<Earthquake> earthquakes) {
        updateFromPreferences();

        for (Earthquake earthquake : earthquakes) {
            if (earthquake.getMagnitude() >= mMinMagnitude) {
                if (!mEarthquakes.contains(earthquake)) {
                    mEarthquakes.add(earthquake);
                    mEarthquakeAdapter.notifyItemInserted(mEarthquakes.indexOf(earthquake));
                }
            }
        }

        // if the earthquake list derived has a value that is greater than what the user has keyed in in the preferences,
        // remove it.
        if (mEarthquakes != null && mEarthquakes.size() > 0)
            for (int i = mEarthquakes.size() - 1; i >= 0; i--) {
                if (mEarthquakes.get(i).getMagnitude() < mMinMagnitude) {
                    mEarthquakes.remove(i);
                    mEarthquakeAdapter.notifyItemRemoved(i);
                }
            }
        // This method disables the "refreshing" visual indicator when an update has been received.
        // This is because the update itself will be performed by the Earthquake View Model, which
        // will communicate through the parent Activity.
        mSwipeToRefreshView.setRefreshing(false);

        // Apply the magnitude filter by updating the setEarthquakes method from the EarthquakeListFragment
        // to update the minimum magnitude preference, and check each earthquake's magnitude before
        // adding it to the list:

    }

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

    protected void updateEarthquakes() {
        if (mListener != null) {
            mListener.onListFragmentRefreshRequested();
        }
    }

    private void updateFromPreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        mMinMagnitude = Integer.parseInt( // CONVERT THE VALUE GOTTEN FROM THE USER CLICK TO AN INTEGER
                prefs.getString(PreferencesActivity.PREF_MIN_MAG, "3"));
    }

    // This step is to create a new OnSharedPreferenceChangeListener within the EarthquakeListFragment
    // that will repopulate the Earthquake list, applying the magnitude filter based on the new
    // setting
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Register an OnSharedPreferenceChangeListener
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefs.registerOnSharedPreferenceChangeListener(mPrefListener);
    }

    
    private SharedPreferences.OnSharedPreferenceChangeListener mPrefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if(PreferencesActivity.PREF_MIN_MAG.equals(key)){
                List<Earthquake>earthquakes = earthquakeViewModel.getEarthquakes().getValue();
                if(earthquakes !=null){
                    setEarthquakes(earthquakes);
                }
            }
        }
    };

    public interface OnListFragmentInteractionListener {
        void onListFragmentRefreshRequested();
    }


}
