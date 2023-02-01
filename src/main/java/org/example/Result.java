package org.example;

import java.util.List;
import java.util.Map;

public record Result(
        List<Double> precisionList,
        List<Double> recallList,
        List<Double> f1List,
        List<Double> confusionMatrixList,
        Map<String, Double> featureNameImportance
) { }
