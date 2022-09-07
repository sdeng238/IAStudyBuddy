package com.example.iastudybuddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.atomic.AtomicBoolean;

public class AddSubjectActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    private EditText subjectNameField;

    //https://www.youtube.com/watch?v=on_OrrX7Nw4
    private String selectedColour;
    private Spinner sColour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        subjectNameField = findViewById(R.id.subjectNameEditText);

        sColour = findViewById(R.id.asColourSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.colours, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sColour.setAdapter(adapter);
        sColour.setOnItemSelectedListener(this);
    }

    public void addNewSubject(View v)
    {
        String subjectNameString = subjectNameField.getText().toString();

        //if user inputted a subject name and selected a colour
        if(!subjectNameString.equals("") && !selectedColour.equals("Select colour"))
        {
            //look for subject owned by user and with same name
            firestore.collection("subjects").whereEqualTo("ownerEmail", mAuth.getCurrentUser().getEmail()).whereEqualTo("name", subjectNameString).get().addOnCompleteListener(task ->
            {
                if(task.isSuccessful())
                {
                    //if no subject with the same name exists
                    if(task.getResult().getDocuments().size() == 0)
                    {
                        //fetch current user
                        firestore.collection("users").whereEqualTo("email", mAuth.getCurrentUser().getEmail()).get().addOnCompleteListener(task1 ->
                        {
                            if (task1.isSuccessful())
                            {
                                for(DocumentSnapshot ds : task1.getResult().getDocuments())
                                {
                                    //create new subject
                                    Subject newSubject = new Subject(selectedColour, subjectNameString, mAuth.getCurrentUser().getEmail());

                                    //add new Subject object to subjects collection, setting doc name as subject UID
                                    firestore.collection("subjects").document(newSubject.getUid()).set(newSubject);
                                    //add new subject uid to subjects ArrayList in current user
                                    firestore.collection("users").document(ds.getId()).update("subjects", FieldValue.arrayUnion(newSubject.getUid()));

                                    Toast.makeText(getBaseContext(), "Subject added!", Toast.LENGTH_SHORT).show();
                                    asBack(v);
                                }
                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(getBaseContext(), "A subject with this name already exists", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else
        {
            Toast.makeText(getBaseContext(), "Please type in your subject name/select a colour!", Toast.LENGTH_SHORT).show();
        }
    }

    public void asBack(View v)
    {
        startActivity(new Intent(this, HomeActivity.class));
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
    {
        selectedColour = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView)
    {

    }
}