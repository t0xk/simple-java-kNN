package com.sudomeow.algorithm;

import java.util.Arrays;

/**
 * Represents one line in the algorithms test and learning collections
 */
public class DataRow {

    double[] values;
    String readValue;
    String determinedValue = null;

    public static DataRow fromString(String row){
        String[] splitRow = row.split(",");
        int len = splitRow.length - 1;
        double[] values = new double[len];
        String readValue = splitRow[len];

        for(int i = 0; i < values.length; i++){
            values[i] = Double.parseDouble(splitRow[i]);
        }
        return new DataRow(values, readValue);
    }

    private DataRow(double[] values, String readValue) {
        this.values = values;
        this.readValue = readValue;
    }

    public double[] getValues() {
        return values;
    }

    public String getReadValue() {
        return readValue;
    }

    public String getDeterminedValue() {
        return determinedValue;
    }

    public void setDeterminedValue(String determinedValue) {
        this.determinedValue = determinedValue;
    }

    @Override
    public String toString() {
        return "DataRow{" +
                "values=" + Arrays.toString(values) +
                ", readValue='" + readValue + '\'' +
                ", determinedValue='" + determinedValue + '\'' +
                '}';
    }
}
