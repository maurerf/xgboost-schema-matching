package org.example;

import ml.dmlc.xgboost4j.java.Booster;
import ml.dmlc.xgboost4j.java.DMatrix;
import ml.dmlc.xgboost4j.java.XGBoost;
import ml.dmlc.xgboost4j.java.XGBoostError;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class Model {
    public static Result train(int numRound, HashMap<String, Object> trainingParameters, File inputFile) throws XGBoostError {


        // Init metrics
        List<Double> precisionList = new ArrayList<>();
        List<Double> recallList = new ArrayList<>();
        List<Double> f1List = new ArrayList<>();
        List<List<Map.Entry<String, Double>>> featureImportanceList = new ArrayList<>();

        // Preprocess input
        final InputPartition part = IO.preprocess("Input/" + inputFile.getName());

        // Init watches
        final HashMap<String, DMatrix> trainingWatches = new HashMap<>( //todo: move
                Map.of(
                        "train", part.trainDMatrix(),
                        "test", part.testDMatrix()
                ));

        // Train model
        final Booster bst = XGBoost.train(
                part.trainDMatrix(),
                trainingParameters,
                numRound,
                trainingWatches,
                null,
                null);


        final double bestThreshold = 0d; //bst.getBestThreshold(); todo: threshold (?)

        // Test model todo: move?
        var result = test(bst, bestThreshold, part.testDMatrix());

        // Save model and threshold
        bst.saveModel(Utils.MODEL_SAVE_PATH + "/" + inputFile.getName() + ".model");
        //Files.writeString(Paths.get(Utils.MODEL_SAVE_PATH + "/" + file.getName() + ".threshold"), String.valueOf(bestThreshold)); //todo: threshold (?)

        precisionList.add(result.get("precision"));
        recallList.add(result.get("recall"));
        f1List.add(result.get("f1"));
        featureImportanceList.add((List<Map.Entry<String, Double>>) getFeatureImportances(bst)); //todo: fix the list/dict discrepancy

        // Evaluate feature importance
        Map<String, Double> featureNameImportance = featureImportanceList.stream()
                .flatMap(List::stream)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Double::sum));

        return new Result(
                precisionList,
                recallList,
                f1List,
                featureNameImportance);
    }


    public static Map<String, Double> test(Booster bst, double bestThreshold, DMatrix testFeatures)
            throws XGBoostError {
        float[][] prediction = bst.predict(testFeatures);

        // compute precision, recall, and F1 score
        double[] predLabels = new double[prediction.length];
        /*for (int i = 0; i < prediction.length; i++) {
            predLabels[i] = prediction[i] > bestThreshold ? 1 : 0;
        }*/

        //todo: sklearn port
        double precision = 0.0; //Precision.binary(testLabels, predLabels, 1);
        double recall = 0.0; //Recall.binary(testLabels, predLabels, 1);
        double f1 = 0.0; //F1.binary(testLabels, predLabels, 1);

        Map<String, Double> scores = new HashMap<>();
        scores.put("precision", precision);
        scores.put("recall", recall);
        scores.put("f1", f1);

        return scores;
    }

    private static Map<String, Integer> getFeatureImportances(Booster bst) throws XGBoostError {
        return null; //todo bst.getFeatureScore();
    }


}