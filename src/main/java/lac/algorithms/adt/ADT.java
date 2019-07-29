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
package lac.algorithms.adt;

import java.util.ArrayList;

import lac.data.Dataset;

/**
 * Main class for the ADT algorithm. K. Wang, S. Zhou, and Y. He, “Growing
 * decision trees on support-less associa-tion rules,” inProceedings of the
 * Sixth ACM SIGKDD International Conference onKnowledge Discovery and Data
 * Mining, ser. KDD ’00. New York, NY, USA: ACM,2000, pp. 265–269.
 */
public class ADT extends lac.algorithms.Algorithm {
    /**
     * Constructor
     * 
     * @param config configuration used to obtain rules
     */
    public ADT(Config config) {
        this.config = config;
    }

    /*
     * (non-Javadoc)
     * 
     * @see lac.algorithms.Algorithm#train(lac.data.Dataset)
     */
    @Override
    public Classifier train(Dataset training) throws Exception {
        ExtractorRules extractor = new ExtractorRules(training, (Config) config);

        ArrayList<Rule> rules = extractor.run();

        return new Classifier(rules, (Config) config, training);
    }

}
