package com.sudomeow.algorithm;

import com.sudomeow.misc.Calc;

import java.io.*;
import java.util.*;

/**
 * Main k-Nearest Neighbours(kNN) algorithm logic
 */
public class KNNAlgorithm {

    final File ucnaCsv;
    final File testnaCsv;
    final int k;
    final boolean extra;

    ArrayList<DataRow> ucnaMnozica = new ArrayList<>();
    ArrayList<DataRow> testnaMnozica = new ArrayList<>();

    ArrayList<String> possibleValues = new ArrayList<>();
    int[][] confusionMatrix;

    public KNNAlgorithm(File ucnaCsv, File testnaCsv, int k, boolean extra) {
        this.ucnaCsv = ucnaCsv;
        this.testnaCsv = testnaCsv;
        this.k = k;
        this.extra = extra;
    }

    /**
     * All necessary preparation that have to run before the main algorithm logic.
     */
    private void init() {
        /*
            Ucna Mnozica
         */
        populateMnozica(ucnaCsv, ucnaMnozica);

        for (DataRow row : ucnaMnozica) {
            if (!possibleValues.contains(row.getReadValue())) {
                possibleValues.add(row.getReadValue());
            }
        }
        confusionMatrix = new int[possibleValues.size()][possibleValues.size()];

        /*
            Testna Mnozica
         */
        populateMnozica(testnaCsv, testnaMnozica);

        /*
            Debug
         */
        System.out.println("Number of elements in learning collection: " + ucnaMnozica.size());
        System.out.println("Number of elements in testing collection: " + testnaMnozica.size());
    }

    /**
     * Main execution logic
     */
    public void exec() {
        init();

        /*
            Determine the value based on k nearest neighbours for every element of the test collection
         */
        for (DataRow d : testnaMnozica) {
            HashMap<DataRow, Double> distances = new HashMap<>();

            for (DataRow row : ucnaMnozica) {
                try {
                    distances.put(row, Calc.distance(d.getValues(), row.getValues()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            distances = sortMap(distances);

            HashMap<String, Integer> neighbourVals = new HashMap<>();
            int count = 0;
            for (DataRow row : distances.keySet()) {
                if (count >= k) {
                    break;
                }

                if (neighbourVals.containsKey(row.getReadValue())) {
                    neighbourVals.put(row.getReadValue(), neighbourVals.get(row.getReadValue()) + 1);
                } else {
                    neighbourVals.put(row.getReadValue(), 1);
                }

                count++;
            }
            int max = Collections.max(neighbourVals.values());
            for (String s : neighbourVals.keySet()) {
                if (neighbourVals.get(s) == max) {
                    d.setDeterminedValue(s);
                    break;
                }
            }

            /*
                Populate the confusion matrix
            */
            confusionMatrix[possibleValues.indexOf(d.getReadValue())][possibleValues.indexOf(d.getDeterminedValue())] += 1;
        }
        printConfusionMatrix(confusionMatrix);
        System.out.println();

        if (extra)
            printExtra();
    }

    /**
     * Displays the extra information implied by the -i argument.
     */
    public void printExtra() {
        StringBuilder out = new StringBuilder("Matrike:\n");

        /*
            Tocnost
         */
        double tocnost;
        int tocniSum = 0;
        for (int i = 0; i < confusionMatrix.length; i++) {
            for (int j = 0; j < confusionMatrix[0].length; j++) {
                if (i == j) {
                    tocniSum += confusionMatrix[i][j];
                }
            }
        }
        tocnost = (double) tocniSum / (double) testnaMnozica.size();
        out.append("Tocnost:\t").append(tocnost).append("\n");

        /*
            Precision
         */
        double precision;
        double[] precisions = new double[confusionMatrix.length];
        double temp = 0;
        for (int i = 0; i < confusionMatrix[0].length; i++) {
            int stolpecSum = 0;
            for (int j = 0; j < confusionMatrix.length; j++) {
                stolpecSum += confusionMatrix[j][i];
            }
            temp += precisions[i] = (double) confusionMatrix[i][i] / (double) stolpecSum;
        }
        precision = temp / (double) confusionMatrix.length;
        out.append("Precision:\t").append(precision).append("\n");

        /*
            Recall
         */
        double recall;
        double[] recalls = new double[confusionMatrix.length];
        double tempR = 0;
        for (int i = 0; i < confusionMatrix.length; i++) {
            int vrsticaSum = 0;
            for (int j = 0; j < confusionMatrix[0].length; j++) {
                vrsticaSum += confusionMatrix[i][j];
            }
            tempR += recalls[i] = (double) confusionMatrix[i][i] / (double) vrsticaSum;
        }
        recall = tempR / (double) confusionMatrix[0].length;
        out.append("Recall:\t\t").append(recall).append("\n");

        /*
            F-score
         */
        double fscore = 0;
        double[] totals = new double[confusionMatrix.length];
        double totalSum = 0;
        for (int i = 0; i < confusionMatrix.length; i++) {
            int total = 0;
            for (int in : confusionMatrix[i]) {
                total += in;
            }
            totalSum += totals[i] = total;
        }

        for (int i = 0; i < confusionMatrix.length; i++) {
            double fmera = 2 * ((precisions[i] * recalls[i]) / (precisions[i] + recalls[i]));
            fscore += (totals[i] / totalSum) * fmera;
        }
        out.append("Fscore:\t\t").append(fscore).append("\n");

        System.out.println(out.toString());
    }

    /**
     * Populates the ArrayList with values from a given File
     *
     * @param f       The File we're reading
     * @param mnozica The ArrayList we're populating
     */
    private void populateMnozica(File f, ArrayList<DataRow> mnozica) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            String msg;
            int count = 0;

            while ((msg = reader.readLine()) != null) {
                if (count > 0) {
                    mnozica.add(DataRow.fromString(msg));
                }
                count++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sorts the given HashMap based on the entry values
     *
     * @param unsorted HashMap that will be sorted
     * @return The sorted HashMap
     */
    private HashMap<DataRow, Double> sortMap(HashMap<DataRow, Double> unsorted) {
        List<Map.Entry<DataRow, Double>> list = new LinkedList<>(unsorted.entrySet());
        HashMap<DataRow, Double> sortedMap = new LinkedHashMap<>();

        Collections.sort(list, Comparator.comparing(Map.Entry::getValue));

        for (Map.Entry<DataRow, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    /**
     * A stylized prinout of the 2-dimensional confusion matrix
     *
     * @param matrix The confusion matrix that we're printing
     */
    private void printConfusionMatrix(int[][] matrix) {
        StringBuilder out = new StringBuilder("Confusion matrix:\n");

        for (String s : possibleValues) {
            out.append(s).append("\t");
        }
        out.append("\n");

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                out.append(matrix[i][j]).append("\t\t");
            }
            out.append(possibleValues.get(i)).append("\n");
        }

        System.out.println(out.toString());
    }

}
