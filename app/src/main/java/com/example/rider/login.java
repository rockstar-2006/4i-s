package com.example.rider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class login extends AppCompatActivity {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://ambu-care-c56de-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText username = findViewById(R.id.username);
        final EditText password = findViewById(R.id.password);
        final Button buttonlog = findViewById(R.id.buttonlog);
        final TextView registerbtn = findViewById(R.id.registerbtn);
        final Button skipButton = findViewById(R.id.skip_button);

        buttonlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String usernameTxt = username.getText().toString();
                final String passwordTxt = password.getText().toString();

                if (usernameTxt.isEmpty() || passwordTxt.isEmpty()) {
                    Toast.makeText(login.this, "Please enter your mobile or password ", Toast.LENGTH_SHORT).show();
                } else {
                    databaseReference.child("Rider").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(usernameTxt)) {
                                final String getpassowrd = snapshot.child(usernameTxt).child("password").getValue(String.class);

                                if (getpassowrd.equals(passwordTxt)) {
                                    Toast.makeText(login.this, "Success fully login", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(login.this, Home.class));
                                    finish();
                                } else {
                                    Toast.makeText(login.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(login.this, "Wrong Username", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle database error
                        }
                    });
                }
            }
        });

        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login.this, register.class));
            }
        });

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login.this, Home.class)); // Redirect to Home activity
            }
        });
    }
}
