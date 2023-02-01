package org.example;

public record InputPartition(
        double[][] trainFeatures,
        double[] trainLabels,
        double[][] testFeatures,
        double[] testLabels
)
{ }
