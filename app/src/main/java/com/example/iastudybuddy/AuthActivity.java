package com.example.iastudybuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

public class AuthActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    private EditText emailField;
    private EditText passwordField;

    private TextView usernameText;
    private EditText usernameField;
    private Button continueButton;

    //if user is already signed in, bring user to HomeActivity
    @Override
    public void onStart()
    {
        super.onStart();

        if(updateUI(mAuth.getCurrentUser()))
        {
            startActivity(new Intent(this, HomeActivity.class));
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        emailField = findViewById(R.id.emailEditText);
        passwordField = findViewById(R.id.passwordEditTextTextPassword);

        usernameText = findViewById(R.id.usernameTextView);
        usernameField = findViewById(R.id.usernameEditText);
        continueButton = findViewById(R.id.continueButton);

        usernameText.setVisibility(View.INVISIBLE);
        usernameField.setVisibility(View.INVISIBLE);
        continueButton.setVisibility(View.INVISIBLE);
    }

    public void signIn(View v)
    {
        String emailString = emailField.getText().toString();
        String passwordString = passwordField.getText().toString();

        //check if user document already exists in firebase
        firestore.collection("users").whereEqualTo("email", emailString).get().addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                //if account exists, sign in
                if(task.getResult().getDocuments().size() == 1)
                {
                    mAuth.signInWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(this, task1 -> {
                        if(task1.isSuccessful())
                        {
                            Log.d("SIGN IN", "signInWithEmail:success");

                            //if current user is not null, bring to HomeActivity
                            if(updateUI(mAuth.getCurrentUser()))
                            {
                                startActivity(new Intent(getBaseContext(), HomeActivity.class));
                            }
                        }
                        else
                        {
                            Log.w("SIGN IN", "signInWithEmail:failure", task1.getException());
                            Toast.makeText(getBaseContext(), "Sign in failed! Please create an account first!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                {
                    Toast.makeText(getBaseContext(), "This account does not exist. Sign up instead.", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(getBaseContext(), "Cannot fetch users!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void signUp(View v)
    {
        String emailString = emailField.getText().toString();
        String passwordString = passwordField.getText().toString();

        //check if account with inputted email exists
        firestore.collection("users").whereEqualTo("email", emailString).get().addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                //if account does not exist, create account
                if(task.getResult().getDocuments().size() == 0)
                {
                    mAuth.createUserWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(task1 -> {
                        if(task1.isSuccessful())
                        {
                            Log.d("SIGN UP", "signUpWithEmail:success");

                            //once account has been created, allow user to input username
                            usernameText.setVisibility(View.VISIBLE);
                            usernameField.setVisibility(View.VISIBLE);
                            continueButton.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            Log.w("SIGN UP", "signUpWithEmail:failure", task1.getException());
                            Toast.makeText(getBaseContext(), "Sign up failed!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                {
                    Toast.makeText(getBaseContext(), "This account already exists. Log in instead.", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(getBaseContext(), "Cannot fetch users!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void continueMethod(View v) //changed method name
    {
        String emailString = emailField.getText().toString();
        String passwordString = passwordField.getText().toString();
        String usernameString = usernameField.getText().toString();

        //check if username is unique
        firestore.collection("users").whereEqualTo("username", usernameString).get().addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                //if no other User document has the same username
                if(task.getResult().getDocuments().size() == 0)
                {
                    //signs user in
                    mAuth.signInWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(task1 -> {
                        if(task1.isSuccessful())
                        {
                            Log.d("SIGN IN", "signInWithEmail:success");

                            CISUser newUser = new CISUser(emailString, usernameString);
                            firestore.collection("users").document(newUser.getUid()).set(newUser);

                            //if current user is not null, bring to HomeActivity
                            if(updateUI(mAuth.getCurrentUser()))
                            {
                                startActivity(new Intent(getBaseContext(), HomeActivity.class));
                            }
                        }
                        else
                        {
                            Log.w("SIGN IN", "signInWithEmail:failure", task1.getException());
                            Toast.makeText(getBaseContext(), "Cannot sign in!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                {
                    Toast.makeText(getBaseContext(), "Username taken! Please enter another one.", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(getBaseContext(), "Fetch users failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //check if user is not null
    public boolean updateUI(FirebaseUser user)
    {
        if(user != null)
        {
            return true;
        }
        return false;
    }
}