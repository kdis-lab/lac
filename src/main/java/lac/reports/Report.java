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
package lac.reports;

import lac.algorithms.Classifier;
import lac.data.Dataset;
import lac.runner.ConfigExecution;

/**
 * Base class used for the reports of LAC
 */
public abstract class Report {
    /**
     * Dataset used for the training phase
     */
    protected Dataset training;

    /**
     * Dataset used for the test phase
     */
    protected Dataset test;

    /**
     * Classifier to be used on the report
     */
    protected Classifier classifier;

    /**
     * Base constructor for the reports of LAC
     * 
     * @param config config of the execution to be reported
     */
    public Report(ConfigExecution config) {
        this.training = config.getTraining();
        this.test = config.getTest();
        this.classifier = config.getClassifier();
    }

    /**
     * Method to be implemented in children class to store the report on disk
     * 
     * @param path of the report path
     */
    public abstract void write(String path);
}
