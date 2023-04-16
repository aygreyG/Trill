package com.aygreyg.trill;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {
    private static final String LOG_TAG = RegisterActivity.class.getName();
    private static final int SECRET_KEY = 11;

    EditText userNameEditText;
    EditText userEmailEditText;
    EditText passwordEditText;
    EditText passwordConfirmEditText;

    private FirebaseAuth mAuth;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);

        if (secret_key != SECRET_KEY) {
            finish();
        }

        userNameEditText = findViewById(R.id.editTextUserName);
        userEmailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        passwordConfirmEditText = findViewById(R.id.editTextPasswordAgain);

        mAuth = FirebaseAuth.getInstance();
    }

    public void cancel(View view) {
        finish();
    }

    public void register(View view) {
        String userName = userNameEditText.getText().toString();
        String email = userEmailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (checkInputs()) {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    Log.d(LOG_TAG, "Registered: " + userName + ", e-mail: " + email);
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(userName)
                            .build();

                    Toast.makeText(RegisterActivity.this, "User successfully created!", Toast.LENGTH_LONG).show();

                    task.getResult().getUser().updateProfile(profileUpdates).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Log.d(LOG_TAG, "User updated with username!");
                            finish();
                        } else {
                            Log.d(LOG_TAG, "User couldn't be updated with username! " + task.getException().getMessage());
                            finish();
                        }
                    });
                } else {
                    Log.d(LOG_TAG, "User couldn't be created! " + task.getException().getMessage());
                    Toast.makeText(RegisterActivity.this, "User couldn't be created!", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private boolean checkInputs() {
        if (userNameEditText.length() == 0) {
            userNameEditText.setError("You must provide a username!");
            return false;
        }

        if (userEmailEditText.length() == 0) {
            userEmailEditText.setError("You must provide an email!");
            return false;
        }

        String email = userEmailEditText.getText().toString();
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            userEmailEditText.setError("You must provide a valid email!");
            return false;
        }

        if (passwordEditText.length() == 0) {
            passwordEditText.setError("You must provide a password!");
            return false;
        }

        if (passwordEditText.getText().toString().length() < 6) {
            passwordEditText.setError("The password must be at least 6 characters long!");
            return false;
        }

        if (passwordConfirmEditText.length() == 0) {
            passwordConfirmEditText.setError("You must provide the password confirmation!");
            return false;
        }

        String password = passwordEditText.getText().toString();
        String passwordConfirm = passwordConfirmEditText.getText().toString();

        if (!password.equals(passwordConfirm)) {
            passwordConfirmEditText.setError("The password and confirmation should be the same!");
            return false;
        }

        return true;
    }
}
