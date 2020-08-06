package com.pinder.app.MyFunctions;



import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.Assert.*;

public class StringDateToAgeTest {
    @ParameterizedTest
    @ValueSource(ints = {0,1,2,3,4,5})
    public void isStringDateToAgeOld(int iterator) throws Exception {
        //Arrange
        String[] inputString = {"05/04/1990", "05/04/2000","05/04/1995","05/04/2000","05/04/1990","05/04/1995"};
        int[] expectedOutput={30,20,25,20,30,25};
        //Act
        //Assert
        StringDateToAge test = new StringDateToAge();
        int calculatedOutput= test.stringDateToAgeOld(inputString[iterator]);
        assertEquals(calculatedOutput,expectedOutput[iterator]);
        System.out.println("Calculated: " + calculatedOutput + " : " + expectedOutput[iterator] + " expected" );
    }

    @ParameterizedTest
    @ValueSource(ints = {0,1,2,3,4,5})
    public void isStringDateToAgeOreo(int iterator) throws Exception {
        //Arrange
        String[] inputString = {"05/04/1990", "05/04/2000","05/04/1995","05/04/2000","05/04/1990","05/04/1995"};
        int[] expectedOutput={30,20,25,20,30,25};
        //Act
        //Assert
        StringDateToAge test = new StringDateToAge();
        int calculatedOutput= test.stringDateToAgeOreo(inputString[iterator]);
        assertEquals(calculatedOutput,expectedOutput[iterator]);
        System.out.println("Calculated: " + calculatedOutput + " : " + expectedOutput[iterator] + " expected" );
    }
}