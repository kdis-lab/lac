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
package lac.algorithms.l3;

import java.util.ArrayList;

import lac.algorithms.Algorithm;
import lac.data.Dataset;

/**
 * Main class for the algorithm L3. Please refer to the original publication for
 * more information on this algorithm. E. Baralis and P. Garza, “A lazy approach
 * to pruning classification rules,” inProceedings of the 2002 IEEE
 * International Conference on Data Mining, ser. ICDM’02. Washington, DC, USA:
 * IEEE Computer Society, 2002, pp. 35–.
 */
public class L3 extends Algorithm {
    /**
     * Default constructor
     * 
     * @param config Configuration used to train this classifier
     */
    public L3(Config config) {
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
        FPGrowth fpgrowthMultiple = new FPGrowth(training, config.getMinSup(), config.getMinConf());

        ArrayList<lac.algorithms.Rule> rules = fpgrowthMultiple.run();

        return new Classifier(training, rules);
    }
}