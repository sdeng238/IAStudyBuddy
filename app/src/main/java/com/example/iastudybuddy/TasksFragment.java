package com.example.iastudybuddy;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.color.ColorRoles;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TasksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TasksFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    private TextView numComTasksText;
    private Button goToAddTaskActivityButton;

    private RecyclerView tasksRecView;
    private ArrayList<CISTask> tasksList;
    private ArrayList<CISTask> sortedTasksList;
    private int[] subjectColours; //NEW
    private ArrayList<String> subjectUID; //NEW
    private ArrayList<Integer> sortingIndex;
    private CISTask deletedTask;

    private String selectedSort;
    private Spinner sSort;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TasksFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TasksFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TasksFragment newInstance(String param1, String param2) {
        TasksFragment fragment = new TasksFragment();
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
        final View v = inflater.inflate(R.layout.fragment_tasks, container, false);

        mAuth = FirebaseAuth.getInstance();
        //DELETE ALL mUSERs
        firestore = FirebaseFirestore.getInstance();

        goToAddTaskActivityButton = v.findViewById(R.id.goToAddTaskButton);
        goToAddTaskActivityButton.setOnClickListener(v1 -> startActivity(new Intent(getActivity(), AddTaskActivity.class)));

        tasksRecView = v.findViewById(R.id.tasksRecView);

        //array of subject colour ints
        subjectColours = new int[]{getResources().getColor(R.color.red), getResources().getColor(R.color.orange), getResources().getColor(R.color.yellow), getResources().getColor(R.color.green), getResources().getColor(R.color.blue), getResources().getColor(R.color.purple), getResources().getColor(R.color.pink)};

        //ArrayList of user's Subject objects' UIDs
        subjectUID = new ArrayList<>();
        firestore.collection("subjects").whereEqualTo("ownerEmail", mAuth.getCurrentUser().getEmail()).get().addOnCompleteListener(task ->
        {
            if(task.isSuccessful())
            {
                for(DocumentSnapshot ds : task.getResult().getDocuments())
                {
                    Subject currSubject = ds.toObject(Subject.class);

                    subjectUID.add(currSubject.getUid());
                }
            }
        });

        //ArrayList of user's Task objects NOT sorted by date
        tasksList = new ArrayList<>();

        //ArrayList of sorting index for selected sort (by date)
        sortingIndex = new ArrayList<>();
        //ArrayList of user's Task objects SORTED BY DATE
        sortedTasksList = new ArrayList<>();
        numComTasksText = v.findViewById(R.id.numComTasksTextView);
        deletedTask = null;
        //fetch user's Task objects
        firestore.collection("tasks").whereEqualTo("ownerEmail", mAuth.getCurrentUser().getEmail()).get().addOnCompleteListener(task ->
        {
            if(task.isSuccessful())
            {
                for(DocumentSnapshot ds : task.getResult().getDocuments())
                {
                    CISTask currTask = ds.toObject(CISTask.class);
                    tasksList.add(currTask);
                }

                //fetch current user
                firestore.collection("users").whereEqualTo("email", mAuth.getCurrentUser().getEmail()).get().addOnCompleteListener(task1 ->
                {
                    if(task1.isSuccessful())
                    {
                        for(DocumentSnapshot ds1 : task1.getResult().getDocuments())
                        {
                            CISUser currUser = ds1.toObject(CISUser.class);

                            numComTasksText.setText(String.valueOf(currUser.getTodayTasksCompleted()));

                            boolean added = false;
                            //loop through each element in tasksList
                            for(int num = 0; num < tasksList.size(); num++)
                            {
                                //if nothing has been added to sortedTasksList, add first element of tasksList to sortedTasksList
                                if(sortedTasksList.size() == 0)
                                {
                                    sortedTasksList.add(tasksList.get(num));
                                }
                                //if something has already been added to sortedTasksList
                                else
                                {
                                    //set added to false
                                    added = false;
                                    //loop through each element in sortedTasksList
                                    for(int numTwo = 0; numTwo < sortedTasksList.size(); numTwo++)
                                    {
                                        //if added is false
                                        if(!added)
                                        {
                                            //if creation date of Task in taskList is before that of current Task in sortedTaskList
                                            if(new Date(tasksList.get(num).getCreationDay().getTime()).before(new Date(sortedTasksList.get(numTwo).getCreationDay().getTime())))
                                            {
                                                //insert Task in taskList before current Task in sortedTaskList
                                                sortedTasksList.add(numTwo, tasksList.get(num));
                                                //set added to true
                                                added = true;
                                            }
                                        }
                                    }
                                    //if Task in taskList has not been added to sortedTaskList
                                    if(!added)
                                    {
                                        //add Task in taskList to the end of sortedTaskList
                                        sortedTasksList.add(tasksList.get(num));
                                    }
                                }
                            }

                            //add numbers in natural order to sortingIndex ArrayList to make "by date" show up as sortedTasksList
                            for(int num = 0; num < sortedTasksList.size(); num++)
                            {
                                sortingIndex.add(num);
                            }

                            //create and add adapter for recView
                            TasksAdapter myAdapter = new TasksAdapter(sortedTasksList, subjectColours, sortingIndex);
                            tasksRecView.setAdapter(myAdapter);
                            tasksRecView.setLayoutManager(new LinearLayoutManager(v.getContext()));

                            ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT)
                            {
                                //not used -> only applied for drag and rearrange items
                                @Override
                                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target)
                                {
                                    return false;
                                }

                                //swipe features
                                @Override
                                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction)
                                {
                                    int position = viewHolder.getAdapterPosition();
                                    DocumentReference currUserRef = firestore.collection("users").document(ds1.getId());

                                    switch (direction)
                                    {
                                        //handle the case in which user swipes from right to left <--
                                        case ItemTouchHelper.LEFT:
                                            deletedTask = sortedTasksList.get(position);
                                            sortedTasksList.remove(position);
                                            myAdapter.notifyItemRemoved(position);

                                            //remove completed Task object UID from tasks ArrayList in Firebase user
                                            currUserRef.update("tasks", FieldValue.arrayRemove(deletedTask.getUid()));
                                            //delete Task object document from tasks collection in Firebase
                                            firestore.collection("tasks").document(deletedTask.getUid()).delete();

                                            //increase todayTasksCompleted by 1 in Firebase user
                                            currUserRef.update("todayTasksCompleted", currUser.getTodayTasksCompleted() + 1);
                                            //increase numComTasksTextView by 1 on display
                                            numComTasksText.setText(String.valueOf(currUser.getTodayTasksCompleted() + 1));

                                            Snackbar.make(tasksRecView, deletedTask.getName() + " has been completed!", Snackbar.LENGTH_INDEFINITE)
                                                    .setAction("Undo", view ->
                                                    {
                                                        sortedTasksList.add(position, deletedTask);
                                                        myAdapter.notifyItemInserted(position);

                                                        currUserRef.update("tasks", FieldValue.arrayUnion(deletedTask.getUid()));
                                                        firestore.collection("tasks").document(deletedTask.getUid()).set(deletedTask);

                                                        currUserRef.update("todayTasksCompleted", currUser.getTodayTasksCompleted());
                                                        numComTasksText.setText(String.valueOf(currUser.getTodayTasksCompleted()));
                                                    }).show();
                                            break;
                                    }
                                }

                                @Override
                                public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                                    new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                                            .addSwipeLeftBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.green_2))
                                            .addSwipeLeftActionIcon(R.drawable.ic_baseline_done_outline_24)
                                            .create()
                                            .decorate();

                                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                                }
                            };

                            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
                            itemTouchHelper.attachToRecyclerView(tasksRecView);
                        }

                    }
                });
            }

        });

        sSort = v.findViewById(R.id.tSortSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(v.getContext(), R.array.sorts, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sSort.setAdapter(adapter);
        sSort.setOnItemSelectedListener(this);

        return v;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
    {
        selectedSort = adapterView.getItemAtPosition(i).toString();

        if(selectedSort.equals("by date"))
        {
            sortingIndex.clear();

            //add by ascending order (natural order) bc tasks are added to list according to time anyways
            for(int num = 0; num < sortedTasksList.size(); num++)
            {
                sortingIndex.add(num);
            }
        }
        //if sort by subject
        else
        {
            sortingIndex.clear();

            //fetch Subject objects owned by current user
            for(String currSubjectUID : subjectUID)
            {
                //loop through each Task object owned by current user
                for(int num = 0; num < sortedTasksList.size(); num++)
                {
                    //if current Task object's subject UID matches current Subject object UID, add index of Task object to sortIndex ArrayList
                    if(sortedTasksList.get(num).getSubject().equals(currSubjectUID))
                    {
                        sortingIndex.add(num);
                    }
                }
            }
        }

        TasksAdapter currAdapter = new TasksAdapter(sortedTasksList, subjectColours, sortingIndex);
        tasksRecView.setAdapter(currAdapter);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView)
    {

    }

    public static class TasksAdapter extends RecyclerView.Adapter<TasksViewHolder>
    {
        FirebaseFirestore firestore;
        FirebaseAuth mAuth;

        ArrayList<CISTask> tasksData;
        int[] subjectColours;

        ArrayList<Integer> sortIndex;

        public TasksAdapter(ArrayList<CISTask> data, int[] colours, ArrayList<Integer> sortingIndex)
        {
            firestore = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();

            tasksData = data;
            subjectColours = colours;

            sortIndex = sortingIndex;
        }

        @NonNull
        @Override
        public TasksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View myView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tasks_row_view, parent, false);

            TasksViewHolder holder = new TasksViewHolder(myView);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull TasksViewHolder holder, int position)
        {
            mAuth = FirebaseAuth.getInstance();
            firestore = FirebaseFirestore.getInstance();

            holder.taskNameText.setText(tasksData.get(sortIndex.get(position)).getName());

            //get LocalDate objects of today's date and task's creation date
            LocalDate dateAfter = LocalDate.now();

            Date creationDate = new Date(tasksData.get(sortIndex.get(position)).getCreationDay().getTime());
            LocalDate dateBefore = creationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            //calculate difference in days between today's date and task's creation date
            int daysDiff = (int) ChronoUnit.DAYS.between(dateBefore, dateAfter);

            //if no different in days, display today
            if(daysDiff == 0)
            {
                holder.creationDayText.setText("Today");
            }
            //if have difference in days, display number of days overdue and in red
            else
            {
                holder.creationDayText.setText(String.valueOf(daysDiff) + " days ago");
                holder.creationDayText.setTextColor(Color.RED);
            }

            //fetch Subject object with same UID of task's subject and is owned by current user
            firestore.collection("subjects").whereEqualTo("ownerEmail", mAuth.getCurrentUser().getEmail()).whereEqualTo("uid", tasksData.get(sortIndex.get(position)).getSubject()).get().addOnCompleteListener(task ->
            {
                if(task.isSuccessful())
                {
                    for(DocumentSnapshot ds : task.getResult().getDocuments())
                    {
                        Subject currSubject = ds.toObject(Subject.class);

                        //loop through each subject colour in subjectColours ArrayList
                        for(int num = 0; num < subjectColours.length; num++)
                        {
                            //if colour of task's subject colour matches with current subject colour, set colour
                            if(currSubject.getColour().equals("red"))
                            {
                                holder.taskSubjectColourText.setTextColor(subjectColours[0]);
                                holder.taskSubjectColourText2.setTextColor(subjectColours[0]);
                            }
                            else if(currSubject.getColour().equals("orange"))
                            {
                                holder.taskSubjectColourText.setTextColor(subjectColours[1]);
                                holder.taskSubjectColourText2.setTextColor(subjectColours[1]);
                            }
                            else if(currSubject.getColour().equals("yellow"))
                            {
                                holder.taskSubjectColourText.setTextColor(subjectColours[2]);
                                holder.taskSubjectColourText2.setTextColor(subjectColours[2]);
                            }
                            else if(currSubject.getColour().equals("green"))
                            {
                                holder.taskSubjectColourText.setTextColor(subjectColours[3]);
                                holder.taskSubjectColourText2.setTextColor(subjectColours[3]);
                            }
                            else if(currSubject.getColour().equals("blue"))
                            {
                                holder.taskSubjectColourText.setTextColor(subjectColours[4]);
                                holder.taskSubjectColourText2.setTextColor(subjectColours[4]);
                            }
                            else if(currSubject.getColour().equals("purple"))
                            {
                                holder.taskSubjectColourText.setTextColor(subjectColours[5]);
                                holder.taskSubjectColourText2.setTextColor(subjectColours[5]);
                            }
                            else if(currSubject.getColour().equals("pink"))
                            {
                                holder.taskSubjectColourText.setTextColor(subjectColours[6]);
                                holder.taskSubjectColourText2.setTextColor(subjectColours[6]);
                            }
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return tasksData.size();
        }
    }

    public static class TasksViewHolder extends RecyclerView.ViewHolder
    {
        protected TextView taskNameText;
        protected TextView creationDayText;
        protected TextView taskSubjectColourText;
        protected TextView taskSubjectColourText2;


        public TasksViewHolder(@NonNull View itemView) {
            super(itemView);

            taskNameText = itemView.findViewById(R.id.taskNameTextView);
            creationDayText = itemView.findViewById(R.id.creationDayTextView);
            taskSubjectColourText = itemView.findViewById(R.id.taskSubjectColourTextView);
            taskSubjectColourText2 = itemView.findViewById(R.id.taskSubjectColourTextView2);
        }
    }
}