package com.aygreyg.trill;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class NewPostActivity extends AppCompatActivity {
    private static final String LOG_TAG = NewPostActivity.class.getName();
    FirebaseUser user;
    EditText contentEditText;
    private FirebaseFirestore mFireStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        this.user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Log.d(LOG_TAG, "Unauthenticated user!");
            finish();
        }

        this.contentEditText = findViewById(R.id.editTextPostContent);
        this.mFireStore = FirebaseFirestore.getInstance();
    }

    public void postToFeed(View view) {
        String content = this.contentEditText.getText().toString();
        if (content.length() == 0) {
            contentEditText.setError("There must be at least 1 character worth of content!");
        }

        String username = this.user.getDisplayName();

        if (username == null || username.isEmpty()) {
            username = this.user.getEmail();
        }

        FeedItem item = new FeedItem(username, this.user.getUid(), content, new ArrayList<>(), 0);

        this.mFireStore.collection("Posts").add(item).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                finish();
            } else {
                Log.d(LOG_TAG, "Couldn't make new post!" + task.getException().getMessage());
                Toast.makeText(NewPostActivity.this, "Couldn't make post, try again later!" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void cancel(View view) {
        finish();
    }
}