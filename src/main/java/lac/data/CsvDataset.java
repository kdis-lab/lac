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
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class containing the logic to read dataset in comma-separated values. RFC for
 * this format could be fount at: https://tools.ietf.org/html/rfc4180
 */
public class CsvDataset extends Dataset {
    /**
     * Separator for each attribute per line
     */
    private static String SEPARATOR = ",";

    /**
     * Name for the class in the dataset 
     */
    private static String KLASS = "class";

    /**
     * Constructor
     * 
     * @param path
     * @throws Exception
     */
    public CsvDataset(String path) throws Exception {
        super();

        try {
            // Set the name of the dataset by the path
            String[] paths = path.split(".+?/(?=[^/]+$)");
            this.name = paths[paths.length - 1];

            FileReader fileReader = new FileReader(path);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            // Read the header line
            String header = bufferedReader.readLine().replaceAll(" *", "");

            // Obtains the name of the attributes from the header
            String[] nameAttributes = header.split(",");

            // Used to save the metadata information for all the attributes
            ArrayList<ArrayList<String>> metadataAttributes = new ArrayList<ArrayList<String>>();
            for (int i = 0; i < nameAttributes.length; i++) {
                metadataAttributes.add(new ArrayList<String>());
            }

            ArrayList<String[]> lines = new ArrayList<String[]>();
            // Read all the dataset, each different value is saved in metadata
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                // Remove empty spaces
                line = line.replaceAll(" *", "");

                String[] values = line.split(SEPARATOR);

                if (line.isEmpty() || values.length <= 0)
                    continue;

                // For each value for this instance, save it in the metadata information
                for (int i = 0; i < values.length; i++) {
                    if (!metadataAttributes.get(i).contains(values[i]))
                        metadataAttributes.get(i).add(values[i]);
                }

                lines.add(values);
            }

            // Once all the different values have been saved, start iterating to find which
            // kind of attribute
            for (int i = 0; i < metadataAttributes.size(); i++) {
                // Check if is numeric
                boolean isNumeric = true;
                for (int j = 0; j < metadataAttributes.get(i).size() && isNumeric; j++) {
                    if (!isNumeric(metadataAttributes.get(i).get(j)))
                        isNumeric = false;
                }

                String nameAttribute = nameAttributes[i];

                if (isNumeric) {
                    this.addNumericAttribute(nameAttributes[i]);
                } else {
                    String[] values = new String[metadataAttributes.get(i).size()];
                    metadataAttributes.get(i).toArray(values);
                    Arrays.sort(values);

                    if (nameAttribute.contains(KLASS)) {
                        this.addKlass(values);
                    } else {
                        this.addNominalAttribute(nameAttribute, values);
                    }
                }
            }

            // After setting metadata, dataset is stored
            for (int i = 0; i < lines.size(); i++) {
                this.addInstance(lines.get(i));
            }
            lines.clear();

            bufferedReader.close();
        } catch (Exception e) {
            System.out.println("File " + path + " cannot be found.");
            throw e;
        }
    }

    /**
     * Check if value is numeric or not
     * 
     * @param strNum check if value is numeric
     * @return true if value is numeric, false otherwise
     */
    private static boolean isNumeric(String strNum) {
        try {
            Double.parseDouble(strNum);
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }
        return true;
    }
}
