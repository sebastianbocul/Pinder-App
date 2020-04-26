package com.example.tinderapp.Cards;

public class cards {
    private String userId;
    private String name;
    private String profileImageUrl;
    private String tags;

    public cards (String userId,String name, String profileImageUrl,String tags){
        this.userId = userId;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.tags = tags;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
