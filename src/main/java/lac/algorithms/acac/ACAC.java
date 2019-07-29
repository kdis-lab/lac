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
package lac.algorithms.acac;

import java.util.ArrayList;

import lac.algorithms.Algorithm;
import lac.data.Dataset;

/**
 * Main class for the algorithm ACAC. Please refer to the original publication
 * for more information on this algorithm. Z. Huang, Z. Zhou, T. He, and X.
 * Wang, "Acac: Associative classification based on all-confidence", 11 2011,
 * pp. 289–293.
 */
public class ACAC extends Algorithm {
    /**
     * Main constructor
     * 
     * @param config
     */
    public ACAC(Config config) {
        this.config = config;
    }

    /*
     * (non-Javadoc)
     * 
     * @see lac.algorithms.Algorithm#train(lac.data.Dataset)
     */
    @Override
    public Classifier train(Dataset training) throws Exception {
        Apriori apriori = new Apriori(training, (Config) this.config);

        ArrayList<Rule> rules = apriori.run();

        return new Classifier(rules);
    }
}
