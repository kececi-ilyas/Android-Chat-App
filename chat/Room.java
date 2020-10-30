package com.example.chat;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class Room extends AppCompatActivity {


    private ScrollView scrollView;
    private LinearLayout layout;

    private Button sendButton;
    private EditText messageContent;

    private TextView userName;

    private String userToMessageID;
    private String userToMessageName;
    User userToMessage;

    //Messages
    List<Message> sendMessages = new ArrayList<>();
    List<Message> receiveMessages = new ArrayList<>();

    FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();
        userToMessageID = intent.getStringExtra("userID");
        userToMessageName = intent.getStringExtra("userName");


        getSupportActionBar().setTitle(userToMessageName);

        Log.i("Room", "ID: " + userToMessageID +  "   " + userToMessageName);

        scrollView = findViewById(R.id.roomScrollView);
        layout = findViewById(R.id.roomMessagesLayout);
        sendButton = findViewById(R.id.roomSendButton);
        messageContent = findViewById(R.id.roomMessageArea);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(messageContent.getText().toString());
                messageContent.setText("");
            }
        });

        setUser();
    }

    private void setUser(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    User user = dataSnapshot1.getValue(User.class);
                    Log.i("Room", "Users get: " + user.getId());
                    if(user.getId().equals(userToMessageID)){
                        userToMessage = user;
                        Log.i("Room", "Users get2");
                        getMessages();

                        break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getMessages(){
        DatabaseReference sentMessages = FirebaseDatabase.getInstance().getReference("Chat");

        String sentMessagesId = userToMessage.getId() + "-"+currentUser.getUid();

        sentMessages.child(sentMessagesId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                sendMessages.clear();
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    Message message = dataSnapshot1.getValue(Message.class);
                    message.yours = true;
                    sendMessages.add(message);
                }

                Log.i("Message", "Sent: " + sendMessages.size());
                updateMessages();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference receivedMessages = FirebaseDatabase.getInstance().getReference("Chat");
        String receivedMessagesId = currentUser.getUid() + "-"+userToMessage.getId();
        receivedMessages.child(receivedMessagesId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                receiveMessages.clear();
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    Message message = dataSnapshot1.getValue(Message.class);
                    receiveMessages.add(message);
                }
                Log.i("Message", "Received: " + receiveMessages.size());
                updateMessages();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String message){
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        String roomName = userToMessage.id+"-"+currentUser.getUid();
        DatabaseReference myRef = database.getReference("Chat");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", currentUser.getUid());
        hashMap.put("receiver", userToMessage.id);
        hashMap.put("content", message);
        hashMap.put("number", sendMessages.size() + receiveMessages.size());

        myRef.child(roomName).push().setValue(hashMap);
    }

    private void updateMessages(){
        List<Message> allMessages = new ArrayList<>();
        allMessages.addAll(sendMessages);
        allMessages.addAll(receiveMessages);
        sortMessages(allMessages);

        layout.removeAllViews();

        for(int i = 0; i < allMessages.size(); i++){
            TextView messageView = new TextView(layout.getContext());

            messageView.setWidth(layout.getWidth());

            if(allMessages.get(i).yours){
                messageView.setTextAlignment(RelativeLayout.TEXT_ALIGNMENT_TEXT_END);
            }

            messageView.setText(allMessages.get(i).getContent());
            layout.addView(messageView);
        }

        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    private void sortMessages(List<Message> messages){
        Collections.sort(messages, new Comparator<Message>() {
            @Override
            public int compare(Message m1, Message m2) {
                return m1.getNumber() - m2.getNumber();
            }
        });
    }
}
