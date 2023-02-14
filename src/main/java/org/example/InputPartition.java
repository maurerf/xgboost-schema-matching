package org.example;

import ml.dmlc.xgboost4j.java.DMatrix;

public record InputPartition(
        float[] trainLabels,
        float[] testLabels,
        double[][] trainFeatures,
        double[][] testFeatures,

        //todo: only use either above or below

        DMatrix trainDMatrix,
        DMatrix testDMatrix
)
{ }
