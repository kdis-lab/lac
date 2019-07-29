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
import lac.algorithms.Config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import lac.algorithms.Algorithm;
import lac.data.Dataset;
import lac.reports.Report;

/**
 * Class used to represent each execution which is or will be performed
 */
public class ConfigExecution {
    /**
     * Name of the algorithm to be executed
     */
    private String nameAlgorithm;

    /**
     * Configuration to be used while running this algorithm
     */
    private Config config;

    /**
     * Path for the train dataset
     */
    private String trainPath;

    /**
     * Path for the test dataset
     */
    private String testPath;

    /**
     * Dataset for the training phase
     */
    private Dataset training;

    /**
     * Dataset for the test phase
     */
    private Dataset test;

    /**
     * Path where the reports will be saved
     */
    private String reportPath;

    /**
     * Type of reports to be generated after training this algorithm
     */
    private ArrayList<String> reportType;

    /**
     * Trained classifier in this execution
     */
    private Classifier classifier;

    /**
     * Basic report for both train and test phases.
     */
    private BasicReportExecution trainReport, testReport;

    /**
     * Constructor for the config execution
     * 
     * @param nameAlgorithm name of the algorithm to be run. It only has to have the
     *                      name, no namespace is required.
     * @param config        The configuration to be used with the previously
     *                      specified algorithm
     * @param train         Training dataset
     * @param test          Test dataset
     * @param reportType    Types of reports to be generated after training the
     *                      classifier
     * @param report        Path where the reports will be stored
     */
    ConfigExecution(String nameAlgorithm, Config config, String train, String test, ArrayList<String> reportType,
            String report) {
        this.nameAlgorithm = nameAlgorithm;
        this.config = config;
        this.trainPath = train;
        this.testPath = test;
        this.reportPath = report;
        this.reportType = reportType;

        this.trainReport = new BasicReportExecution();
        this.testReport = new BasicReportExecution();
    }

    /**
     * Get the name of the algorithm being trained
     * 
     * @return the name of the algorithm
     */
    public String getNameAlgorithm() {
        return this.nameAlgorithm;
    }

    /**
     * Get the trained classifier
     * 
     * @return the generated classifier
     */
    public Classifier getClassifier() {
        return this.classifier;
    }

    /**
     * Get the basic report execution for the test phase. It stores the basic
     * information shown in STDOUT.
     * 
     * @return the basic report execution for the training phase
     */
    public BasicReportExecution getTrainReport() {
        return this.trainReport;
    }

    /**
     * Get the basic report execution for the test phase. It stores the basic
     * information shown in STDOUT.
     * 
     * @return the basic report execution for the test phase
     */
    public BasicReportExecution getTestReport() {
        return this.testReport;
    }

    /**
     * Get the training dataset
     * 
     * @return dataset for the training phase
     */
    public Dataset getTraining() {
        return this.training;
    }

    /**
     * Get the test dataset
     * 
     * @return dataset for the test phase
     */
    public Dataset getTest() {
        return this.test;
    }

    /**
     * It runs the configured algorithm. First, it trains the classifier using the
     * training dataset. Then, testing is performed using the test dataset. Finally,
     * all the reports (if they were defined) are generated
     * 
     * @throws Exception
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void run() throws Exception {
        String namePackage = this.nameAlgorithm.toLowerCase();
        Class algorithmKlass = Class.forName("lac.algorithms." + namePackage + "." + nameAlgorithm);
        Algorithm algorithm = (Algorithm) algorithmKlass.getDeclaredConstructor(this.config.getClass())
                .newInstance(this.config);

        // Training phase
        this.trainReport.startTime();
        this.training = Dataset.read(this.trainPath);
        algorithm.checkCompatibility(this.training);
        this.classifier = algorithm.train(training);
        this.trainReport.calculateAccuracy(training, this.classifier);
        this.trainReport.stopTime();

        // Test phase
        this.testReport.startTime();
        this.test = Dataset.read(this.testPath);
        this.testReport.calculateAccuracy(test, this.classifier);
        this.testReport.stopTime();

        // Shall we create some kind of reports?
        if (this.reportPath != null && this.reportType != null) {
            Path reportAsPath = Paths.get(this.reportPath);
            // Create reportPath
            if (!Files.exists(reportAsPath)) {
                Files.createDirectories(reportAsPath);
            }

            for (int i = 0; i < this.reportType.size(); i++) {
                Class reportKlass = Class.forName("lac.reports." + this.reportType.get(i));
                Report report = (Report) reportKlass.getDeclaredConstructor(ConfigExecution.class).newInstance(this);
                report.write(this.reportPath + "/" + this.reportType.get(i));
            }
        }

    }

}