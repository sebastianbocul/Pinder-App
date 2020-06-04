package com.example.tinderapp.Cards;

import java.util.Map;

public class cards {
    private String userId;
    private String name;
    private String profileImageUrl;
    private String tags;
    private Map mutualTagsMap;
    private double distance;
    private boolean likesMe;

    public cards (String userId,String name, String profileImageUrl,String tags,Map mutualTagsMap,double distance,boolean likesMe){
        this.userId = userId;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.tags = tags;
        this.mutualTagsMap = mutualTagsMap;
        this.distance=distance;
        this.likesMe=likesMe;
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

    public Map getMutualTagsMap() {
        return mutualTagsMap;
    }

    public void setMutualTagsMap(Map mutualTagsMap) {
        this.mutualTagsMap = mutualTagsMap;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public boolean isLikesMe() {
        return likesMe;
    }

    public void setLikesMe(boolean likesMe) {
        this.likesMe = likesMe;
    }

}
