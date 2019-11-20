/**
 * This file is part of Library for Associative Classification (LAC)
 *
 * Copyright (C) 2019
 *   
 * LAC is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details. You should have 
 * received a copy of the GNU General Public License along with 
 * this program.  If not, see http://www.gnu.org/licenses/
 */
package lac.metrics;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Confusion matrix used to calculate easily all the metrics of the quality of
 * the classifications
 */
public class ConfusionMatrix {
    /**
     * Total number of cases
     */
    private long total = 0;

    /**
     * Correct number of cases
     */
    protected long correct = 0;

    /**
     * Map used to represent the final confusion matrix
     */
    protected Map<Short, Map<Short, Long>> matrix;

    /**
     * All the real classes contained in the dataset
     */
    protected TreeSet<Short> allRealklasss = new TreeSet<Short>();

    /**
     * All the predictions done by the classifier
     */
    protected TreeSet<Short> allPredictedklasss = new TreeSet<Short>();

    /**
     * Constructor
     */
    public ConfusionMatrix() {
        this.matrix = new TreeMap<Short, Map<Short, Long>>();
    }

    /**
     * Get average recall of the classifier
     * 
     * @return the average recall of the classifier
     */
    public double getAverageRecall() {
        double result = 0.0;

        Collection<Double> values = this.getRecallForklasss().values();
        for (double value : values) {
            result += value;
        }

        return result / (double) values.size();
    }

    /**
     * Get the average precission for all the classes
     * 
     * @return the average precision for classes
     */
    public double getAveragePrecision() {
        double result = 0.0;

        Collection<Double> values = this.getPrecisionForklasss().values();
        for (double value : values) {
            result += value;
        }

        return result / (double) values.size();
    }

    /**
     * Get the Micro F-measure
     *
     * @return the micro f-measure for all the classes
     */
    public double getMicroFMeasure() {
        long allTruePositives = 0;
        long allTruePositivesAndFalsePositives = 0;
        long allTruePositivesAndFalseNegatives = 0;

        for (Short klass : this.matrix.keySet()) {
            if (this.matrix.containsKey(klass) && this.matrix.get(klass).containsKey(klass)) {
                allTruePositives += this.matrix.get(klass).get(klass);
            }
            allTruePositivesAndFalsePositives += this.getColSum(klass);
            allTruePositivesAndFalseNegatives += this.getRowSum(klass);
        }

        double precision = (double) allTruePositives / (double) allTruePositivesAndFalsePositives;
        double recall = (double) allTruePositives / (double) allTruePositivesAndFalseNegatives;

        return (2.0 * precision * recall) / (precision + recall);
    }

    /**
     * Get the Macro F-measure
     *
     * @return the macro f-measure for all the classes
     */
    public double getMacroFMeasure() {
        Map<Short, Double> fMeasureForklasss = this.getFMeasureForKlasses();

        double totalFMeasure = 0;

        for (Double value : fMeasureForklasss.values()) {
            totalFMeasure += value;
        }

        return totalFMeasure / fMeasureForklasss.size();
    }

    /**
     * Get the F-measure for each independent class
     *
     * @return double the f-measure for each class
     */
    private Map<Short, Double> getFMeasureForKlasses() {
        Map<Short, Double> fMeasure = new LinkedHashMap<>();

        Map<Short, Double> precisionForklasss = this.getPrecisionForklasss();
        Map<Short, Double> recallForklasss = this.getRecallForklasss();

        for (Short klass : this.allRealklasss) {
            double p = precisionForklasss.get(klass);
            double r = recallForklasss.get(klass);

            double fm = 0.0;

            if ((p + r) > 0) {
                fm = (2.0 * p * r) / (p + r);
            }

            fMeasure.put(klass, fm);
        }

        return fMeasure;
    }

    /**
     * Get recall for each class
     *
     * @return double
     */
    private Map<Short, Double> getRecallForklasss() {
        Map<Short, Double> recalls = new LinkedHashMap<Short, Double>();
        for (Short klass : allRealklasss) {
            double recall = this.getRecallForKlass(klass);

            recalls.put(klass, recall);
        }
        return recalls;
    }

