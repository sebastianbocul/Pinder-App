package com.pinder.app.Tags;

public class TagsPopularObject {
    private String tagName;
    private int tagPopularity;

    public TagsPopularObject(String tagName, int tagPopularity) {
        this.tagName = tagName;
        this.tagPopularity = tagPopularity;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public int getTagPopularity() {
        return tagPopularity;
    }

    public void setTagPopularity(int tagPopularity) {
        this.tagPopularity = tagPopularity;
    }
}
