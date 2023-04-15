package com.aygreyg.trill;

public class FeedItem {
    private String username;
    private String content;
    private Integer likes;

    public FeedItem(String username, String content, int likes) {
        this.username = username;
        this.content = content;
        this.likes = likes;
    }

    public FeedItem() {}

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }

    public Integer getLikes() {
        return likes;
    }
}
