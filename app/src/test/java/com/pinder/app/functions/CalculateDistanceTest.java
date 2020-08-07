package com.pinder.app.functions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Arrays;

public class CalculateDistanceTest {
    //gd-wwa || gd-szczecin ||  gd-kraków || gd-elb || gd--new york || gd - london || gd - gd || gd - gdynia || gd-rio ||gd-tokyo
    private ArrayList<String> cities = new ArrayList<>(Arrays.asList("Warszawa", "Szczecin", "Kraków", "Elbląg", "New York", "London", "Gdansk", "Gdynia", "Rio", "Tokyo"));
    private ArrayList<Double> lat1 = new ArrayList<>(Arrays.asList(54.3517, 54.3517, 54.3517, 54.3517, 54.3517, 54.3517, 54.3517, 54.3517, 54.3517, 54.3517));
    private ArrayList<Double> lon1 = new ArrayList<>(Arrays.asList(18.6510, 18.6510, 18.6510, 18.6510, 18.6510, 18.6510, 18.6510, 18.6510, 18.6510, 18.6510));
    private ArrayList<Double> lat2 = new ArrayList<>(Arrays.asList(52.2243, 53.4316, 50.0717, 54.1597, 40.7169, 51.5235, 54.3517, 54.5064, -22.9385, 35.6829));
    private ArrayList<Double> lon2 = new ArrayList<>(Arrays.asList(21.0147, 14.5500, 19.9363, 19.4033, -74.0175, -0.1230, 18.6510, 18.5360, -43.1922, 139.7651));

    //https://gps-coordinates.org/distance-between-coordinates.php
    private ArrayList<Double> expectedDistance = new ArrayList<>(Arrays.asList(284.40, 287.49, 484.25, 53.48, 6601.46, 1292.59, 0.0, 18.77, 10411.48, 8533.08));

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9})
    void calculatedDistance_expectedDistance_returnTrue(int iterator) throws Exception {
        //Arrange
        double calculatedDistance = CalculateDistance.distance(lat1.get(iterator), lon1.get(iterator), lat2.get(iterator), lon2.get(iterator));
        int expectedRound = (int) Math.round(expectedDistance.get(iterator));
        int calculatedRound = (int) Math.round(calculatedDistance);
        //Assert
        Assertions.assertEquals(expectedRound, calculatedRound);
        System.out.println("Gdansk - " +cities.get(iterator));
        System.out.println("Expected: " + expectedDistance.get(iterator) + " : " + calculatedDistance + " Calculated");
    }
}
