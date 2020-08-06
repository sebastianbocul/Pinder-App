package com.pinder.app.tags.maintags;

import org.junit.Test;

import static org.junit.Assert.*;

public class TagsObjectTest {
    @Test
    public void isTagsObjectEqual_identicalProperties_returnTrue() throws Exception {
        //Arrange
        TagsObject tag1 = new TagsObject("tag1","male","18","25","100");
        TagsObject tag2 = new TagsObject("tag1","male","18","25","100");
        //Act
        //Assert
        assertEquals(tag1,tag2);
        System.out.println("Tags are equals! ");
    }

    @Test
    public void isTagObjectEqual_differentTagNames_returnFalse() throws Exception {
        //Arrange
        TagsObject tag1 = new TagsObject("tag1","male","18","25","100");
        TagsObject tag2 = new TagsObject("tag2","male","18","25","100");
        //Act
        //Assert
        assertNotEquals(tag1,tag2);
        System.out.println("Tags are not equals! Different tag names!");
    }

    @Test
    public void isTagObjectEqual_differentGender_returnFalse() throws Exception {
        //Arrange
        TagsObject tag1 = new TagsObject("tag1","male","18","25","100");
        TagsObject tag2 = new TagsObject("tag1","female","18","25","100");
        //Act
        //Assert
        assertNotEquals(tag1,tag2);
        System.out.println("Tags are not equals! Different genders!");
    }
    @Test
    public void isTagObjectEqual_differentAgeMin_returnFalse() throws Exception {
        //Arrange
        TagsObject tag1 = new TagsObject("tag1","male","18","25","100");
        TagsObject tag2 = new TagsObject("tag1","male","19","25","100");
        //Act
        //Assert
        assertNotEquals(tag1,tag2);
        System.out.println("Tags are not equals! Different minimum ages!");
    }

    @Test
    public void isTagObjectEqual_differentAgeMax_returnFalse() throws Exception {
        //Arrange
        TagsObject tag1 = new TagsObject("tag1","male","18","25","100");
        TagsObject tag2 = new TagsObject("tag1","male","18","30","100");
        //Act
        //Assert
        assertNotEquals(tag1,tag2);
        System.out.println("Tags are not equals! Different maximum ages!");
    }

    @Test
    public void isTagObjectEqual_differentDistance_returnFalse() throws Exception {
        //Arrange
        TagsObject tag1 = new TagsObject("tag1","male","18","25","100");
        TagsObject tag2 = new TagsObject("tag1","male","18","25","1000");
        //Act
        //Assert
        assertNotEquals(tag1,tag2);
        System.out.println("Tags are not equals! Different distance ages!");
    }
    @Test
    public void isTagObjectEqual_differentNameAndDistance_returnFalse() throws Exception {
        //Arrange
        TagsObject tag1 = new TagsObject("tag1","male","18","25","100");
        TagsObject tag2 = new TagsObject("tag2","male","18","25","1000");
        //Act
        //Assert
        assertNotEquals(tag1,tag2);
        System.out.println("Tags are not equals! Different name and distance ages!");
    }
}
