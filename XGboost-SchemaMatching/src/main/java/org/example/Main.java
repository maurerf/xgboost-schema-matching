package org.example;

import ml.dmlc.xgboost4j.java.XGBoostError;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.example.Model.trainLoop;

public class Main {
    public static void main(String[] args) throws IOException, XGBoostError {
        Utils.MODEL_SAVE_PATH = "model/" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));
        Path path = Paths.get(Utils.MODEL_SAVE_PATH);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Result result = trainLoop(300);
        // give evaluation results
        System.out.println("Average Confusion Matrix: " + Utils.doubleListAverage(result.precisionList()));
        System.out.println("Average Confusion Matrix: " + Utils.doubleListAverage(result.recallList()));
        System.out.println("Average Confusion Matrix: " + Utils.doubleListAverage(result.f1List()));
        System.out.println("Average Confusion Matrix: " + Utils.doubleListAverage(result.confusionMatrixList()));
        for(var entry : result.featureNameImportance().entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        //todo: the "if false" part from the python impl??
    }
}