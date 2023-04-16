package com.aygreyg.trill;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final int SECRET_KEY = 11;
    private static final int RC_SIGN_IN = 123;

    EditText emailEditText;
    EditText passwordEditText;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);

        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGSignInClient = GoogleSignIn.getClient(this, gso);

        FirebaseUser user = this.mAuth.getCurrentUser();

        if (user != null) {
            Intent intent = new Intent(this, FeedActivity.class);
            startActivity(intent);
        }
    }

    public void login(View view) {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (!checkInputs(email, password)) {
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Log.d(LOG_TAG, "User successfully logged in! ");
                Toast.makeText(MainActivity.this, "Successfully logged in! ", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, FeedActivity.class);
                startActivity(intent);
            } else {
                Log.d(LOG_TAG, "User couldn't log in! " + task.getException().getMessage());
                Toast.makeText(MainActivity.this, "Couldn't log in! " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean checkInputs(String email, String password) {
        if (emailEditText.length() == 0) {
            emailEditText.setError("You must provide an email!");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("You must provide a valid email!");
            return false;
        }

        if (passwordEditText.length() == 0) {
            passwordEditText.setError("You must provide a password!");
            return false;
        }

        return true;
    }

    public void openRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
    }

    public void loginWithGoogle(View view) {
        Intent signInIntent = mGSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                FirebaseAuthWithGoogle(account.getIdToken(), account.getDisplayName());
            } catch (ApiException e) {
                Log.e(LOG_TAG, "Google sign in failed", e);
            }
        }
    }

    private void FirebaseAuthWithGoogle(String idToken, String accountName) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Log.d(LOG_TAG, "User successfully logged in! ");
                Toast.makeText(MainActivity.this, "Successfully logged in! ", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, FeedActivity.class);
                startActivity(intent);
            } else {
                Log.d(LOG_TAG, "User couldn't log in! " + task.getException().getMessage());
                Toast.makeText(MainActivity.this, "Couldn't log in! " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}