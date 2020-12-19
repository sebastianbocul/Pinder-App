package com.pinder.app.models;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TagsObject {
    private String tagName;
    private String gender;
    private String mAgeMin;
    private String mAgeMax;
    private String mDistance;

    public TagsObject(String tagName, String gender, String mAgeMin, String mAgeMax, String mDistance) {
        this.tagName = tagName;
        this.gender = gender;
        this.mAgeMin = mAgeMin;
        this.mAgeMax = mAgeMax;
        this.mDistance = mDistance;
    }

    public TagsObject() {
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getmAgeMin() {
        return mAgeMin;
    }

    public void setmAgeMin(String mAgeMin) {
        this.mAgeMin = mAgeMin;
    }

    public String getmAgeMax() {
        return mAgeMax;
    }

    public void setmAgeMax(String mAgeMax) {
        this.mAgeMax = mAgeMax;
    }

    public String getmDistance() {
        return mDistance;
    }

    public void setmDistance(String mDistance) {
        this.mDistance = mDistance;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TagsObject object = (TagsObject) obj;
        boolean res = (this.tagName.equals(object.getTagName()) &&
                this.gender.equals(object.getGender()) &&
                this.mAgeMin.equals(object.getmAgeMin()) &&
                this.mAgeMax.equals(object.getmAgeMax()) &&
                this.mDistance.equals(object.getmDistance()));
        return res;
    }

    @Override
    public String toString() {
        return "TagsObject{" +
                "tagName='" + tagName + '\'' +
                ", gender='" + gender + '\'' +
                ", mAgeMin='" + mAgeMin + '\'' +
                ", mAgeMax='" + mAgeMax + '\'' +
                ", mDistance='" + mDistance + '\'' +
                '}';
    }
}
