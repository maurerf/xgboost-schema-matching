package org.example;

import ml.dmlc.xgboost4j.java.XGBoostError;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.example.Model.train;

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

        // Init training parameters
        final HashMap<String, Object> trainingParameters = new HashMap<>(
                Map.of(
                        "silent", 1
                        //todo: more params
                )
        );

        // Training loop
        File inputDir = new File("Input");
        for (File inputFile : Objects.requireNonNull(inputDir.listFiles())) {
            Result result = train(300, trainingParameters, inputFile);

            // Give evaluation results
            System.out.println("Average Precision: " + Utils.doubleListAverage(result.precisionList()));
            System.out.println("Average Recall: " + Utils.doubleListAverage(result.recallList()));
            System.out.println("Average F1 Score: " + Utils.doubleListAverage(result.f1List()));
            for (var entry : result.featureNameImportance().entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }
    }
}