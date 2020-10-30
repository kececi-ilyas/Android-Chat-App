package com.example.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;

public class Chatting extends AppCompatActivity {
    private Button bttnLogin, bttnRegister;

    private FirebaseAuth mAuth;

    public void init() {
        bttnLogin = (Button) findViewById(R.id.bttnLogin);
        bttnRegister = (Button) findViewById(R.id.bttnRegister);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        init();
        getSupportActionBar().setTitle("Chat App");

        bttnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentLogin = new Intent(Chatting.this, LoginActivity.class);

                startActivity(intentLogin);
            }

        });
        bttnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentRegister = new Intent(Chatting.this, RegisterActivity.class);

                startActivity(intentRegister);
            }
        });


        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
}
