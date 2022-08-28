package com.example.iastudybuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddFriendActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    private String selectedMethod;
    private Spinner sMethod;

    private TextView friendEmailTextView;
    private TextView friendUsernameTextView;
    private EditText friendEmailField;
    private EditText friendUsernameField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        sMethod = findViewById(R.id.afMethodSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.methods, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sMethod.setAdapter(adapter);
        sMethod.setOnItemSelectedListener(this);

        friendEmailTextView = findViewById(R.id.friendEmailTextView);
        friendUsernameTextView = findViewById(R.id.friendUsernameTextView);
        friendEmailField = findViewById(R.id.friendEmailEditText);
        friendUsernameField = findViewById(R.id.friendUsernameEditText);

        friendEmailTextView.setVisibility(View.INVISIBLE);
        friendEmailField.setVisibility(View.INVISIBLE);
    }

    public void checkValid(View v)
    {
        //if selected "by email"
        if(selectedMethod.equals("by email"))
        {
            //if email field is not empty
            if(!friendEmailField.getText().toString().isEmpty())
            {
                //fetch user with inputted email
                firestore.collection("users").whereEqualTo("email", friendEmailField.getText().toString()).get().addOnCompleteListener(task ->
                {
                    //if there is exactly one user with inputted email
                    if(task.getResult().getDocuments().size() == 1)
                    {
                        for(DocumentSnapshot ds : task.getResult().getDocuments())
                        {
                            //call addNewFriend() method, passing in requested user's document ID
                            addNewFriend(v, ds.getId());
                        }
                    }
                    else
                    {
                        //if no user with inputted email/username, ask user to enter valid email/username
                        Toast.makeText(this, "Please enter a valid email/username", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else
            {
                //if username field is empty, ask user to enter valid email/username
                Toast.makeText(this, "Please enter a valid email/username", Toast.LENGTH_SHORT).show();
            }
        }
        //if selected "by username"
        else
        {
            //if username field is not empty
            if(!friendUsernameField.getText().toString().isEmpty())
            {
                //fetch user with inputted username
                firestore.collection("users").whereEqualTo("username", friendUsernameField.getText().toString()).get().addOnCompleteListener(task ->
                {
                    //if there is exactly one user with inputted username
                    if(task.getResult().getDocuments().size() == 1)
                    {
                        for(DocumentSnapshot ds : task.getResult().getDocuments())
                        {
                            //call addNewFriend() method, passing in requested user's document ID
                            addNewFriend(v, ds.getId());
                        }
                    }
                    else
                    {
                        //if no user with inputted email/username, ask user to enter valid email/username
                        Toast.makeText(this, "Please enter a valid email/username", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else
            {
                //if username field is empty, ask user to enter valid email/username
                Toast.makeText(this, "Please enter a valid email/username", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void addNewFriend(View v, String userDocID)
    {
        //fetch current user
        firestore.collection("users").whereEqualTo("email", mAuth.getCurrentUser().getEmail()).get().addOnCompleteListener(task ->
        {
            if(task.isSuccessful())
            {
                for(DocumentSnapshot ds : task.getResult().getDocuments())
                {
                    CISUser currUser = ds.toObject(CISUser.class);

                    //add current user's UID to requested user's requestsUID ArrayList
                    firestore.collection("users").document(userDocID).update("requestsUID", FieldValue.arrayUnion(currUser.getUid()));

                    Toast.makeText(this, "Friend request sent!", Toast.LENGTH_SHORT).show();
                    afBack(v);
                }
            }
        });
    }

    public void afBack(View v)
    {
        startActivity(new Intent(this, HomeActivity.class));
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
    {
        selectedMethod = adapterView.getItemAtPosition(i).toString();

        if(selectedMethod.equals("by email"))
        {
            friendUsernameTextView.setVisibility(View.INVISIBLE);
            friendUsernameField.setVisibility(View.INVISIBLE);
            friendEmailTextView.setVisibility(View.VISIBLE);
            friendEmailField.setVisibility(View.VISIBLE);
        }
        else if(selectedMethod.equals("by username"))
        {
            friendEmailTextView.setVisibility(View.INVISIBLE);
            friendEmailField.setVisibility(View.INVISIBLE);
            friendUsernameTextView.setVisibility(View.VISIBLE);
            friendUsernameField.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView)
    {

    }
}