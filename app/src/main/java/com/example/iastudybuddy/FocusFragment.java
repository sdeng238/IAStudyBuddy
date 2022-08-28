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
 * A simple {@link Fragment} subclass.
 * Use the {@link FocusFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FocusFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    private Button goToAddSubjectActivityButton;

    private Chronometer chronometer;
    //time difference from when user starts chronometer from when they paused it
    private long pauseOffset;
    private boolean running;
    private Button chrStartButton;
    private Button chrPauseButton;
    private Button chrResetButton;

    private RecyclerView subjectsRecView;
    private ArrayList<Subject> subjectsList;
    private int[] subjectColours; //NEW

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

        mAuth = FirebaseAuth.getInstance();
        //DELETE ALL mUSERs
        firestore = FirebaseFirestore.getInstance();

        goToAddSubjectActivityButton = v.findViewById(R.id.goToAddSubjectButton);
        goToAddSubjectActivityButton.setOnClickListener(view -> startActivity(new Intent(getActivity(), AddSubjectActivity.class)));

        //hour and colon are automatically added when one hour is reached
        chronometer = v.findViewById(R.id.chronometer);
        //set as Time: (chronometer) when activity starts
//        chronometer.setFormat("Time: %s");
//        chronometer.setBase(SystemClock.elapsedRealtime());

        chrStartButton = v.findViewById(R.id.chrStartButton);
//        chrStartButton.setBackgroundColor(getResources().getColor(R.color.blue));
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


        //array of subject colour ints
        subjectColours = new int[]{getResources().getColor(R.color.red), getResources().getColor(R.color.orange), getResources().getColor(R.color.yellow), getResources().getColor(R.color.green), getResources().getColor(R.color.blue), getResources().getColor(R.color.purple), getResources().getColor(R.color.pink)};
        //ArrayList of user's Subject objects
        subjectsList = new ArrayList<>();
        //fetch user's subjects
        firestore.collection("subjects").whereEqualTo("ownerEmail", mAuth.getCurrentUser().getEmail()).get().addOnCompleteListener(task ->
        {
            if(task.isSuccessful())
            {
                for(DocumentSnapshot ds : task.getResult().getDocuments())
                {
                    Subject currSubject = ds.toObject(Subject.class);

                    subjectsList.add(currSubject);
                }

                subjectsRecView = v.findViewById(R.id.subjectsRecView);
                SubjectsAdapter myAdapter = new SubjectsAdapter(subjectsList, subjectColours);
                subjectsRecView.setAdapter(myAdapter);
                subjectsRecView.setLayoutManager(new LinearLayoutManager(v.getContext()));
            }
        });

        return v;
    }


    public static class SubjectsAdapter extends RecyclerView.Adapter<SubjectsViewHolder>
    {
        ArrayList<Subject> subjectsData;
        int[] subjectColours;

        public SubjectsAdapter(ArrayList data, int[] colours)
        {
            subjectsData = data;
            subjectColours = colours;
        }

        @NonNull
        @Override
        public SubjectsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View myView = LayoutInflater.from(parent.getContext()).inflate(R.layout.subjects_row_view, parent, false);

            SubjectsViewHolder holder = new SubjectsViewHolder(myView);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull SubjectsViewHolder holder, int position)
        {
            holder.subjectNameText.setText(subjectsData.get(position).getName());
            //if colour of task's subject colour matches with current subject colour, set colour
            if(subjectsData.get(position).getColour().equals("red"))
            {
                holder.subjectNameText.setTextColor(subjectColours[0]);
            }
            else if(subjectsData.get(position).getColour().equals("orange"))
            {
                holder.subjectNameText.setTextColor(subjectColours[1]);
            }
            else if(subjectsData.get(position).getColour().equals("yellow"))
            {
                holder.subjectNameText.setTextColor(subjectColours[2]);
            }
            else if(subjectsData.get(position).getColour().equals("green"))
            {
                holder.subjectNameText.setTextColor(subjectColours[3]);
            }
            else if(subjectsData.get(position).getColour().equals("blue"))
            {
                holder.subjectNameText.setTextColor(subjectColours[4]);
            }
            else if(subjectsData.get(position).getColour().equals("purple"))
            {
                holder.subjectNameText.setTextColor(subjectColours[5]);
            }
            else if(subjectsData.get(position).getColour().equals("pink"))
            {
                holder.subjectNameText.setTextColor(subjectColours[6]);
            }

            ArrayList<Integer> todayFocusTime = subjectsData.get(position).getTodayFocusTime();
            holder.subjectTimeText.setText(todayFocusTime.get(0) + ":" + todayFocusTime.get(1) + ":" + todayFocusTime.get(2));
        }

        @Override
        public int getItemCount()
        {
            return subjectsData.size();
        }
    }


    public static class SubjectsViewHolder extends RecyclerView.ViewHolder
    {
        protected TextView subjectNameText;
        protected TextView subjectTimeText;
        protected Button startStopButton;

        public SubjectsViewHolder(@NonNull View itemView)
        {
            super(itemView);

            subjectNameText = itemView.findViewById(R.id.srSubjectNameTextView);
            subjectTimeText = itemView.findViewById(R.id.srSubjectTimeTextView);
            startStopButton = itemView.findViewById(R.id.startStopButton);
        }
    }
}