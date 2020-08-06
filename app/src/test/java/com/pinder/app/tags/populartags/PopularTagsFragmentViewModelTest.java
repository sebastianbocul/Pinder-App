package com.pinder.app.tags.populartags;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PopularTagsFragmentViewModelTest {
    @Test
    public void sortCollectionTest() throws Exception {
        //Arrange
        PopularTagsObject o1 = new PopularTagsObject("AAA", 5);
        PopularTagsObject o2 = new PopularTagsObject("BBB", 10);
        PopularTagsObject o3 = new PopularTagsObject("CCC", 1);
        PopularTagsObject o4 = new PopularTagsObject("DDD", 50);
        PopularTagsObject o5 = new PopularTagsObject("EEE", 3);
        List<PopularTagsObject> input = new ArrayList<>();
        input.addAll(Arrays.asList(o1, o2, o3, o4, o5));
        input = PopularTagsFragmentViewModel.sortCollection(input);
        PopularTagsObject[] inputP = new PopularTagsObject[5];
        for (int i = 0; i < input.size(); i++) {
            inputP[i] = input.get(i);
        }
        PopularTagsObject[] expectedP = {o4, o2, o1, o5, o3};
        //Act
        //Assert
        Assertions.assertArrayEquals(inputP, expectedP);
        System.out.println("Expected : Input");
        for (int i = 0; i < input.size(); i++) {
            System.out.println(expectedP[i].getTagName() + " " + expectedP[i].getTagPopularity() + " : " + inputP[i].getTagName() + " " + inputP[i].getTagPopularity());
        }
        //assertArrayEquals(input, expected);
    }
}