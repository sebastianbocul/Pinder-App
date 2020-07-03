package com.pinder.app;

import org.junit.Test;

import static org.junit.Assert.*;

public class MainActivityTest {
    @Test
    public void stringDateToAgeOld() {
        String input = "05/04/1995";
        int output;
        int expected = 25;
        MainActivity test = new MainActivity();
        output = test.stringDateToAgeOld(input);
        assertEquals(expected,output);
    }

    @Test
    public void stringDateToAgeOld2() {
        String input = "05/04/1990";
        int output;
        int expected = 30;
        MainActivity test = new MainActivity();
        output = test.stringDateToAgeOld(input);
        assertEquals(expected,output);
    }

    @Test
    public void stringDateToAgeOld3() {
        String input = "05/04/2000";
        int output;
        int expected = 20;
        MainActivity test = new MainActivity();
        output = test.stringDateToAgeOld(input);
        assertEquals(expected,output);
    }


    @Test
    public void stringDateToAgeOreo() {
        String input = "05/04/1995";
        int output;
        int expected = 25;
        MainActivity test = new MainActivity();
        output = test.stringDateToAgeOreo(input);
        assertEquals(expected,output);
    }

    @Test
    public void stringDateToAgeOreo2() {
        String input = "05/04/1990";
        int output;
        int expected = 30;
        MainActivity test = new MainActivity();
        output = test.stringDateToAgeOreo(input);
        assertEquals(expected,output);
    }

    @Test
    public void stringDateToAgeOreo3() {
        String input = "05/04/2000";
        int output;
        int expected = 20;
        MainActivity test = new MainActivity();
        output = test.stringDateToAgeOreo(input);
        assertEquals(expected,output);
    }
}