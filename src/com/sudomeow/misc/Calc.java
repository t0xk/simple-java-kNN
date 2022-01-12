package com.sudomeow.misc;

public class Calc {

    /**
     * Calculates the distance between two n-dimensional points, where n is the length of the point array
     * @param point1 Array of values that represent the point in n-dimentional space
     * @param point2 Array of values that represent the point in n-dimentional space
     * @return The distance between point1 and point2
     * @throws Exception Is thrown if given points don't represent the same dimention
     */
    public static double distance(double[] point1, double[] point2) throws Exception {
        if (point1.length != point2.length) {
            throw new Exception("Tocki se ne ujemata v dimenziji!");
        }

        double sum = 0;
        for (int i = 0; i < point1.length; i++) {
            sum += Math.pow(point1[i] - point2[i], 2);
        }

        return Math.sqrt(sum);
    }

}
