package com.example.iastudybuddy;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FocusFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FocusFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private Button goToAddSubjectActivityButton;

    private Chronometer chronometer;
    //time difference from when user starts chronometer from when they paused it
    private long pauseOffset;
    private boolean running;

    private Button chrStartButton;
    private Button chrPauseButton;
    private Button chrResetButton;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FocusFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FocusFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FocusFragment newInstance(String param1, String param2) {
        FocusFragment fragment = new FocusFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_focus, container, false);

        //hour and colon are automatically added when one hour is reached
        chronometer = v.findViewById(R.id.chronometer);
        //set as Time: (chronometer) when activity starts
//        chronometer.setFormat("Time: %s");
//        chronometer.setBase(SystemClock.elapsedRealtime());

        chrStartButton = v.findViewById(R.id.chrStartButton);
        chrStartButton.setOnClickListener(view ->
        {
            //if not running, be able to start chronometer
            if(!running)
            {
                //sets base pause offset (eg. 5 seconds) into the past -> chronometer starts earlier than the current time
                chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                //start running chronometer at the time user clicks "pause"
                chronometer.start();
                running = true;
            }
        });

        chrPauseButton = v.findViewById(R.id.chrPauseButton);
        chrPauseButton.setOnClickListener(view ->
        {
            if(running)
            {
                chronometer.stop();
                //current elapsed real time - elapsed real time when user clicked start
                //getBase() returns base time when user started chronometer
                //time that has passed since user started chronometer IN MILLISECONDS
                pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
                running = false;
            }
        });

        chrResetButton = v.findViewById(R.id.chrResetButton);
        chrResetButton.setOnClickListener(view ->
        {
            //reset back to 0
            chronometer.setBase(SystemClock.elapsedRealtime());
            //don't want any time to be added to chronometer when user clicks start
            pauseOffset = 0;
        });

        goToAddSubjectActivityButton = v.findViewById(R.id.goToAddSubjectButton);
        goToAddSubjectActivityButton.setOnClickListener(view -> startActivity(new Intent(getActivity(), AddSubjectActivity.class)));

        return v;
    }
}