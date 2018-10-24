package com.ami.android.ami;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import android.content.Intent;

import com.ami.android.ami.Common.Common;
import com.ami.android.ami.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.widget.Toast.makeText;

public class LogIn extends AppCompatActivity {
    EditText username, password;
    Button signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // Init on-screen views
        password = findViewById(R.id.password);
        username = findViewById(R.id.username);
        signInButton = findViewById(R.id.signinbutton);

        // Init Firebase database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference user = database.getReference("User");

        // Event listener for sign in button
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
                    public void onClick(View view) {

                        final ProgressDialog loadingDialog = new ProgressDialog(LogIn.this);
                        loadingDialog.setMessage("One moment...");
                        loadingDialog.show();

                        user.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                loadingDialog.dismiss();
                                if (dataSnapshot.child(username.getText().toString()).exists()) { // if user exists in database
                                    User user = dataSnapshot.child(username.getText().toString()).getValue(User.class);
                                    if (user.getPassword().equals((password.getText().toString()))) { // if correct password
                                        makeText(LogIn.this, "Welcome, " + user.getName(), Toast.LENGTH_SHORT).show();
                                        Common.currentUser = user;
                                        Intent main = new Intent(LogIn.this, MainMenu.class);
                                        startActivity(main);
                                        finish(); // this prevents users from using the Android back button to return to login
                                    } else { //if incorrect password
                                        makeText(LogIn.this, R.string.wrongPasswordMessage, Toast.LENGTH_SHORT).show();
                                    }
                                } else { // if user not in database
                                    makeText(LogIn.this, "There is no account associated with that username.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) { }
                        });
            }
        });
    }
}
