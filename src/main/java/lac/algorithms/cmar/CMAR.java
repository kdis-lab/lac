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
package lac.algorithms.cmar;

import java.util.ArrayList;

import lac.algorithms.Algorithm;
import lac.data.Dataset;

/**
 * Main class for the algorithm CMAR. Please refer to the original publication
 * for more information on this algorithm. W. Li, J. Han, and J. Pei, “Cmar:
 * Accurate and efficient classification based on multiple class-association
 * rules,” in 2002 IEEE International Conference on DataMining(ICDM01), 2001,
 * pp. 369–376
 */
public class CMAR extends Algorithm {
    /**
     * Default constructor
     * 
     * @param config
     */
    public CMAR(Config config) {
        this.config = config;
    }

    /*
     * (non-Javadoc)
     * 
     * @see lac.algorithms.Algorithm#train(lac.data.Dataset)
     */
    @Override
    public Classifier train(Dataset training) throws Exception {
        Config config = (Config) this.config;
        FPGrowth fpgrowth = new FPGrowth(training, config.getMinSup(), config.getMinConf());

        ArrayList<lac.algorithms.Rule> rules = fpgrowth.run();

        return new Classifier(rules, training, config);
    }
}