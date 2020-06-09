package com.example.tinderapp.Chat;

public class ChatObject {
    private String message;
    private Boolean currentUser;
    private String profileImageUrl;

    public ChatObject(String message, Boolean currentUser, String profileImageUrl) {
        this.message = message;
        this.currentUser = currentUser;
        this.profileImageUrl = profileImageUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(Boolean currentUser) {
        this.currentUser = currentUser;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
