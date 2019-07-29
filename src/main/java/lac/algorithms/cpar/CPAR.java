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
package lac.algorithms.cpar;

import java.util.ArrayList;

import lac.algorithms.Algorithm;
import lac.data.Dataset;

/**
 * Main class for the algorithm CPAR. Please refer to the original publication
 * for more information on this algorithm. X. Yin and J. Han, “Cpar:
 * Classification based on predictive association rules,” in3rd SIAM
 * International Conference on Data Mining(SDM03), 2003, pp. 331–335.
 */
public class CPAR extends Algorithm {
    /**
     * Constructor
     * 
     * @param config used to train the classifier
     */
    public CPAR(Config config) {
        this.config = config;
    }

    /*
     * (non-Javadoc)
     * 
     * @see lac.algorithms.Algorithm#train(lac.data.Dataset)
     */
    public Classifier train(Dataset training) throws Exception {
        PRM prm = new PRM(training, (Config) config);

        ArrayList<Rule> rules = prm.run();

        return new Classifier(rules, (Config) config);
    }
}