package com.example.iastudybuddy;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * This class contains a stopwatch where the user can measure the amount of time they spend focusing.
 * This class also navigates to AddSubjectActivity, where the user can create a new subject.
 *
 * @author Shirley Deng
 * @version 0.1
 */
public class FocusFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    private Button goToAddSubjectActivityButton;

    private Chronometer chronometer;
    //time difference from when user starts chronometer from when they paused it
    private long pauseOffset;
    private boolean running;
    private Button chrStartButton;
    private Button chrPauseButton;

    public FocusFragment() {
        // Required empty public constructor
    }

    public static FocusFragment newInstance(String param1, String param2) {
        FocusFragment fragment = new FocusFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_focus, container, false);

        mAuth = FirebaseAuth.getInstance();
        //DELETE ALL mUSERs
        firestore = FirebaseFirestore.getInstance();

        goToAddSubjectActivityButton = v.findViewById(R.id.goToAddSubjectButton);
        goToAddSubjectActivityButton.setOnClickListener(view -> startActivity(new Intent(getActivity(), AddSubjectActivity.class)));

        //https://www.youtube.com/watch?v=RLnb4vVkftc
        //hour and colon are automatically added when one hour is reached
        chronometer = v.findViewById(R.id.chronometer);

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

        return v;
    }


}