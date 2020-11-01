package com.pinder.app.util;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.pinder.app.models.Card;
import com.pinder.app.models.TagsObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ValidateUserByPreferences {
    private static final String TAG = "ValidateUserByPreferenc";

    public static Card validateUserByPreferences(DataSnapshot userDatasnapshot , Boolean likesMe, LatLng myLocation, ArrayList<TagsObject> myTagsList, Map<String, String> myInfo) {
        Card card = null;
//        Log.d(TAG, "validateUserByPreferences: datasnapshot:" + userDatasnapshot.toString());
        Log.d(TAG, "validateUserByPreferences: likesMe:" + likesMe);
        Log.d(TAG, "validateUserByPreferences: myLocation:" + myLocation);
//        for(TagsObject t:myTagsList){
//            Log.d(TAG, "validateUserByPreferences: tag:  : " + t.toString());
//        }
        Log.d(TAG, "validateUserByPreferences: myTagsList:" + myTagsList);
        Log.d(TAG, "validateUserByPreferences: myInfo:" + myInfo);
        ArrayList<String> mutualTagsList = new ArrayList<>();
        StringBuilder mutualTagsStringBuilder = new StringBuilder();
        Map<Object, Object> tagsMap = new HashMap<>();
        try {
            Log.d(TAG, "User Name " + userDatasnapshot.child("name").getValue().toString() + " uid: " + userDatasnapshot.getKey());
            int age = new StringDateToAge().stringDateToAge(userDatasnapshot.child("dateOfBirth").getValue().toString());
            Log.d(TAG, "age: " + age);
            int myAge = Integer.parseInt(myInfo.get("age"));
            Log.d(TAG, "myAge: " + myAge);
            String mySex = myInfo.get("sex");
            double latitude = Double.parseDouble(userDatasnapshot.child("location").child("latitude").getValue().toString());
            Log.d(TAG, "latitude: " + latitude);
            double longitude = Double.parseDouble(userDatasnapshot.child("location").child("longitude").getValue().toString());
            Log.d(TAG, "longitude: " + longitude);
            double distanceDouble = CalculateDistance.distance(myLocation.latitude, myLocation.longitude, latitude, longitude);
            Log.d(TAG, "distanceDouble" + distanceDouble);
            //VALIDATE USER
            for (DataSnapshot dataTag: userDatasnapshot.child("tags").getChildren()) {
                Log.d(TAG, "forFirst ");
                for (TagsObject tag : myTagsList) {
                    ///VALIDATING TAGS BY MY PREFERENCES
                    //comparing tags
                    Log.d(TAG, "1st if: " + dataTag.getKey() + " == " + tag.getTagName());
                    if (dataTag.getKey().equals(tag.getTagName())) {
                        //validating my gender preferences
                        Log.d(TAG, "2nd if: " + tag.getGender() + " == " + userDatasnapshot.child("sex").getValue().toString() + "  ||  " + tag.getGender() + " == Any");
                        if (tag.getGender().equals(userDatasnapshot.child("sex").getValue().toString()) || tag.getGender().equals("Any")) {
                            Log.d(TAG, "3rd if: " + dataTag.child("gender").getValue().toString() + " == " + myInfo.get("sex") + "  ||  " + dataTag.child("gender").getValue().toString() + " == Any");
                            //validating user gender preferences
                            if (dataTag.child("gender").getValue().toString().equals(mySex) || dataTag.child("gender").getValue().toString().equals("Any")) {
                                Log.d(TAG, "4th if: " + tag.getmAgeMin() + " <= " + age + "  &&  " + tag.getmAgeMax() + " >= " + age);
                                //validating myTag age preferences with minAge and maxAge
                                if (Integer.parseInt(tag.getmAgeMin()) <= age && Integer.parseInt(tag.getmAgeMax()) >= age) {
                                    Log.d(TAG, "5th if: " + dataTag.child("minAge").getValue().toString() + " <= " + myAge + "  &&  " + dataTag.child("maxAge").getValue().toString() + " >= " + myAge);
                                    //validating userTag age preferences with minAge and maxAge
                                    if (Integer.parseInt(dataTag.child("minAge").getValue().toString()) <= myAge && Integer.parseInt(dataTag.child("maxAge").getValue().toString()) >= myAge) {
                                        Log.d(TAG, "6th if: " + tag.getmDistance() + " >= " + distanceDouble);
                                        //validating myTag distance preference
                                        if (Double.parseDouble(tag.getmDistance()) >= distanceDouble) {
                                            //validate userTag distance preference
                                            Log.d(TAG, "7th if: " + dataTag.child("maxDistance").getValue().toString() + " >= " + distanceDouble);
                                            if (Double.parseDouble(dataTag.child("maxDistance").getValue().toString()) >= distanceDouble) {
                                                ///CAN VALIDATE OTHER USER PREFERENCES
                                                Log.d(TAG, "USER VALIDATE PASS: ");
                                                mutualTagsList.add(tag.getTagName());
                                                tagsMap.put(tag.getTagName(), true);
                                                mutualTagsStringBuilder.append("#" + tag.getTagName() + " ");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            //CREATE CARD ITEM
            if (mutualTagsList.size() != 0) {
                String profileImageUrl = "default";
                if (userDatasnapshot.child("profileImageUrl").exists()) {
                    if (!userDatasnapshot.child("profileImageUrl").getValue().toString().equals("default")) {
                        profileImageUrl = userDatasnapshot.child("profileImageUrl").getValue().toString();
                    }
                } else profileImageUrl = "default";
                String gender = "";
                if (userDatasnapshot.child("sex").exists()) {
                    gender = userDatasnapshot.child("sex").getValue().toString();
                }
                String dateOfBirth = "";
                if (userDatasnapshot.child("dateOfBirth").exists()) {
                    dateOfBirth = userDatasnapshot.child("dateOfBirth").getValue().toString();
                }
                ArrayList images = new ArrayList();
                for (DataSnapshot dataSnapshot : userDatasnapshot.child("images").getChildren()) {
                    images.add(dataSnapshot.child("uri").getValue());
                }
                String location = "";
                if (userDatasnapshot.child("location").child("locality").exists()) {
                    location = userDatasnapshot.child("location").child("locality").getValue().toString();
                }
                String description = "";
                if (userDatasnapshot.child("description").exists()) {
                    description = userDatasnapshot.child("description").getValue().toString();
                }
                card = new Card(userDatasnapshot.getKey(), userDatasnapshot.child("name").getValue().toString(), profileImageUrl, images, gender, dateOfBirth, mutualTagsStringBuilder.toString(), tagsMap, distanceDouble, location, likesMe, description);
                Log.d(TAG, "CREATED NEW CARD :  "+ card.getName().toString() + " likesMe " + likesMe);
            } else {
            }
        } catch (Exception e) {
            Log.d(TAG, "tryError " + e.toString());
        }
        Log.d(TAG, "RETURN CARD :  "+ card + " likesMe " + likesMe);

        return card;
    }
}
