package com.aygreyg.trill;

import com.google.firebase.Timestamp;

import java.util.ArrayList;

public class FeedItem {
    private String id;
    private String username;
    private String userid;
    private String content;
    private ArrayList<String> userids;
    private Integer likes;
    private Timestamp createdAt;

    public ArrayList<String> getUserids() {
        return userids;
    }

    public FeedItem(String username, String userid, String content, ArrayList<String> userids, Integer likes) {
        this.userid = userid;
        this.username = username;
        this.content = content;
        this.userids = userids;
        this.likes = likes;
        this.createdAt = Timestamp.now();
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public FeedItem() {
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }

    public Integer getLikes() {
        return likes;
    }

    public String getUserid() {
        return userid;
    }

    public String _getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void like(String userid) {
        this.userids.add(userid);
        this.likes += 1;
    }

    public void unlike(String userid) {
        this.userids.remove(userid);
        this.likes -= 1;
    }
}
