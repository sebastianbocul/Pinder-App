package com.pinder.app.MyFunctions;

import org.junit.Test;

import static org.junit.Assert.*;

public class StringDateToAgeTest {
    @Test
    public void stringDateToAgeOld() {
        String input = "05/04/1995";
        int output;
        int expected = 25;
        StringDateToAge test = new StringDateToAge();
        output = test.stringDateToAgeOld(input);
        assertEquals(expected,output);
    }

    @Test
    public void stringDateToAgeOld2() {
        String input = "05/04/1990";
        int output;
        int expected = 30;
        StringDateToAge test = new StringDateToAge();
        output = test.stringDateToAgeOld(input);
        assertEquals(expected,output);
    }

    @Test
    public void stringDateToAgeOld3() {
        String input = "05/04/2000";
        int output;
        int expected = 20;
        StringDateToAge test = new StringDateToAge();
        output = test.stringDateToAgeOld(input);
        assertEquals(expected,output);
    }


    @Test
    public void stringDateToAgeOreo() {
        String input = "05/04/1995";
        int output;
        int expected = 25;
        StringDateToAge test = new StringDateToAge();
        output = test.stringDateToAgeOreo(input);
        assertEquals(expected,output);
    }

    @Test
    public void stringDateToAgeOreo2() {
        String input = "05/04/1990";
        int output;
        int expected = 30;
        StringDateToAge test = new StringDateToAge();
        output = test.stringDateToAgeOreo(input);
        assertEquals(expected,output);
    }

    @Test
    public void stringDateToAgeOreo3() {
        String input = "05/04/2000";
        int output;
        int expected = 20;
        StringDateToAge test = new StringDateToAge();
        output = test.stringDateToAgeOreo(input);
        assertEquals(expected,output);
    }
}