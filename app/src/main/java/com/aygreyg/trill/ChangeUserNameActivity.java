package com.aygreyg.trill;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

public class ChangeUserNameActivity extends AppCompatActivity {

    private static final String LOG_TAG = ChangeUserNameActivity.class.getName();
    FirebaseUser user;
    EditText newUsernameEditText;
    FirebaseFirestore mFireStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_name);

        this.user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Log.d(LOG_TAG, "Unauthenticated user!");
            finish();
        }

        this.newUsernameEditText = findViewById(R.id.newUsername);
        this.mFireStore = FirebaseFirestore.getInstance();
    }

    public void changeUsername(View view) {
        String newUsername = this.newUsernameEditText.getText().toString();

        if (newUsername.isEmpty() || newUsername.trim().equals("")) {
            this.newUsernameEditText.setError("You must give a proper username!");
            return;
        }

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(newUsername)
                .build();

        this.user.updateProfile(profileUpdates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                Query query = this.mFireStore.collection("Posts").whereEqualTo("userid", this.user.getUid());
                WriteBatch batch = this.mFireStore.batch();

                query.get().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task1.getResult()){
                            DocumentReference ref = document.getReference();
                            batch.update(ref, "username", newUsername);
                        }

                        batch.commit();
                    }
                });


                Toast.makeText(ChangeUserNameActivity.this, "Username successfully changed!", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(ChangeUserNameActivity.this, "Username couldn't be changed!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void cancel(View view) {
        finish();
    }
}