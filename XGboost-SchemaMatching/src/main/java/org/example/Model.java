package org.example;

import ml.dmlc.xgboost4j.java.Booster;
import ml.dmlc.xgboost4j.java.DMatrix;
import ml.dmlc.xgboost4j.java.XGBoost;
import ml.dmlc.xgboost4j.java.XGBoostError;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Model {
    public static Result trainLoop(int numRound) throws IOException, XGBoostError {
        // Init training parameters
        final HashMap<String, Object> trainingParameters = new HashMap<String, Object>(
                Map.of(
                        "silent", 1
                )
        );

        // Init metrics
        List<Double> precisionList = new ArrayList<>();
        List<Double> recallList = new ArrayList<>();
        List<Double> f1List = new ArrayList<>();
        List<Double> cMatrixList = new ArrayList<>(); //todo: fix confusion matrix: Not Double
        List<List<Map.Entry<String, Double>>> featureImportanceList = new ArrayList<>();

        File inputDir = new File("Input");
        for (File file : Objects.requireNonNull(inputDir.listFiles())) {
            // Preprocess input
            InputPartition part = preprocess("Input/" + file.getName());

            part.trainDMatrix().setLabel(part.trainLabels()); //todo: set this earlier, e.g. in preprocess

            final HashMap<String, DMatrix> trainingWatches = new HashMap<String, DMatrix>(
                    Map.of(
                            "train", part.trainDMatrix(),
                            "test", part.testDMatrix()
            ));

            // Train model
            Booster bst = XGBoost.train(
                    part.trainDMatrix(),
                    trainingParameters,
                    numRound,
                    trainingWatches,
                    null,
                    null);


            double bestThreshold = 0d; //bst.getBestThreshold(); todo

            // Test model todo: move
            var result = test(bst, bestThreshold, part.testFeatures(), part.testLabels());

            // Normalize confusion matrix
            /*
            var cMatrix = result.get("confusionMatrix")
            double[][] cMatrixNorm = new double[cMatrix.length][cMatrix[0].length];
            for (int i = 0; i < cMatrix.length; i++) {
                for (int j = 0; j < cMatrix[0].length; j++) {
                    cMatrixNorm[i][j] = cMatrix[i][j] / cMatrix[i][j];
                }
            }*/ //todo: fix confusion matrix

            // Save model and threshold
            bst.saveModel(Utils.MODEL_SAVE_PATH + "/" + file.getName() + ".model");
            //Files.writeString(Paths.get(Utils.MODEL_SAVE_PATH + "/" + file.getName() + ".threshold"), String.valueOf(bestThreshold)); //todo: threshold (?)

            precisionList.add(result.get("precision"));
            recallList.add(result.get("recall"));
            f1List.add(result.get("f1"));
            //cMatrixList.add(cMatrixNorm); todo: fix confusion matrix
            featureImportanceList.add((List<Map.Entry<String, Double>>) getFeatureImportances(bst)); //todo: fix the list/dict discrepancy
        }

        // Evaluate feature importance
        Map<String, Double> featureNameImportance = featureImportanceList.stream()
                .flatMap(List::stream)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Double::sum));

        return new Result(
                precisionList,
                recallList,
                f1List,
                cMatrixList, //todo: fix confusion matrix
                featureNameImportance);
    }


    public static Map<String, Double> test(Booster bst, double bestThreshold, double[][] testFeatures, float[] testLabels)
            throws XGBoostError {
        DMatrix dtest = new DMatrix(""); //todo: fill properly using testFeatures
        dtest.setLabel(testLabels);
        float[][] prediction = bst.predict(dtest);

        // compute precision, recall, and F1 score
        double[] predLabels = new double[prediction.length];
        for (int i = 0; i < prediction.length; i++) {
            predLabels[i] = prediction[i] > bestThreshold ? 1 : 0;
        }

        //todo: sklearn port
        double precision = Precision.binary(testLabels, predLabels, 1);
        double recall = Recall.binary(testLabels, predLabels, 1);
        double f1 = F1.binary(testLabels, predLabels, 1);
        // int[][] cMatrix = ConfusionMatrix.binary(testLabels, predLabels); todo: fix confusion matrix

        Map<String, Double> result = new HashMap<>();
        result.put("precision", precision);
        result.put("recall", recall);
        result.put("f1", f1);
        //result.put("confusionMatrix", cMatrix); todo: fix confusion matrix
        return result;
    }

    //todo: verify correctness
    public static double[][] mergeFeatures(String path) {
        File folder = new File(path);
        File[] files = folder.listFiles();
        Arrays.sort(files);
        List<double[][]> mergedFeatures = Arrays.stream(files)
                .filter(f -> f.getName().contains("features"))
                .map(f -> Utils.readDoubleArray2DFromFile(f.getAbsolutePath()))
                .collect(Collectors.toList());
        return Utils.concatenateDoubleArrays2D(mergedFeatures);
    }

    //todo: verify correctness
    public static double[] getLabels(String path) {
        File folder = new File(path);
        File[] files = folder.listFiles();

        assert files != null;
        Arrays.sort(files);

        List<double[]> mergedFeatures = Arrays.stream(files)
                .filter(f -> f.getName().contains("labels"))
                .map(f -> Utils.readDoubleArrayFromFile(f.getAbsolutePath()))
                .collect(Collectors.toList());

        return Utils.concatenateDoubleArrays(mergedFeatures);
    }

    public static Map<String, Integer> getFeatureImportances(Booster bst) throws XGBoostError {
        return null; //todo bst.getFeatureScore();
    }

    public static InputPartition preprocess(String path) throws XGBoostError {
        String trainPath = path + "/train/";
        String testPath = path + "/test/";

        double[][] trainFeatures = mergeFeatures(trainPath);
        double[] trainLabels = getLabels(trainPath);
        double[][] testFeatures = mergeFeatures(testPath);
        double[] testLabels = getLabels(testPath);

        return new InputPartition(trainFeatures, trainLabels, testFeatures, testLabels, new DMatrix(""), new DMatrix("")); //todo: fix the Dmatrix situation
    }
}