package com.aygreyg.trill;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FeedItemAdapter extends RecyclerView.Adapter<FeedItemAdapter.ViewHolder> {
    private static final String LOG_TAG = FeedItemAdapter.class.getName();

    private ArrayList<FeedItem> mItems;
    private Context mContext;
    private int lastPosition = -1;


    FeedItemAdapter(Context context, ArrayList<FeedItem> items) {
        this.mItems = items;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.feed_item, parent, false));
    }

    @Override
    public void onBindViewHolder(FeedItemAdapter.ViewHolder holder, int position) {
        FeedItem currentItem = mItems.get(position);

        holder.bindTo(currentItem);

        if (holder.getAdapterPosition() > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.feed_animation);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mUserNameText;
        private TextView mContentText;
        private TextView mLikeCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.mUserNameText = itemView.findViewById(R.id.username);
            this.mContentText = itemView.findViewById(R.id.feed_content);
            this.mLikeCount = itemView.findViewById(R.id.like_count);

            itemView.findViewById(R.id.like_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //todo: update like count
                }
            });
        }

        public void bindTo(FeedItem currentItem) {
            this.mUserNameText.setText(currentItem.getUsername());
            this.mContentText.setText(currentItem.getContent());
            this.mLikeCount.setText(currentItem.getLikes().toString());
        }
    }
}
