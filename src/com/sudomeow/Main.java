package com.sudomeow;

import com.sudomeow.algorithm.KNNAlgorithm;

import java.io.File;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        String learnPath = null;
        String testPath = null;
        boolean showInfo = false;
        int k = 0;

        System.out.println(Arrays.toString(args));

        for (int i = 0; i < args.length; i++) {
            String foo = args[i];
            switch (foo) {
                case "-t" -> learnPath = "./" + args[i + 1];
                case "-T" -> testPath = "./" + args[i + 1];
                case "-i" -> showInfo = true;
                case "-k" -> k = Integer.parseInt(args[i + 1]);
            }
        }

        if (learnPath == null || testPath == null || k == 0) {
            printHelp();
            System.exit(1);
        }

        KNNAlgorithm algorithm = new KNNAlgorithm(
                new File(learnPath),
                new File(testPath),
                k,
                showInfo
        );
        algorithm.exec();
    }

    public static void printHelp() {
        StringBuilder out = new StringBuilder();
        out
                .append("Program arguments: \n")
                .append("\t-t\tPath to learning dataset\n")
                .append("\t-T\tPath to testing dataset\n")
                .append("\t-i\tInclude extra info in final results(Optional).\n")
                .append("\t-k\tThe number of nearest neighbours to use in decisions.\n");
        System.err.println(out.toString());
    }
}
