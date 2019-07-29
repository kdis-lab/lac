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

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import lac.data.Dataset;
import lac.data.Instance;
import lac.metrics.ConfusionMatrix;
import lac.runner.ConfigExecution;

/**
 * Class with the logic to generate a report of metrics of the quality of the
 * classifier. It makes use of the confusion matrix to delegate the calculation
 */
public class MetricsReport extends Report {
    /**
     * Runtime for the training phase
     */
    private long runtimeTraining;

    /**
     * Runtime for the test phase
     */
    private long runtimeTest;

    /**
     * Default constructor
     * 
     * @param config for this execution being reported
     */
    public MetricsReport(ConfigExecution config) {
        super(config);

        this.runtimeTraining = config.getTrainReport().getTotalTime();
        this.runtimeTest = config.getTestReport().getTotalTime();
    }

    /*
     * (non-Javadoc)
     * 
     * @see lac.reports.Report#write(java.lang.String)
     */
    @Override
    public void write(String reportPath) {
        writeResults(this.training, reportPath + ".training");
        writeResults(this.test, reportPath + ".test");
    }

    /**
     * Write results in the path specified as parameter and using the dataset
     * specified as argument. It also calls to the confusion matrix to generate all
     * the metrics stored in the report
     * 
     * @param dataset    to be used while calculating the measures
     * @param reportPath path where the report has to be saved
     */
    private void writeResults(Dataset dataset, String reportPath) {
        try {
            PrintWriter writer = new PrintWriter(reportPath, "UTF-8");

            ConfusionMatrix matrix = new ConfusionMatrix();
            for (int i = 0; i < dataset.size(); i++) {
                Instance example = dataset.getInstance(i);
                short predictedKlass = this.classifier.predict(example);
                short realKlass = dataset.getKlassInstance(i);

                matrix.add(realKlass, predictedKlass);
            }

            writer.println("Name metric, Value");
            writer.println("Accuracy, " + matrix.getAccuracy());
            writer.println("Kappa, " + matrix.getKappa());
            writer.println("Recall, " + matrix.getAverageRecall());
            writer.println("Precision, " + matrix.getAveragePrecision());
            writer.println("F-measure(micro), " + matrix.getMicroFMeasure());
            writer.println("F-measure(macro), " + matrix.getMacroFMeasure());
            writer.println("Number rules, " + this.classifier.getNumberRules());
            writer.println("Average number attributes, " + this.getAverageNumberAttributes());
            writer.println("Runtime training (ms)," + this.runtimeTraining);
            writer.println("Runtime test (ms)," + this.runtimeTest);

            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the average number of attributes in all the rules
     * 
     * @return the average number of attributes in all the classifier
     */
    private double getAverageNumberAttributes() {
        double avg = 0;

        for (int i = 0; i < this.classifier.getNumberRules(); i++) {
            avg += this.classifier.getRules().get(i).getAntecedent().size();
        }

        avg /= this.classifier.getNumberRules();

        return avg;
    }
}
