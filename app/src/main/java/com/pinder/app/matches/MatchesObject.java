package com.pinder.app.matches;

import java.util.ArrayList;

public class MatchesObject {
    private String userId;
    private String name;
    private String profileImageUrl;
    private String lastMessage;
    private boolean createdByMe;
    private String sortId;
    private ArrayList<String> mutualTags;

    public MatchesObject(String userId, String name, String profileImageUrl, String lastMessage, boolean createdByMe, String sortId, ArrayList<String> mutualTags) {
        this.userId = userId;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.lastMessage = lastMessage;
        this.createdByMe = createdByMe;
        this.sortId = sortId;
        this.mutualTags = mutualTags;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public boolean isCreatedByMe() {
        return createdByMe;
    }

    public void setCreatedByMe(boolean createdByMe) {
        this.createdByMe = createdByMe;
    }

    public String getSortId() {
        return sortId;
    }

    public void setSortId(String sortId) {
        this.sortId = sortId;
    }

    public ArrayList<String> getMutualTags() {
        return mutualTags;
    }

    public void setMutualTags(ArrayList<String> mutualTags) {
        this.mutualTags = mutualTags;
    }
}
