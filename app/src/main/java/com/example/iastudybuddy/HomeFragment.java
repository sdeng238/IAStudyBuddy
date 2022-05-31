package com.example.iastudybuddy;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    private TextView timeOfTheDayText;
    private TextView nameText;
    private TextView todaysDateText;
    private TextView numIncomTasksText;
    private TextView todayTotFocHourText;
    private TextView todayTotFocMinText;

    public Button userProfileButton;
    public Button addFocusButton;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_home, container, false);

        timeOfTheDayText = v.findViewById(R.id.timeOfTheDayTextView);
        nameText = v.findViewById(R.id.nameTextView);
        todaysDateText = v.findViewById(R.id.todaysDateTextView);
        numIncomTasksText = v.findViewById(R.id.numIncomTasksTextView);
        todayTotFocHourText = v.findViewById(R.id.todayTotFocHourTextView);
        todayTotFocMinText = v.findViewById(R.id.todayTotFocMinTextView);

        //get today's date and current time
        Date currentDate = Calendar.getInstance().getTime();

        //display current time of the day
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
        String now = timeFormatter.format(currentDate);
        String afternoon = "12:00:00";
        String evening = "18:00:00";

        try
        {
            currentDate = timeFormatter.parse(now);
            Date afternoonDate = timeFormatter.parse(afternoon);
            Date eveningDate = timeFormatter.parse(evening);

            if(afternoonDate.after(currentDate))
            {
                timeOfTheDayText.setText("Morning");
            }
            else if(currentDate.after(afternoonDate) && eveningDate.after(currentDate))
            {
                timeOfTheDayText.setText("Afternoon");
            }
            else if(currentDate.after(eveningDate))
            {
                timeOfTheDayText.setText("Evening");
            }
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        //display today's date
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = dateFormatter.format(currentDate);
        todaysDateText.setText(strDate);

        //fetch current user
        firestore.collection("users").whereEqualTo("email", mAuth.getCurrentUser().getEmail()).get().addOnCompleteListener(getActivity(), new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    for(DocumentSnapshot ds : task.getResult().getDocuments())
                    {
                        CISUser currUser = ds.toObject(CISUser.class);
                        //display username
                        nameText.setText(currUser.getUsername());
                        //display the number of incomplete tasks
                        numIncomTasksText.setText(Integer.toString(currUser.getTasks().size()));
                        //display today's total focus time
                        todayTotFocHourText.setText(Integer.toString(currUser.getTodayTotalFocusTime().get(0)));
                        todayTotFocMinText.setText(Integer.toString(currUser.getTodayTotalFocusTime().get(1)));
                    }
                }
                else
                {
                    Toast.makeText(getActivity(), "Cannot fetch users!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return v;
    }


    public void goToUserProfileActivity(View v)
    {
        startActivity(new Intent(getActivity(), UserProfileActivity.class));
    }

    public void goToFocusActivity(View v)
    {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.home, new FocusFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}