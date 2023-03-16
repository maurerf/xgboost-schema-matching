package org.example;

import ml.dmlc.xgboost4j.java.DMatrix;

public record InputPartition(
        DMatrix trainDMatrix,
        DMatrix testDMatrix
)
{ }
