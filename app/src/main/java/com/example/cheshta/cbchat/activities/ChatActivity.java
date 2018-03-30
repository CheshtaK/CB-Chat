package com.example.cheshta.cbchat.activities;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.cheshta.cbchat.R;
import com.example.cheshta.cbchat.adapter.MessageAdapter;
import com.example.cheshta.cbchat.model.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    
    private SwipeRefreshLayout srlChat;
    private RecyclerView rvMessages;
    private ImageButton btnAdd, btnSend;
    private EditText etMessage;

    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;
    private String currentUser, chatUser;

    private final List<Message> messages = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        
        srlChat = findViewById(R.id.srlChat);
        rvMessages = findViewById(R.id.rvMessages);
        btnAdd = findViewById(R.id.btnAdd);
        btnSend = findViewById(R.id.btnSend);
        etMessage = findViewById(R.id.etMessage);

        mUserDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();
        chatUser = "CB";

        layoutManager = new LinearLayoutManager(this);
        messageAdapter = new MessageAdapter(this, messages, currentUser);

        rvMessages.setHasFixedSize(true);
        rvMessages.setLayoutManager(layoutManager);
        rvMessages.setAdapter(messageAdapter);

        loadMessages();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    private void loadMessages(){

        DatabaseReference messageRef = mUserDatabase.child("messages").child(currentUser).child(chatUser);
        messageRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);

                messages.add(message);
                messageAdapter.notifyDataSetChanged();
                rvMessages.scrollToPosition(messages.size() - 1);
                srlChat.setRefreshing(false);
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

    private void sendMessage(){

        String message = etMessage.getText().toString();

        if(!TextUtils.isEmpty(message)){

            String current_user_ref = "messages/" + currentUser + "/" + chatUser;
            String chat_user_ref = "messages/" + chatUser + "/" + currentUser;

            DatabaseReference user_message_push = mUserDatabase.child("messages")
                    .child(currentUser).child(chatUser).push();

            String push_id = user_message_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", currentUser);

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

            etMessage.setText("");

            mUserDatabase.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if(databaseError != null){
                        Log.d("CHAT_LOG", databaseError.getMessage().toString());
                    }
                }
            });
        }
    }
}
