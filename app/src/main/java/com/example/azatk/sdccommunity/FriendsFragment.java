package com.example.azatk.sdccommunity;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private RecyclerView mFriendsList;

    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUsersDatabase;
    private FirebaseAuth mAuth;

    private String mCurrent_user_id;
    String list_user_id;
    String list_user_id2;
    private View mMainView;

    //-----------------Request---------------------------------
    private RecyclerView mRequestList;

    private DatabaseReference mRequestDatabase;
    private DatabaseReference mRequestUsersDatabase;
    private DatabaseReference mFriendReqDatabase;
    private FirebaseAuth requestAuth;

    private String requestCurrent_user_id;

    private FirebaseUser mCurrent_user;
    private DatabaseReference mRootRef;

    String mCurrent_state = "";

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_friends, container, false);

       mFriendsList = (RecyclerView) mMainView.findViewById(R.id.friends_list);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        mFriendsDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mUsersDatabase.keepSynced(true);

        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        //-----------------------Request-------------------------

        requestAuth = FirebaseAuth.getInstance();

        mRequestList = (RecyclerView)mMainView.findViewById(R.id.request_list);

        requestCurrent_user_id = requestAuth.getCurrentUser().getUid();

        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();
        mRootRef = FirebaseDatabase.getInstance().getReference();

        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(requestCurrent_user_id);
        mRequestDatabase.keepSynced(true);
        mRequestUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mRequestUsersDatabase.keepSynced(true);

        //mRequestList.setHasFixedSize(true);
        mRequestList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inflate the layout for this fragment*/
        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> friendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(
                Friends.class,
                R.layout.users_single_layout,
                FriendsViewHolder.class,
                mFriendsDatabase
        ) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder friendsViewHolder, final Friends friends, int position) {

                friendsViewHolder.setDate(friends.getDate());

                final String list_user_id = getRef(position).getKey();

                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();

                        if(dataSnapshot.hasChild("online")){

                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            friendsViewHolder.setUserOnline(userOnline);

                        }


                        friendsViewHolder.setName(userName);
                        friendsViewHolder.setUserImage(userThumb,getContext());

                        friendsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                CharSequence options[] = new CharSequence[]{"Open Profile","Send message"};

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Select Options");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {

                                        if(i == 0){

                                            Intent profileIntent = new Intent(getContext(),ProfileActivity.class);
                                            profileIntent.putExtra("user_id",list_user_id);
                                            startActivity(profileIntent);

                                        }
                                        if(i == 1){

                                            Intent messageIntent = new Intent(getContext(),ChatActivity.class);
                                            messageIntent.putExtra("user_id",list_user_id);
                                            messageIntent.putExtra("user_name",userName);
                                            startActivity(messageIntent);

                                        }


                                    }
                                });

                                builder.show();
                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        mFriendsList.setAdapter(friendsRecyclerViewAdapter);


        FirebaseRecyclerAdapter<Requests, FriendsFragment.RequestsViewHolder> requestsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Requests, FriendsFragment.RequestsViewHolder>(
                Requests.class,
                R.layout.request_item_layout,
                FriendsFragment.RequestsViewHolder.class,
                mRequestDatabase
        ) {
            @Override
            protected void populateViewHolder(final FriendsFragment.RequestsViewHolder requestsViewHolder, final Requests requests, int position) {

                list_user_id2 = getRef(position).getKey();


                mFriendReqDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(list_user_id2)) {

                            String req_type = dataSnapshot.child(list_user_id2).child("request_type").getValue().toString();

                            if (req_type.equals("received")) {

                                mCurrent_state = "req_received";


                            } else if (req_type.equals("sent")) {

                                mCurrent_state = "req_sent";


                            }

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mRequestUsersDatabase.child(list_user_id2).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                        if (dataSnapshot.hasChild("online")) {

                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            requestsViewHolder.setUserOnline(userOnline);

                        }


                        requestsViewHolder.setName(userName);
                        requestsViewHolder.setUserImage(userThumb, getContext());


                        requestsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent profileIntent = new Intent(getActivity(), ProfileActivity.class);
                                profileIntent.putExtra("user_id", list_user_id2);
                                startActivity(profileIntent);

                            }
                        });


                        if(mCurrent_state.equals("req_received")) {
                            requestsViewHolder.accept.setText("Accept");
                            requestsViewHolder.accept.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    final String currentData = DateFormat.getDateTimeInstance().format(new Date());

                                    Map friendsMap = new HashMap();
                                    friendsMap.put("Friends/" + mCurrent_user.getUid() + "/" + list_user_id2 + "/date", currentData);
                                    friendsMap.put("Friends/" + list_user_id2 + "/" + mCurrent_user.getUid() + "/date", currentData);

                                    friendsMap.put("Friend_req/" + mCurrent_user.getUid() + "/" + list_user_id2, null);
                                    friendsMap.put("Friend_req/" + list_user_id2 + "/" + mCurrent_user.getUid(), null);

                                    mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                            if (databaseError == null) {

                                                Toast.makeText(getContext(), "Cool", Toast.LENGTH_SHORT).show();

                                            } else {

                                                String error = databaseError.getMessage();

                                                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();

                                            }

                                        }
                                    });
                                }
                            });
                            requestsViewHolder.cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    mFriendReqDatabase.child(mCurrent_user.getUid()).child(list_user_id2).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            mFriendReqDatabase.child(list_user_id2).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                }
                                            });

                                        }
                                    });

                                }
                            });
                        }else if(mCurrent_state.equals("req_sent")){
                            requestsViewHolder.accept.setText("Cancel");
                            requestsViewHolder.cancel.setVisibility(View.INVISIBLE);
                            requestsViewHolder.accept.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {


                                    mFriendReqDatabase.child(mCurrent_user.getUid()).child(list_user_id2).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            mFriendReqDatabase.child(list_user_id2).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                }
                                            });

                                        }
                                    });
                                }
                            });

                        }


                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        if(mCurrent_state.equals("req_sent")){
            requestsRecyclerViewAdapter = null;
            mRequestList.setAdapter(requestsRecyclerViewAdapter);
        }else if(mCurrent_state.equals("req_received")){
            mRequestList.setAdapter(requestsRecyclerViewAdapter);
        }else{
            mRequestList.setAdapter(requestsRecyclerViewAdapter);
        };

    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public FriendsViewHolder(View itemView){
            super(itemView);

            mView = itemView;

        }

        public void setDate(String date){

            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_status);
            userNameView.setText(date);

        }

        public void setName(String name){

            TextView userNameView = (TextView)mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);

        }

        public void setUserImage(String thumb_image, Context context){

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);

            Picasso.with(context).load(thumb_image).placeholder(R.drawable.default_person).into(userImageView);

        }

        public void setUserOnline(String online_status){

            ImageView userOnlineView = (ImageView)mView.findViewById(R.id.user_single_online_icon);

            if(online_status.equals("true")){

                userOnlineView.setVisibility(View.VISIBLE);

            }else{

                userOnlineView.setVisibility(View.INVISIBLE);

            }

        }

    }


    //-----------------------Request ------------------------------------------------------------

    public static class RequestsViewHolder extends RecyclerView.ViewHolder{

        View mView;
        Button accept;
        Button cancel;

        public RequestsViewHolder(View itemView){
            super(itemView);

            mView = itemView;
            accept = (Button) mView.findViewById(R.id.button2);
            cancel = (Button) mView.findViewById(R.id.button3);


        }


        public void setName(String name){

            TextView userNameView = (TextView)mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);

        }

        public void setUserImage(String thumb_image, Context context){

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);

            Picasso.with(context).load(thumb_image).placeholder(R.drawable.default_person).into(userImageView);

        }

        public void setUserOnline(String online_status){

            ImageView userOnlineView = (ImageView)mView.findViewById(R.id.user_single_online_icon);

            if(online_status.equals("true")){

                userOnlineView.setVisibility(View.VISIBLE);

            }else{

                userOnlineView.setVisibility(View.INVISIBLE);

            }

        }

    }

}
