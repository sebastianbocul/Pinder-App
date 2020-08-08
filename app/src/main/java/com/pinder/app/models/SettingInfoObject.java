package com.pinder.app.models;

public class SettingInfoObject {
    String date;
    Boolean showMyLocation;
    Boolean sortByDistance;

    public SettingInfoObject() {
    }

    public SettingInfoObject(String date, boolean showMyLocation, boolean sortByDistance) {
        this.date = date;
        this.showMyLocation = showMyLocation;
        this.sortByDistance = sortByDistance;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean getShowMyLocation() {
        return showMyLocation;
    }

    public void setShowMyLocation(boolean showMyLocation) {
        this.showMyLocation = showMyLocation;
    }

    public boolean getSortByDistance() {
        return sortByDistance;
    }

    public void setSortByDistance(boolean sortByDistance) {
        this.sortByDistance = sortByDistance;
    }
}
