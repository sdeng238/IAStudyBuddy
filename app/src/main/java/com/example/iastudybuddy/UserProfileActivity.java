package com.example.iastudybuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

public class UserProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    private CISUser userInfo;

    private TextView nameText;
    private TextView emailText;
    private TextView personalBestText;
    private TextView totFocusTimeText;
    //deleted total tasks completed
    private TextView rankText;
    private TextView crownsReceivedText;

    // make back button
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        //current user being shown in this activity
        userInfo = (CISUser) getIntent().getSerializableExtra("user");

        nameText = findViewById(R.id.upUsernameTextView);
        emailText = findViewById(R.id.upEmailTextView);
        personalBestText = findViewById(R.id.upPersonalBestTextView);
        totFocusTimeText = findViewById(R.id.upTotalFocusTimeTextView);
        rankText = findViewById(R.id.rankTextView);
        crownsReceivedText = findViewById(R.id.crownsReceivedTextView);

        //display username
        nameText.setText(userInfo.getUsername());
        //display email
        emailText.setText(userInfo.getEmail());
        //display personal best
        personalBestText.setText(userInfo.getPersonalBestMinutes()/24 + "h " + userInfo.getPersonalBestMinutes()%24 + "min");
        //display total focus time
        totFocusTimeText.setText(userInfo.getTotalFocusMinutes()/24 + "h " + userInfo.getTotalFocusMinutes()%24 + "min");
        //display rank
        int totalFocusHours = userInfo.getTotalFocusMinutes()/24;
        if(totalFocusHours > 700)
        {
            rankText.setText("Diamond\n ≥700h");
        }
        else if(totalFocusHours >= 450)
        {
            rankText.setText("Gold\n ≥400h");
        }
        else if(totalFocusHours >= 300)
        {
            rankText.setText("Silver\n ≥300h");
        }
        else if(totalFocusHours >= 150)
        {
            rankText.setText("Bronze\n ≥150h");
        }
        //display crowns received
        crownsReceivedText.setText(Integer.toString(userInfo.getCrownsNumber()));
    }

    public void upBack(View v)
    {
        startActivity(new Intent(this, HomeActivity.class));
    }

    public void signOut(View v)
    {
        mAuth.signOut();

        startActivity(new Intent(this, AuthActivity.class));
    }
}