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
package lac.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class containing the logic to read an ARFF format. The specification for this
 * file could be found on the following link:
 * https://www.cs.waikato.ac.nz/~ml/weka/arff.html
 */
public class ArffDataset extends Dataset {
    /**
     * Character used in ARFF to specify a comment
     */
    private static String COMMENT_CHAR1 = "%";

    /**
     * Character used in ARFF to specify a comment
     */
    private static String COMMENT_CHAR2 = "#";

    /**
     * Character used in ARFF to specify that a line contains meta-information
     */
    private static String META_CHAR = "@";

    /**
     * Separator used to separate each value in the instances
     */
    private static String SEPARATOR = ",";

    /**
     * Starts of the line containing meta information of the name of the dataset
     */
    private static String RELATION = "@relation";

    /**
     * Starts of the line containing meta information on an attribute
     */
    private static String ATTRIBUTE = "@attribute";

    /**
     * Name of the attribute containing the class
     */
    private static String KLASS = "class";

    /**
     * Constructor for the dataset. It reads from disk the dataset, and generates an
     * object in main memory with all the information contained in the dataset
     * 
     * @param path of the dataset to be read
     * @throws Exception
     */
    public ArffDataset(String path) throws Exception {
        super();

        try {
            FileReader fileReader = new FileReader(path);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                // Remove empty spaces
                line = line.trim();

                // Ignore empty lines
                // Ignore lines with comments
                if (line.isEmpty() || this.isComment(line)) {
                    continue;
                    // Metadata information
                } else if (line.startsWith(META_CHAR)) {
                    this.proccessMetadata(line);
                } else {
                    // Check if no class was set, the last attribute will be used
                    if (this.getKlass() == null) {
                        Attribute attrKlass = this.getAttribute(this.getNumberAttributes() - 1);
                        this.attributes.remove(this.getNumberAttributes() - 1);
                        this.indexes.remove(this.getNumberAttributes());
                        this.addKlass(attrKlass.getValues());
                    }
                    this.proccessData(line);
                }
            }

            bufferedReader.close();
        } catch (Exception e) {
            System.err.println("File " + path + " cannot be found.");
            throw e;
        }
    }

    /**
     * Receives a line containing meta information of the dataset, and delegates its
     * treatment to another low level method
     * 
     * @param line containing the meta information
     */
    private void proccessMetadata(String line) {
        if (line.toLowerCase().startsWith(RELATION)) {
            this.name = line.replace(RELATION + " ", "");
        } else if (line.toLowerCase().startsWith(ATTRIBUTE)) {
            this.processAttribute(line);

        }
    }

    /**
     * Process a line containing information of an attribute
     * 
     * @param line containing the information of the attribute
     */
    private void processAttribute(String line) {
        Pattern p = Pattern.compile("@attribute .*\\{(.*)\\}");
        Matcher m = p.matcher(line.toLowerCase());

        String[] splitted = line.replaceAll(" +", " ").split(" ");
        String nameAttribute = splitted[1];

        // Nominal attributes in weka starts with {, to list all the possible values
        if (m.matches()) {
            String[] values = m.group(1).replaceAll(" *", "").split(",");

            if (nameAttribute.contains(KLASS)) {
                this.addKlass(values);
            } else {
                this.addNominalAttribute(nameAttribute, values);
            }
        } else {
            this.addNumericAttribute(nameAttribute);
        }

    }

    /**
     * Process each one of the lines contained in the dataset
     * 
     * @param line to be persited on the object being generated in main memory
     * @throws Exception
     */
    private void proccessData(String line) throws Exception {
        String[] values = line.split(SEPARATOR);

        this.addInstance(values);
    }

    /**
     * Check if a line passed as parameter is a comment or not
     * 
     * @param line to check if it is a comment
     * @return true if line is a comment, false otherwise
     */
    private boolean isComment(String line) {
        return line.startsWith(COMMENT_CHAR1) || line.startsWith(COMMENT_CHAR2);
    }
}
