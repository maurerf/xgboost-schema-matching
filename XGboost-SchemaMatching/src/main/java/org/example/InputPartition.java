package org.example;

import ml.dmlc.xgboost4j.java.DMatrix;

public record InputPartition(
        float[] trainLabels,
        float[] testLabels,

        DMatrix trainDMatrix,
        DMatrix testDMatrix
)
{ }
