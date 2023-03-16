package org.example;

import ml.dmlc.xgboost4j.java.DMatrix;
import ml.dmlc.xgboost4j.java.XGBoostError;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class IO {
    private static DMatrix mergeFeatures(String path) throws XGBoostError {
        /* TODO: port to dmatrix
        File folder = new File(path);
        File[] files = folder.listFiles();
        Arrays.sort(files);
        List<double[][]> mergedFeatures = Arrays.stream(files)
                .filter(f -> f.getName().contains("features"))
                .map(f -> Utils.readDoubleArray2DFromFile(f.getAbsolutePath()))
                .collect(Collectors.toList());
        return Utils.concatenateDoubleArrays2D(mergedFeatures);
         */
        return new DMatrix("");
    }

    private static float[] getLabels(String path) {
        File folder = new File(path);
        File[] files = folder.listFiles();

        assert files != null;
        Arrays.sort(files);

        List<float[]> mergedFeatures = Arrays.stream(files)
                .filter(f -> f.getName().contains("labels"))
                .map(f -> Utils.roadFloatArrayFromFile(f.getAbsolutePath()))
                .collect(Collectors.toList());

        return Utils.concatenateFloatArrays(mergedFeatures);
    }

    public static InputPartition preprocess(String path) throws XGBoostError {
        String trainPath = path + "/train/";
        String testPath = path + "/test/";

        var trainFeatures = mergeFeatures(trainPath);
        trainFeatures.setLabel(getLabels(trainPath));
        var testFeatures = mergeFeatures(testPath);
        testFeatures.setLabel(getLabels(testPath));

        return new InputPartition(trainFeatures, testFeatures);
    }
}
