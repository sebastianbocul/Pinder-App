package com.pinder.app.models;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Card implements Parcelable {
    private String userId;
    private String name;
    private String profileImageUrl;
    private List<String> images;
    private String gender;
    private String dateOfBirth;
    private String tags;
    private Map mutualTagsMap;
    private double distance;
    private String location;
    private boolean likesMe;
    private String description;

    public Card(String userId, String name, String profileImageUrl, List<String> images, String gender, String dateOfBirth, String tags, Map mutualTagsMap, double distance, String location, boolean likesMe, String description) {
        this.userId = userId;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.images = images;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.tags = tags;
        this.mutualTagsMap = mutualTagsMap;
        this.distance = distance;
        this.location = location;
        this.likesMe = likesMe;
        this.description = description;
    }

    public Card() {
    }

    protected Card(Parcel in) {
        userId = in.readString();
        name = in.readString();
        profileImageUrl = in.readString();
        images = in.createStringArrayList();
        gender = in.readString();
        dateOfBirth = in.readString();
        tags = in.readString();
        distance = in.readDouble();
        location = in.readString();
        likesMe = in.readByte() != 0;
        description = in.readString();
    }

    public static final Creator<Card> CREATOR = new Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel in) {
            return new Card(in);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };

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

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isLikesMe() {
        return likesMe;
    }

    public void setLikesMe(boolean likesMe) {
        this.likesMe = likesMe;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return Double.compare(card.distance, distance) == 0 &&
                likesMe == card.likesMe &&
                Objects.equals(userId, card.userId) &&
                Objects.equals(name, card.name) &&
                Objects.equals(profileImageUrl, card.profileImageUrl) &&
                Objects.equals(images, card.images) &&
                Objects.equals(gender, card.gender) &&
                Objects.equals(dateOfBirth, card.dateOfBirth) &&
                Objects.equals(tags, card.tags) &&
                Objects.equals(mutualTagsMap, card.mutualTagsMap) &&
                Objects.equals(location, card.location) &&
                Objects.equals(description, card.description);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(userId, name, profileImageUrl, images, gender, dateOfBirth, tags, mutualTagsMap, distance, location, likesMe, description);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(name);
        dest.writeString(profileImageUrl);
        dest.writeStringList(images);
        dest.writeString(gender);
        dest.writeString(dateOfBirth);
        dest.writeString(tags);
        dest.writeDouble(distance);
        dest.writeString(location);
        dest.writeByte((byte) (likesMe ? 1 : 0));
        dest.writeString(description);
    }
}
