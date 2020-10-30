package com.example.chat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    EditText email, emailConfirm, editPassword, username, name;
    Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        name = findViewById(R.id.editName);
        username = findViewById(R.id.editUsername);
        email = findViewById(R.id.editLoginEmail2);
        emailConfirm = findViewById(R.id.editEmailConfirm);
        editPassword = findViewById(R.id.editLoginPassword);

        registerButton = findViewById(R.id.buttonConfirm);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewUser();
            }
        });
    }

    private void saveUser(){
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            Log.i("Check","Success");
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("id", currentUser.getUid());
            hashMap.put("email", currentUser.getEmail());
            hashMap.put("username", username.getText().toString());
            hashMap.put("name", name.getText().toString());

            reference.setValue(hashMap);


            Intent intent = new Intent(this, UserList.class);
            startActivity(intent);
        }
    }

    private void createNewUser(){

        String emailString = email.getText().toString();
        String emailConfirmString = emailConfirm.getText().toString();
        String passwordString = editPassword.getText().toString();

        if(!emailString.equals(emailConfirmString)){
            Toast.makeText(this, "E-mails don't match", Toast.LENGTH_SHORT).show();
            return;
        }

        if(passwordString.length() < 6){
            Toast.makeText(this, "Password is too short", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(emailString, passwordString)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(RegisterActivity.this, "Authentication success.",
                                    Toast.LENGTH_SHORT).show();
                            saveUser();
                            Intent intentRegister = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intentRegister);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}
