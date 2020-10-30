package com.example.chat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class UserList extends AppCompatActivity {

    LinearLayout usersLayout;

    private FirebaseUser currentUser;
    List<User> users = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        getUsers();
        getSupportActionBar().setTitle("Registered Users");


        usersLayout = findViewById(R.id.usersLayout);

        Window w = getWindow();
        w.setTitle("Users");
    }

    private void getUsers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                users.clear();
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    User user = dataSnapshot1.getValue(User.class);
                    if(!user.getId().equals(currentUser.getUid())){
                        Log.i("Users", dataSnapshot1.getValue().toString());
                        users.add(dataSnapshot1.getValue(User.class));
                    }
                }
                createUserList();

                /*Intent intent = new Intent(MainActivity.this, Room.class);
                intent.putExtra("userID", users.get(0).getId());
                startActivity(intent);*/
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void createUserList(){
        usersLayout.removeAllViews();

        for(int i = 0; i < users.size(); i++){
            final int usernumber = i;
            TextView view = new TextView(this);
            view.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
            view.setHeight(120);
            view.setTextSize(20);
            view.setText(users.get(i).getName());
            view.setGravity(Gravity.CENTER);
            view.setTextAlignment(RelativeLayout.TEXT_ALIGNMENT_VIEW_START);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(UserList.this, Room.class);
                    intent.putExtra("userID", users.get(usernumber).getId());
                    intent.putExtra("userName", users.get(usernumber).getName());
                    startActivity(intent);
                }
            });
            usersLayout.addView(view);
        }

    }

}
