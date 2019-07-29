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
package lac.algorithms.cba2;

import java.util.ArrayList;

import lac.algorithms.Classifier;
import lac.algorithms.Algorithm;
import lac.algorithms.cba.CBAM2;
import lac.algorithms.cba.Rule;
import lac.data.Dataset;

/**
 * Main class for this algorithm. It is presented as an improvement of previous
 * CBA version. Please refer to the original publication for more details of
 * this algorithm: B. Liu, Y. Ma, and C. Wong,Classification Using Association
 * Rules: Weaknesses and Enhancements. Kluwer Academic Publishers, 2001, pp.
 * 591–601
 */
public class CBA2 extends Algorithm {
    /**
     * Default constructor
     * 
     * @param config used to train the algorithm
     */
    public CBA2(Config config) {
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
        CBAM2 cba = new CBAM2(training, rules);

        return cba.getClassifier();
    }
}