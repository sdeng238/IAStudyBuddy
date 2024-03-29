package com.example.iastudybuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * This class allows users to create a new task if the input is valid (the fields are not empty and
 * a subject is selected).
 *
 * @author Shirley Deng
 * @version 0.1
 */
public class AddTaskActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    private EditText taskNameField;


    //https://www.youtube.com/watch?v=on_OrrX7Nw4
    private String selectedSubject;
    private Spinner sSubject;
    private ArrayList<String> listedSubjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        taskNameField = findViewById(R.id.taskNameEditText);

        listedSubjects = new ArrayList<>();
        listedSubjects.add("Select subject");

        firestore.collection("subjects").whereEqualTo("ownerEmail", mAuth.getCurrentUser().getEmail()).get().addOnCompleteListener(task ->
        {
           if(task.isSuccessful())
           {
               for(DocumentSnapshot ds : task.getResult().getDocuments())
               {
                   Subject currSubject = ds.toObject(Subject.class);
                   listedSubjects.add(currSubject.getName());
               }
           }

            sSubject = findViewById(R.id.atSubjectSpinner);
            //get listed subjects from current user's subject ArrayList
            //https://www.tutorialspoint.com/how-to-create-spinner-programmatically-from-array-in-android
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listedSubjects);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sSubject.setAdapter(adapter);
            sSubject.setOnItemSelectedListener(this);
        });
    }

    public void addNewTask(View v)
    {
        String taskNameString = taskNameField.getText().toString();

        //if user inputted a task name and selected a subject
        if(!taskNameString.equals("") && !selectedSubject.equals("Select subject"))
        {
            firestore.collection("tasks").whereEqualTo("ownerEmail", mAuth.getCurrentUser().getEmail()).whereEqualTo("name", taskNameString).get().addOnCompleteListener(task ->
            {
                if(task.isSuccessful())
                {
                    if(task.getResult().getDocuments().size() == 0)
                    {
                        //look for subject owned by user and with selected name (unique)
                        firestore.collection("subjects").whereEqualTo("ownerEmail", mAuth.getCurrentUser().getEmail()).whereEqualTo("name", selectedSubject).get().addOnCompleteListener(task1 ->
                        {
                            if(task1.isSuccessful())
                            {
                                for(DocumentSnapshot ds : task1.getResult().getDocuments())
                                {
                                    Subject currSubject = ds.toObject(Subject.class);

                                    //fetch current user
                                    firestore.collection("users").whereEqualTo("email", mAuth.getCurrentUser().getEmail()).get().addOnCompleteListener(task2 ->
                                    {
                                        if(task2.isSuccessful())
                                        {
                                            for(DocumentSnapshot ds1 : task2.getResult().getDocuments())
                                            {
                                                //create new task with current subject UID
                                                CISTask newTask = new CISTask(taskNameString, currSubject.getUid(), mAuth.getCurrentUser().getEmail());

                                                //add new Task object to tasks collection, setting doc name as task UID
                                                firestore.collection("tasks").document(newTask.getUid()).set(newTask);
                                                //add new task uid to tasks ArrayList in current user
                                                firestore.collection("users").document(ds1.getId()).update("tasks", FieldValue.arrayUnion(newTask.getUid()));

                                                Toast.makeText(getBaseContext(), "Task added!", Toast.LENGTH_SHORT).show();
                                                atBack(v);
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(getBaseContext(), "A task with this name already exists", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else
        {
            Toast.makeText(this, "Please type in your task name/select a subject!", Toast.LENGTH_SHORT).show();
        }
    }

    public void atBack(View v)
    {
        startActivity(new Intent(this, HomeActivity.class));
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selectedSubject = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}