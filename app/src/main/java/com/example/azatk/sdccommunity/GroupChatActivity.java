package com.example.azatk.sdccommunity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GroupChatActivity extends AppCompatActivity {

    private String mGroupID;
    private Toolbar mChatToolbar;
    private EditText mText;
    private ImageButton mChatSendBtn;

    private FirebaseAuth mAuth;
    private String mCurrentUserId;

    private List<GroupMessage> messagesList = new ArrayList<>();
    private LinearLayoutManager mLinearlayout;
    private GroupMessageAdapter mAdapter;

    private DatabaseReference mRootRef;

    private RecyclerView mMessagesList;

    private TextView title;
    private LinearLayout linearLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        mChatToolbar = (Toolbar)findViewById(R.id.chat_app_bar);
        setSupportActionBar(mChatToolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle(null);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();

        linearLayout = (LinearLayout)findViewById(R.id.linearLayout);

        mGroupID = getIntent().getStringExtra("group_id");
        title = (TextView)findViewById(R.id.textView4);
        title.setText(mGroupID);


        mRootRef = FirebaseDatabase.getInstance().getReference();

        mRootRef.child("group").child(mGroupID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String userStatus = dataSnapshot.child("groupInfo").child("vis").getValue().toString();
                String userStat = dataSnapshot.child("groupInfo").child("admin").getValue().toString();
                Log.d("kjjknjknjk",userStatus);

                if(userStatus.equals("invis") && userStat.equals(mCurrentUserId)){

                    linearLayout.setVisibility(View.VISIBLE);

                }else if(userStatus.equals("invis") && !userStat.equals(mCurrentUserId)){

                    linearLayout.setVisibility(View.INVISIBLE);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mChatSendBtn = (ImageButton)findViewById(R.id.chat_send_btn);
        mText = (EditText)findViewById(R.id.chat_message_view);

        mChatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String content = mText.getText().toString().trim();
                if (content.length() > 0) {
                    mText.setText("");
                    GroupMessage newMessage = new GroupMessage();
                    newMessage.text = content;
                    newMessage.idSender = mCurrentUserId;
                    newMessage.idReceiver = mGroupID;
                    newMessage.timestamp = System.currentTimeMillis();
                    FirebaseDatabase.getInstance().getReference().child("messages/"  + mGroupID ).push().setValue(newMessage);
                }

            }
        });

        mAdapter = new GroupMessageAdapter(messagesList);

        mMessagesList = (RecyclerView)findViewById(R.id.messages_list);
        mLinearlayout = new LinearLayoutManager(this);

        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mLinearlayout);

        mMessagesList.setAdapter(mAdapter);


        loadMessages();



    }

    private void loadMessages() {

        DatabaseReference messageRef  = mRootRef.child("messages").child(mGroupID);

        Query messageQuery = messageRef.limitToLast(1* 10);

        messageRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                GroupMessage message = dataSnapshot.getValue(GroupMessage.class);
                /*message = new GroupMessage();

                message.text = dataSnapshot.child("text").getValue().toString();
                message.idReceiver = dataSnapshot.child("idReceiver").getValue().toString();
                message.idSender = dataSnapshot.child("idSender").getValue().toString();
                message.timestamp = (long) dataSnapshot.child("timestamp").getValue();*/

                messagesList.add(message);
                mAdapter.notifyDataSetChanged();


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
