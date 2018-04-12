package com.example.azatk.sdccommunity;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by azatk on 24.10.2017.
 */

public class GroupMessageAdapter extends RecyclerView.Adapter<GroupMessageAdapter.MessageViewHolder>{

    private List<GroupMessage> mMessageList;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;
    String context;


    public GroupMessageAdapter(List<GroupMessage> messagesList) {
        this.mMessageList = messagesList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout,parent,false);



        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int i) {

        //mAuth = FirebaseAuth.getInstance();
        //String current_user_id = mAuth.getCurrentUser().getUid();

        //String from_user = c.getFrom();

        /**if( from_user.equals(current_user_id)){

            holder.messageText.setBackgroundResource(R.drawable.message_text_background_white);
            holder.messageText.setTextColor(Color.BLACK);

        }else{

            holder.messageText.setBackgroundResource(R.drawable.message_text_background);
            holder.messageText.setTextColor(Color.WHITE);

        }**/

        GroupMessage c = mMessageList.get(i);

        String from_user = c.getIdSender();
        //String message_type = c.getType();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(from_user);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("thumb_image").getValue().toString();


                holder.displayName.setText(name);

                Picasso.with(holder.profileImage.getContext()).load(image)
                        .placeholder(R.drawable.default_person).into(holder.profileImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        holder.messageTime.setText("");



            holder.messageText.setText(c.getText());
        Log.d("namespace",c.getText());


    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView messageText;
        public CircleImageView profileImage;
        public TextView displayName;
        public TextView messageTime;
        public ImageView messageImage;

        public MessageViewHolder(View itemView) {
            super(itemView);


             messageText = (TextView)itemView.findViewById(R.id.message_text_layout);
             profileImage = (CircleImageView)itemView.findViewById(R.id.message_profile_layout);
             displayName = (TextView)itemView.findViewById(R.id.name_text_layout);
             messageImage = (ImageView)itemView.findViewById(R.id.message_image_layout);
             messageTime = (TextView)itemView.findViewById(R.id.time_text_layout);

        }
    }
}
