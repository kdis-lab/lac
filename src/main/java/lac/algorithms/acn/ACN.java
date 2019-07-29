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
package lac.algorithms.acn;

import java.util.ArrayList;

import lac.algorithms.Algorithm;
import lac.data.Dataset;

/**
 * Main class for the algorithm ACN. Please refer to the original publication G.
 * Kundu, M. M. Islam, S. Munir, and M. F. Bari, “Acn: An associative
 * classifierwith negative rules,” in 2008 11th IEEE International Conference on
 * ComputationalScience and Engineering, July 2008, pp. 369–375.
 */
public class ACN extends Algorithm {

    /**
     * Default constructor
     * 
     * @param config for this algorithm
     */
    public ACN(Config config) {
        this.config = config;
    }

    /*
     * (non-Javadoc)
     * 
     * @see lac.algorithms.Algorithm#train(lac.data.Dataset)
     */
    @Override
    public Classifier train(Dataset training) throws Exception {
        AprioriNegative apriori = new AprioriNegative(training, (Config) config);

        ArrayList<Rule> rules = apriori.run();

        return new Classifier(rules, training, (Config) config);
    }

}
