package org.example;

import java.util.List;

//todo: use class templating (cf. concatenation funcs)
public class Utils {
    //@Contract(pure=true) todo deploy this contract everywhere
    public static double doubleListAverage(List<Double> list) {
        return list.stream()
        .mapToDouble(d -> d)
        .average()
        .orElse(0d);
    }

    public static float[] concatenateFloatArrays(List<float[]> arrays) {
        int rows = 0;
        for (float[] array : arrays) {
            rows += array.length;
        }
        float[] result = new float[rows];
        int index = 0;
        for (float[] array : arrays) {
            for (var row : array) {
                result[index++] = row;
            }
        }
        return result;
    }

    public static double[][] concatenateDoubleArrays2D(List<double[][]> arrays) {
        int rows = 0;
        int cols = 0;
        for (double[][] array : arrays) {
            rows += array.length;
            cols = array[0].length;
        }
        double[][] result = new double[rows][cols];
        int index = 0;
        for (double[][] array : arrays) {
            for (double[] row : array) {
                result[index++] = row;
            }
        }
        return result;
    }

    public static float[] roadFloatArrayFromFile(String filePath) {
        // implement code to read a double[] array from file
        return null;
    }
    public static double[][] readDoubleArray2DFromFile(String filePath) {
        // implement code to read a double[][] array from file
        return null;
    }

    public static String MODEL_SAVE_PATH;
}
