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
package lac.runner;

import lac.algorithms.Classifier;
import lac.data.Dataset;
import lac.data.Instance;

/**
 * This class has the logic for the basic information shown in STDOUT when an
 * algorithm is run.
 */
public class BasicReportExecution {

    /**
     * Field used to store the starting time
     */
    private long startTime;

    /**
     * Field used to store the stop time
     */
    private long stopTime;

    /**
     * Field used to store the accuracy of the classifier
     */
    private double accuracy;

    /**
     * Default constructor
     */
    public BasicReportExecution() {
        startTime = 0;
        stopTime = 0;
        accuracy = 0.0;
    }

    /**
     * Calculate accuracy for a dataset making use of the specified classifier
     * 
     * @param dataset    train or test dataset to calculate accuracy
     * @param classifier to be used when calculating accuracy
     */
    public void calculateAccuracy(Dataset dataset, Classifier classifier) {
        for (int i = 0; i < dataset.size(); i++) {
            Instance example = dataset.getInstance(i);

            short prediction = classifier.predict(example);

            if (prediction == dataset.getKlassInstance(i))
                accuracy += 1.0;
        }

        accuracy /= dataset.size();
    }

    /**
     * Get accuracy of the classifier
     * 
     * @return accuracy of the classifier
     */
    public double getAccuracy() {
        return accuracy;
    }

    /**
     * Get the total time in ms
     * 
     * @return total time in ms
     */
    public long getTotalTime() {
        return this.stopTime - this.startTime;
    }

    /**
     * Set the starting time
     */
    public void startTime() {
        this.startTime = System.currentTimeMillis();
    }

    /**
     * Set the finished time
     */
    public void stopTime() {
        this.stopTime = System.currentTimeMillis();
    }
}
