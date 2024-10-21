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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class register extends AppCompatActivity {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Rider");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText fullname = findViewById(R.id.fullname);
        final EditText username = findViewById(R.id.username);
        final EditText email = findViewById(R.id.email);
        final EditText contactno = findViewById(R.id.contactno);
        final EditText password = findViewById(R.id.password);
        final EditText conpassword = findViewById(R.id.conpaswword);

        final Button registerbtn = findViewById(R.id.buttonRegister);
        final TextView loginbtn = findViewById(R.id.loginnow);

        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String fullnameTxt = fullname.getText().toString();
                final String usernameTxt = username.getText().toString();
                final String emailTxt = email.getText().toString();
                final String contactnoTxt = contactno.getText().toString();
                final String passwordTxt = password.getText().toString();
                final String conpasswordTxt = conpassword.getText().toString();

                if (fullnameTxt.isEmpty() || usernameTxt.isEmpty() || emailTxt.isEmpty() || contactnoTxt.isEmpty() || conpasswordTxt.isEmpty()) {
                    Toast.makeText(register.this, "Please fill the form", Toast.LENGTH_SHORT).show();
                } else if (!passwordTxt.equals(conpasswordTxt)) {
                    Toast.makeText(register.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                } else {
                    databaseReference.child(usernameTxt).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Toast.makeText(register.this, "User is already registered", Toast.LENGTH_SHORT).show();
                            } else {
                                // Create a User object
                                user user = new user(fullnameTxt, usernameTxt, emailTxt, contactnoTxt, passwordTxt);

                                // Save data to Firebase
                                databaseReference.child(usernameTxt).setValue(user).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(register.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                        finish(); // Go back to the previous screen
                                    } else {
                                        Toast.makeText(register.this, "Registration failed. Please try again", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle database error
                            Toast.makeText(register.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(register.this, login.class));
                finish();
            }
        });
    }
}
