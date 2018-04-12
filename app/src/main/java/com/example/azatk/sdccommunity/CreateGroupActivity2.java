package com.example.azatk.sdccommunity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashSet;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateGroupActivity2 extends AppCompatActivity {


    private RecyclerView mFriendsList;
    private TextView txtGroupIcon;
    private EditText editTextGroupName;
    private LinearLayout btnAddGroup;
    private Set<String> listIDChoose;
    private Set<String> listIDRemove;
    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUsersDatabase;
    private FirebaseAuth mAuth;
    private boolean isEditGroup;
    private Finder id;

    private String mCurrent_user_id;
    String list_user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        mFriendsList = (RecyclerView) findViewById(R.id.recycleListFriend);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        listIDChoose = new HashSet<>();
        listIDRemove = new HashSet<>();
        listIDChoose.add(mCurrent_user_id);

        id = new Finder();

        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        mFriendsDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mUsersDatabase.keepSynced(true);

        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(this));

        btnAddGroup = (LinearLayout) findViewById(R.id.btnAddGroup);
        editTextGroupName = (EditText) findViewById(R.id.editGroupName);
        txtGroupIcon = (TextView) findViewById(R.id.icon_group);
        editTextGroupName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() >= 1) {

                    txtGroupIcon.setText((charSequence.charAt(0) + "").toUpperCase());

                } else {
                    txtGroupIcon.setText("R");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (listIDChoose.size() < 2 && editTextGroupName.getText().length() > 0) {

                    Toast.makeText(CreateGroupActivity2.this, "Add at lease two people to create group", Toast.LENGTH_SHORT).show();

                } else {

                    if (editTextGroupName.getText().length() == 0) {

                        Toast.makeText(CreateGroupActivity2.this, "Enter group name", Toast.LENGTH_SHORT).show();

                    } else {

                        createGroup();

                    }
                }
            }
        });



    }

    private void createGroup() {

        Room room = new Room();
        for (String id : listIDChoose) {
            room.member.add(id);
        }
        room.groupInfo.put("name", editTextGroupName.getText().toString());
        room.groupInfo.put("admin", mCurrent_user_id);
        room.groupInfo.put("vis", "invis");

        FirebaseDatabase.getInstance().getReference().child("group/" + editTextGroupName.getText()).setValue(room)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Intent mainIntent = new Intent(CreateGroupActivity2.this,MainActivity.class);
                        startActivity(mainIntent);


                    }
                });


    }


    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerAdapter<Friends, CreateGroupActivity2.FriendsViewHolder> friendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Friends, CreateGroupActivity2.FriendsViewHolder>(
                Friends.class,
                R.layout.rc_item_add_friend,
                CreateGroupActivity2.FriendsViewHolder.class,
                mFriendsDatabase
        ) {
            @Override
            protected void populateViewHolder(final CreateGroupActivity2.FriendsViewHolder friendsViewHolder, final Friends friends, int position) {

                final String list_user_id = getRef(position).getKey();

                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                        String userStatus = dataSnapshot.child("status").getValue().toString();

                        friendsViewHolder.setName(userName);
                        friendsViewHolder.setUserImage(userThumb, CreateGroupActivity2.this);
                        friendsViewHolder.setDate(userStatus);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                friendsViewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            listIDChoose.add(list_user_id);
                        } else {
                            listIDChoose.remove(list_user_id);
                        }

                        if (listIDChoose.size() >= 1) {
                            btnAddGroup.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimary));
                        } else {
                            btnAddGroup.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.grey_500));
                        }


                    }
                });

            }
        };

        mFriendsList.setAdapter(friendsRecyclerViewAdapter);

    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public CheckBox checkBox;

        public FriendsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            checkBox = (CheckBox) itemView.findViewById(R.id.checkAddPeople);

        }

        public void setDate(String date) {

            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_status);
            userNameView.setText(date);

        }

        public void setName(String name) {

            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);

        }

        public void setUserImage(String thumb_image, Context context) {

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);

            Picasso.with(context).load(thumb_image).placeholder(R.drawable.default_person).into(userImageView);

        }


    }


}
