package com.aygreyg.trill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

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
    private ArrayList<FeedItem> mItemList;
    private FeedItemAdapter mFeedItemAdapter;

    private FirebaseFirestore mFireStore;
    private CollectionReference mItems;

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
        this.mItemList = new ArrayList<>();
        this.mFeedItemAdapter = new FeedItemAdapter(this, mItemList);
        this.mRecyclerView.setAdapter(mFeedItemAdapter);

        mFireStore = FirebaseFirestore.getInstance();
        mItems = mFireStore.collection("Posts");

        queryData();
    }

    private void queryData() {
        mItemList.clear();

        mItems.orderBy("likes", Query.Direction.DESCENDING).get().addOnSuccessListener(queryDocumentSnapshots -> {
           for (QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots) {
               FeedItem item = documentSnapshot.toObject(FeedItem.class);
               mItemList.add(item);
           }
            mFeedItemAdapter.notifyDataSetChanged();
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
                //todo: new activity
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}