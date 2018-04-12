package com.example.azatk.sdccommunity;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatsFragment extends Fragment {

    private RecyclerView mChatsList;

    private DatabaseReference mChatsDatabase;
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mChatDelDatabase;
    private DatabaseReference mGroupAddDatabase;
    private FirebaseAuth mAuth;

    private String mCurrent_user_id;
    private FirebaseUser mCurrent_user;

    private View mMainView;

    //------------------  Group ---------------------------------------------

    private RecyclerView mGroupList;
    private DatabaseReference mGroupDatabase;

    int a=0;
    String jay;

    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_chats, container, false);
        // Inflate the layout for this fragment
        mChatsList = (RecyclerView)mMainView.findViewById(R.id.chats_list);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();
        mChatDelDatabase = FirebaseDatabase.getInstance().getReference().child("messages");
        mChatsDatabase = FirebaseDatabase.getInstance().getReference().child("messages").child(mCurrent_user_id);
        mGroupAddDatabase = FirebaseDatabase.getInstance().getReference().child("group");
        mGroupAddDatabase.keepSynced(true);
        mChatsDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mUsersDatabase.keepSynced(true);

        mChatsList.setHasFixedSize(true);
        mChatsList.setLayoutManager(new LinearLayoutManager(getContext()));

        //-----------------  Group -------------------------------

        mGroupList = (RecyclerView)mMainView.findViewById(R.id.group_list);

        mGroupDatabase = FirebaseDatabase.getInstance().getReference().child("group");
        mGroupDatabase.keepSynced(true);

        mGroupList.setHasFixedSize(true);
        mGroupList.setLayoutManager(new LinearLayoutManager(getContext()));


        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Chats, GroupViewHolder> groupRecyclerViewAdapter = new FirebaseRecyclerAdapter<Chats, GroupViewHolder>(
                Chats.class,
                R.layout.group_single_layout,
                ChatsFragment.GroupViewHolder.class,
                mGroupDatabase
        ) {
            @Override
            protected void populateViewHolder(final ChatsFragment.GroupViewHolder chatsViewHolder, final Chats chats, final int position) {

                final String list_user_id = getRef(position).getKey();

                mGroupAddDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        final String userName = dataSnapshot.child("groupInfo").child("name").getValue().toString();
                        final String groupID = dataSnapshot.child("groupInfo").child("admin").getValue().toString();
                        final String id = dataSnapshot.child("member").getValue().toString();

                        chatsViewHolder.setName(userName);
                        chatsViewHolder.image.setText((userName.charAt(0) + "").toUpperCase());


                        if(groupID.equals(mCurrent_user_id)){

                            chatsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    CharSequence options[] = new CharSequence[]{"Delete Group","Send message"};

                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setTitle("Select Options");
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int i) {

                                            if(i == 0){
                                                FirebaseDatabase.getInstance().getReference().child("group").child(list_user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                    }
                                                });
                                            }
                                            if(i == 1){

                                                Intent profileIntent = new Intent(getContext(),GroupChatActivity.class);
                                                profileIntent.putExtra("group_id",userName);
                                                startActivity(profileIntent);

                                            }


                                        }
                                    });

                                    builder.show();
                                }
                            });

                        }else {
                            chatsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    CharSequence options[] = new CharSequence[]{"Leave Group", "Send message"};

                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setTitle("Select Options");
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int i) {

                                            if (i == 0) {

                                                leaveGroup(userName);

                                            }
                                            if (i == 1) {

                                                Intent profileIntent = new Intent(getContext(), GroupChatActivity.class);
                                                profileIntent.putExtra("group_id", userName);
                                                startActivity(profileIntent);

                                            }


                                        }
                                    });

                                    builder.show();
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


        mGroupList.setAdapter(groupRecyclerViewAdapter);


        FirebaseRecyclerAdapter<Chats, ChatsViewHolder> chatsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Chats, ChatsViewHolder>(
                Chats.class,
                R.layout.users_single_layout,
                ChatsFragment.ChatsViewHolder.class,
                mChatsDatabase
        ) {
            @Override
            protected void populateViewHolder(final ChatsFragment.ChatsViewHolder chatsViewHolder, final Chats chats, int position) {


                final String list_user_id = getRef(position).getKey();


                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();

                        if(dataSnapshot.hasChild("online")){

                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            chatsViewHolder.setUserOnline(userOnline);

                        }


                        chatsViewHolder.setName(userName);
                        chatsViewHolder.setUserImage(userThumb,getContext());

                        chatsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                CharSequence options[] = new CharSequence[]{"Clean History","Send message"};

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Select Options");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {

                                        if(i == 0){

                                            mChatDelDatabase.child(mCurrent_user.getUid()).child(list_user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                }
                                            });

                                        }
                                        if(i == 1){

                                            Intent profileIntent = new Intent(getContext(),ChatActivity.class);
                                            profileIntent.putExtra("user_id",list_user_id);
                                            profileIntent.putExtra("user_name",userName);
                                            startActivity(profileIntent);

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

        mChatsList.setAdapter(chatsRecyclerViewAdapter);




        }

    public void leaveGroup(final String name){
        FirebaseDatabase.getInstance().getReference().child("group").child(name).child("member")
                .orderByValue().equalTo(mCurrent_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getValue() == null) {

                        } else {
                            String memberIndex = "";
                            ArrayList<String> result = new ArrayList<String>();
                            result.add(dataSnapshot.getValue().toString());
                            //(ArrayList<String>)dataSnapshot.getValue());
                            for(int i = 0; i < result.size(); i++){
                                if(result.get(i) != null){
                                    memberIndex = String.valueOf(i);
                                    Log.d("hvihbih",memberIndex);
                                }
                            }

                            FirebaseDatabase.getInstance().getReference().child("group/"+name+"/member")
                                    .child(memberIndex).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(),"gbdbedd",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    public static class ChatsViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public ChatsViewHolder(View itemView){
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


    public static class GroupViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public TextView image;

        public GroupViewHolder(View itemView){
            super(itemView);

            mView = itemView;
            image = mView.findViewById(R.id.user_single_image);

        }


        public void setName(String name){

            TextView userNameView = (TextView)mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);

        }


    }

}
