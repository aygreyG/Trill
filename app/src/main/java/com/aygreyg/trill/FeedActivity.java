package com.aygreyg.trill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class FeedActivity extends AppCompatActivity {
    private static final String LOG_TAG = FeedActivity.class.getName();
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    private RecyclerView mRecyclerView;
    private ArrayList<FeedItem> mPostsList;
    private FeedItemAdapter mFeedItemAdapter;

    private FirebaseFirestore mFireStore;
    private CollectionReference mPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Log.d(LOG_TAG, "Unauthenticated user!");
            finish();
        }

        this.mRecyclerView = findViewById(R.id.recycleView);
        this.mRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        this.mPostsList = new ArrayList<>();
        this.mFeedItemAdapter = new FeedItemAdapter(this, mPostsList);
        this.mRecyclerView.setAdapter(mFeedItemAdapter);

        mFireStore = FirebaseFirestore.getInstance();
        mPosts = mFireStore.collection("Posts");

        SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);

        pullToRefresh.setOnRefreshListener(() -> {
            queryData();
            pullToRefresh.setRefreshing(false);
        });

        queryData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");
        queryData();
    }

    private void queryData() {
        mPosts.orderBy("userids", Query.Direction.DESCENDING)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            mPostsList.clear();
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                FeedItem item = documentSnapshot.toObject(FeedItem.class);
                item.setId(documentSnapshot.getId());
                mPostsList.add(item);
            }
            mFeedItemAdapter.notifyDataSetChanged();
        });
    }

    public void updateLikes(FeedItem item) {
        String userId = user.getUid();
        if (userId.equals(item.getUserid()) || item._getId() == null) {
            return;
        }

        if (item.getUserids().contains(userId)) {
            item.unlike(userId);
        } else {
            item.like(userId);
        }

        mPosts.document(item._getId()).update("userids", item.getUserids(), "likes", item.getLikes()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                queryData();
            }
        });
    }

    public void deletePost(FeedItem item) {

        mPosts.document(item._getId()).delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(FeedActivity.this, "Successfully deleted post!", Toast.LENGTH_LONG).show();
                queryData();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.feed_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            case R.id.post_button:
                Intent postIntent = new Intent(this, NewPostActivity.class);
                startActivity(postIntent);
                return true;
            case R.id.changeUsername:
                Intent changeUsernameIntent = new Intent(this, ChangeUserNameActivity.class);
                startActivity(changeUsernameIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}