    /**
     * Return recall for single klass
     *
     * @param klass klass
     * @return double
     */
    private double getRecallForKlass(Short klass) {
        long fnTp = 0;
        double recall = 0;
        long tp = 0;

        if (matrix.containsKey(klass) && matrix.get(klass).containsKey(klass)) {
            tp = this.matrix.get(klass).get(klass);
            fnTp = this.getRowSum(klass);
        }

        if (fnTp > 0) {
            recall = (double) tp / (double) (fnTp);
        }

        return recall;
    }

    /**
     * Add both prediction and real value to the confusion matrix
     * 
     * @param realValue
     * @param observedValue
     */
    public void add(Short realValue, Short observedValue) {
        this.allRealklasss.add(realValue);
        this.allPredictedklasss.add(observedValue);

        if (!this.matrix.containsKey(realValue)) {
            this.matrix.put(realValue, new TreeMap<Short, Long>());
        }

        if (!this.matrix.get(realValue).containsKey(observedValue)) {
            this.matrix.get(realValue).put(observedValue, 0L);
        }

        long currentValue = this.matrix.get(realValue).get(observedValue);
        this.matrix.get(realValue).put(observedValue, currentValue + 1);

        this.total += 1;

        if (realValue == observedValue) {
            this.correct += 1;
        }
    }

    /**
     * Add both prediction and real value to the confusion matrix
     * 
     * @param realValue
     * @param predictedValue
     */
    public void add(short realValue, short predictedValue) {
        this.add(Short.valueOf(realValue), Short.valueOf(predictedValue));
    }

    /**
     * Get the accuracy for the classifier
     * 
     * @return the accuracy
     */
    public double getAccuracy() {
        return ((double) this.correct / (double) this.total);
    }

    /**
     * Get the value for the metric Cohen's Kappa
     *
     * @return double the value for kappa measure
     */
    public double getKappa() {
        double p0 = this.getAccuracy();

        double pe = 0;
        for (Short klass : this.allRealklasss) {
            double rowSum = this.getRowSum(klass);
            double colSum = this.getColSum(klass);

            pe += (rowSum * colSum) / this.total;
        }

        pe /= this.total;

        return (p0 - pe) / (1.0 - pe);
    }

    /**
     * Get precision for each separated class
     * 
     * @return the precision for each class
     */
    private Map<Short, Double> getPrecisionForklasss() {
        Map<Short, Double> precisions = new LinkedHashMap<Short, Double>();
        for (Short klass : this.allRealklasss) {
            double precision = this.getPrecisionForKlass(klass);

            precisions.put(klass, precision);
        }
        return precisions;
    }

    /**
     * Get the precision for one concrete class
     * 
     * @param klass to get the precision
     * @return precision for the specified class
     */
    private double getPrecisionForKlass(Short klass) {
        double precision = 0;
        long tp = 0;
        long fpTp = 0;

        if (matrix.containsKey(klass) && matrix.get(klass).containsKey(klass)) {
            tp = this.matrix.get(klass).get(klass);
            fpTp = this.getColSum(klass);
        }

        if (fpTp > 0) {
            precision = (double) tp / (double) (fpTp);
        }

        return precision;
    }

    /**
     * Get the sum for the column of the specifid class
     * 
     * @param klass column to be sum
     * @return the sum for the whole column
     */
    private long getColSum(Short klass) {
        long result = 0;

        for (Map<Short, Long> row : this.matrix.values()) {
            if (row.containsKey(klass)) {
                result += row.get(klass);
            }
        }

        return result;
    }

    /**
     * Get the sum for the row of the specifid class
     * 
     * @param klass row to be sum
     * @return the sum for the whole row
     */
    private long getRowSum(Short klass) {
        long result = 0;

        for (long row : matrix.get(klass).values()) {
            result += row;
        }

        return result;
    }
}