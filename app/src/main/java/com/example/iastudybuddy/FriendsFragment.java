package com.example.iastudybuddy;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    private Button goToAddFriendButton;

    private RecyclerView requestsRecView;
    private ArrayList<CISUser> requestsList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FriendsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendsFragment newInstance(String param1, String param2) {
        FriendsFragment fragment = new FriendsFragment();
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
        View v =  inflater.inflate(R.layout.fragment_friends, container, false);

        mAuth = FirebaseAuth.getInstance();
        //DELETE ALL mUSERs
        firestore = FirebaseFirestore.getInstance();

        goToAddFriendButton = v.findViewById(R.id.goToAddFriendButton);
        goToAddFriendButton.setOnClickListener(view ->
        {
            startActivity(new Intent(getActivity(), AddFriendActivity.class));
        });

        //ArrayList of user's request CISUser objects
        requestsList = new ArrayList<>();
        //fetch current user
        firestore.collection("users").whereEqualTo("email", mAuth.getCurrentUser().getEmail()).get().addOnCompleteListener(task ->
        {
            if(task.isSuccessful())
            {
                for(DocumentSnapshot ds : task.getResult().getDocuments())
                {
                    CISUser currUser = ds.toObject(CISUser.class);

                    //loop through current user's requestsUID ArrayList
                    for(String currRequestUID : currUser.getRequestsUID())
                    {
                        //fetch CISUser object of current uid in current user's requestsUID ArrayList
                        firestore.collection("users").whereEqualTo("uid", currRequestUID).get().addOnCompleteListener(task1 ->
                        {
                            if(task1.isSuccessful())
                            {
                                for(DocumentSnapshot ds1 : task1.getResult().getDocuments())
                                {
                                    CISUser currUser1 = ds1.toObject(CISUser.class);

                                    //add CISUser object of uid in current user's requestsUID ArrayList
                                    requestsList.add(currUser1);
                                }

                                requestsRecView = v.findViewById(R.id.requestsRecView);
                                FriendsFragment.RequestsAdapter myAdapter = new FriendsFragment.RequestsAdapter(requestsList);
                                requestsRecView.setAdapter(myAdapter);
                                requestsRecView.setLayoutManager(new LinearLayoutManager(v.getContext()));
                            }
                        });
                    }
                }
            }
        });

        return v;
    }

    public static class RequestsAdapter extends RecyclerView.Adapter<FriendsFragment.RequestsViewHolder>
    {
        FirebaseFirestore firestore;
        FirebaseAuth mAuth;

        ArrayList<CISUser> requestsData;

        public RequestsAdapter(ArrayList<CISUser> data)
        {
            firestore = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();

            requestsData = data;
        }

        @NonNull
        @Override
        public FriendsFragment.RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View myView = LayoutInflater.from(parent.getContext()).inflate(R.layout.requests_row_view, parent, false);

            FriendsFragment.RequestsViewHolder holder = new FriendsFragment.RequestsViewHolder(myView);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull FriendsFragment.RequestsViewHolder holder, int position)
        {
            //display request user's username
            holder.requestUsernameText.setText(requestsData.get(position).getUsername());

            //if user clicks "accept"
            holder.acceptButton.setOnClickListener(view ->
            {
                //fetch current user
                firestore.collection("users").whereEqualTo("email", mAuth.getCurrentUser().getEmail()).get().addOnCompleteListener(task ->
                {
                    if(task.isSuccessful())
                    {
                        for(DocumentSnapshot ds : task.getResult().getDocuments())
                        {
                            CISUser currUser = ds.toObject(CISUser.class);

                            //remove request user's UID from current user's requestsUID ArrayList
                            firestore.collection("users").document(ds.getId()).update("requestsUID", FieldValue.arrayRemove(requestsData.get(position).getUid()));
                            //add request user's UID to current user's friendsUID ArrayList
                            firestore.collection("users").document(ds.getId()).update("friendsUID", FieldValue.arrayUnion(requestsData.get(position).getUid()));

                            //fetch request user
                            firestore.collection("users").whereEqualTo("uid", requestsData.get(position).getUid()).get().addOnCompleteListener(task1 ->
                            {
                                if(task1.isSuccessful())
                                {
                                    for(DocumentSnapshot ds1 : task1.getResult().getDocuments())
                                    {
                                        CISUser currRequestUser = ds1.toObject(CISUser.class);

                                        //if request user's requestsUID ArrayList is not empty
                                        if(currRequestUser.getRequestsUID().size() != 0)
                                        {
                                            //loop through each request user's UID in request user's requestUID ArrayList
                                            for(String currRequestUID : currRequestUser.getRequestsUID())
                                            {
                                                //if current request UID matches current user's UID
                                                if(currRequestUID.equals(currUser.getUid()))
                                                {
                                                    //remove current user's UID from request user's requestsUID ArrayList
                                                    firestore.collection("users").document(ds1.getId()).update("requestsUID", FieldValue.arrayRemove(currUser.getUid()));
                                                }
                                            }
                                        }
                                        //add current user's UID to request user's friendsUID ArrayList
                                        firestore.collection("users").document(ds1.getId()).update("friendsUID", FieldValue.arrayUnion(currUser.getUid()));

                                        //remove request user CISUser object from requestsData ArrayList
                                        requestsData.remove(position);
                                        //notify adapter that item has been removed
                                        notifyItemRemoved(position);
                                        //display Toast to let user know request user has been added as friend
                                        Toast.makeText(view.getContext(), "Friend added!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });
            });

            holder.declineButton.setOnClickListener(view ->
            {
                //fetch current user
                firestore.collection("users").whereEqualTo("email", mAuth.getCurrentUser().getEmail()).get().addOnCompleteListener(task ->
                {
                    if(task.isSuccessful())
                    {
                        for(DocumentSnapshot ds : task.getResult().getDocuments())
                        {
                            CISUser currUser = ds.toObject(CISUser.class);
                            //remove request user's UID from current user's requestsUID ArrayList
                            firestore.collection("users").document(ds.getId()).update("requestsUID", FieldValue.arrayRemove(requestsData.get(position).getUid()));

                            firestore.collection("users").whereEqualTo("uid", requestsData.get(position).getUid()).get().addOnCompleteListener(task1 ->
                            {
                                if(task1.isSuccessful())
                                {
                                    for(DocumentSnapshot ds1 : task1.getResult().getDocuments())
                                    {
                                        CISUser currRequestUser = ds1.toObject(CISUser.class);

                                        //if request user's requestsUID ArrayList is not empty
                                        if(currRequestUser.getRequestsUID().size() != 0)
                                        {
                                            //loop through each request user's UID in request user's requestUID ArrayList
                                            for(String currRequestUID : currRequestUser.getRequestsUID())
                                            {
                                                //if current request UID matches current user's UID
                                                if(currRequestUID.equals(currUser.getUid()))
                                                {
                                                    //remove current user's UID from request user's requestsUID ArrayList
                                                    firestore.collection("users").document(ds1.getId()).update("requestsUID", FieldValue.arrayRemove(currUser.getUid()));
                                                }
                                            }
                                        }

                                        //remove request user CISUser object from requestsData ArrayList
                                        requestsData.remove(position);
                                        //notify adapter that item has been removed
                                        notifyItemRemoved(position);
                                        //display Toast to let user know friend request has been deleted
                                        Toast.makeText(view.getContext(), "Friend deleted!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });
            });
        }

        @Override
        public int getItemCount()
        {
            return requestsData.size();
        }
    }

    public static class RequestsViewHolder extends RecyclerView.ViewHolder
    {
        protected TextView requestUsernameText;
        protected Button acceptButton;
        protected Button declineButton;

        public RequestsViewHolder(@NonNull View itemView)
        {
            super(itemView);

            requestUsernameText = itemView.findViewById(R.id.requestUsernameTextView);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            declineButton = itemView.findViewById(R.id.declineButton);
        }
    }
}