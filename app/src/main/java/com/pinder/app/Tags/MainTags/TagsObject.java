package com.pinder.app.Tags.MainTags;

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
}
