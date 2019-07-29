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

import lac.algorithms.Rule;
import lac.data.Attribute;
import lac.runner.ConfigExecution;

/**
 * Report where the whole classifier is stored on disk. It is designed to store
 * in a text file each rule forming the classifier. The final file will have one
 * line per rule contained in the dataset
 */
public class ClassifierReport extends Report {
    /**
     * Constructor for this report
     * 
     * @param config of the execution being reported
     */
    public ClassifierReport(ConfigExecution config) {
        super(config);
    }

    /*
     * (non-Javadoc)
     * 
     * @see lac.reports.Report#write(java.lang.String)
     */
    public void write(String outputPath) {
        try {
            PrintWriter writer = new PrintWriter(outputPath + ".classifier", "UTF-8");

            for (int i = 0; i < this.classifier.getNumberRules(); i++) {
                Rule rule = this.classifier.getRules().get(i);

                // Transform from rule codified with short to string values
                String[] antecedent = new String[rule.getAntecedent().size()];
                for (int j = 0; j < antecedent.length; j++) {
                    Attribute attr = this.training.getAttributeByIndex(rule.getAntecedent().get(j));
                    antecedent[j] = attr.getName() + "=" + this.training.getValueByIndex(rule.getAntecedent().get(j));
                }

                String klass = this.training.getValueByIndex(rule.getKlass());
                writer.println(String.join(" ", antecedent) + " => " + klass);
            }
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
