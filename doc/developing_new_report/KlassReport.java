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
import lac.runner.ConfigExecution;

/**
 * Report where both the predicted and the real class are stored on disk
 */
public class KlassReport extends Report {
    /**
     * Constructor used to generate the reports
     * 
     * @param execution config of the execution being reported
     */
    public KlassReport(ConfigExecution execution) {
        super(execution);
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
     * Write the report on disk in the patch specified as parameter, and using the
     * dataset also specified as argument
     * 
     * @param dataset
     * @param reportPath
     */
    private void writeResults(Dataset dataset, String reportPath) {
        try {
            PrintWriter writer = new PrintWriter(reportPath, "UTF-8");

            for (int i = 0; i < dataset.size(); i++) {
                Instance example = dataset.getInstance(i);
                short predictedKlassIndex = this.classifier.predict(example);
                short realKlassIndex = dataset.getKlassInstance(i);

                String realKlass = dataset.getValueByIndex(realKlassIndex);
                String predictedKlass = dataset.getValueByIndex(predictedKlassIndex);

                writer.println(realKlass + "," + predictedKlass);
            }
            writer.close();

        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
