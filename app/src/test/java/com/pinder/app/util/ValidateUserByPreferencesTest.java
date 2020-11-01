package com.pinder.app.util;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.snapshot.IndexedNode;
import com.google.firebase.database.snapshot.Node;
import com.google.firebase.database.snapshot.NodeUtilities;
import com.pinder.app.models.Card;
import com.pinder.app.models.TagsObject;

import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.pinder.app.util.ValidateUserByPreferences.*;
import static org.mockito.Mockito.*;

class ValidateUserByPreferencesTest {
    //    @Mock
    public DataSnapshot userDatasnapshot = mock(DataSnapshot.class);
    @Mock
    private DataSnapshot tag1Datasnapshot;
    @Mock
    private DataSnapshot tag2Datasnapshot;
    Boolean likesMe;
    LatLng myLocation;
    ArrayList<TagsObject> myTagsList;
    Map<String, String> myInfo;

    @BeforeEach
    public void setup() {
//        setMocks();
//        setOtherData();
        likesMe = true;
        myLocation = new LatLng(52.2224607, 20.957038200000003);
        myTagsList = new ArrayList<TagsObject>(Arrays.asList(new TagsObject("default", "Any", "18", "99", "100000"), new TagsObject("date", "Any", "18", "99", "100000")));
        myInfo = new HashMap<String, String>() {{
            put("sex", "Female");
            put("age", "30");
        }};
//        when(userDatasnapshot.child("name").getValue(ChildData.class)).thenReturn(new ChildData("Natalie"));
        Map<String, String> myMap = new HashMap<String, String>() {{
            put("a", "b");
            put("c", "d");
        }};
        D
        when(userDatasnapshot.getValue(myMap)).thenReturn(myMap);
        when(userDatasnapshot.exists()).thenReturn(true);
        when(userDatasnapshot.child("name").getValue()).thenReturn("Natalie");
        when(userDatasnapshot.getKey()).thenReturn("v9FfAejJLAMPc95bEqEPPB6xSRV2");
        when(userDatasnapshot.child("dateOfBirth").getValue()).thenReturn("19/01/1997");
        when(userDatasnapshot.child("location").child("latitude").getValue()).thenReturn(54.439800000000005);
        when(userDatasnapshot.child("location").child("longitude").getValue()).thenReturn(18.5466069);
        when(userDatasnapshot.child("sex").exists()).thenReturn(true);
        when(userDatasnapshot.child("sex").getValue()).thenReturn("Female");
        when(userDatasnapshot.child("profileImageUrl").exists()).thenReturn(true);
        when(userDatasnapshot.child("profileImageUrl").getValue()).thenReturn("https://firebasestorage.googleapis.com/v0/b/tinderapp-7da20.appspot.com/o/images%2Fv9FfAejJLAMPc95bEqEPPB6xSRV2%2F-MAh-0HY_0DnyPSXDZ_7?alt=media&token=97e78695-8138-4ee5-9aae-82079133af74");
        when(userDatasnapshot.child("dateOfBirth").exists()).thenReturn(true);
        when(userDatasnapshot.child("location").child("locality").exists()).thenReturn(true);
        when(userDatasnapshot.child("description").exists()).thenReturn(true);
        when(tag1Datasnapshot.getKey()).thenReturn("date");
        when(tag1Datasnapshot.child("gender").getValue().toString()).thenReturn("Male");
        when(tag1Datasnapshot.child("minAge").getValue()).thenReturn("99");
        when(tag1Datasnapshot.child("maxAge").getValue()).thenReturn("18");
        when(tag1Datasnapshot.child("maxDistance").getValue()).thenReturn("100000");
        when(tag2Datasnapshot.getKey()).thenReturn("default");
        when(tag2Datasnapshot.child("gender").getValue().toString()).thenReturn("Male");
        when(tag2Datasnapshot.child("minAge").getValue()).thenReturn("99");
        when(tag2Datasnapshot.child("maxAge").getValue()).thenReturn("18");
        when(tag2Datasnapshot.child("maxDistance").getValue()).thenReturn("100000");
        when(userDatasnapshot.child("tags").getChildren()).thenReturn(Arrays.asList(tag1Datasnapshot, tag2Datasnapshot));
    }

    private void setOtherData() {
    }

    private void setMocks() {
    }

    @Test
    void test_ValidateUserByPreferences_returnTrue() throws Exception {
        //Arrange
        //Act
        Card c = validateUserByPreferences(userDatasnapshot, likesMe, myLocation, myTagsList, myInfo);
        System.out.println("CARD TEST:  " + c.toString());
        //Assert
    }

    class ChildData {
        public ChildData(String str) {
            this.str = str;
        }

        String str;
    }

    private class MockDatabaseReference extends DatabaseReference {

    }
}

