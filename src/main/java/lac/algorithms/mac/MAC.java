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
package lac.algorithms.mac;

import java.util.ArrayList;

import lac.algorithms.Algorithm;
import lac.data.Dataset;

/**
 * Main class for the algorithm MAC. Please refer to the original publication
 * for more information on this algorithm. N. Abdelhamid, A. Ayesh, F. Thabtah,
 * S. Ahmadi, and W. Hadi, “Mac: A multiclassassociative classification
 * algorithm,”Journal of Information & Knowledge Manage-ment, vol. 11, 06 2012.
 */
public class MAC extends Algorithm {

    /**
     * Default constructor for this algorithm
     * 
     * @param config Configuration to be used while training this algorithm
     */
    public MAC(Config config) {
        this.config = config;
    }

    /*
     * (non-Javadoc)
     * 
     * @see lac.algorithms.Algorithm#train(lac.data.Dataset)
     */
    @Override
    public Classifier train(Dataset training) throws Exception {
        Eclat eclat = new Eclat(training, (Config) this.config);

        ArrayList<Rule> rules = eclat.run();

        return new Classifier(training, rules);
    }
}