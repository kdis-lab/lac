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
 *
 * This algorithm was taken from the SPMF Library, which is is licensed 
 * under the GNU GPL v3 license.
 * Fournier-Viger, P., Lin, C.W., Gomariz, A., Gueniche, T., Soltani, A., 
 * Deng, Z., Lam, H. T. (2016). The SPMF Open-Source Data Mining Library 
 * Version 2. Proc. 19th European Conference on Principles of Data Mining 
 * and Knowledge Discovery (PKDD 2016) Part III, Springer LNCS 9853, pp. 36-40.
 */
package lac.algorithms.l3;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

import lac.data.Dataset;

/**
 * This is an adaptation of FPGrowth to extract class association rules. More
 * information on the original proposal could be found at: J. Han, J. Pei, Y.
 * Yin, and R. Mao, “Mining frequent patterns without candidate gen-eration: A
 * frequent-pattern tree approach,”Data Mining and Knowledge Discovery,vol. 8,
 * no. 1, pp. 53–87, Jan 2004. It includes a couple of differences with regard
 * to the original proposal:
 * <ul>
 * <li>It searches for class association rules</li>
 * <li>No patterns are generated, but rules are mined directly without an
 * intermediary step for searching for patterns</li>
 * <li>It includes multiple minimum support in function of the class</li>
 * </ul>
 */
public class FPGrowth extends lac.algorithms.cmar.FPGrowth {
    /**
     * It stores the multiple minimum support by class
     */
    private HashMap<Short, Long> supportByKlass;

    /**
     * Constructor
     * 
     * @param training Dataset used to extract class association rules
     * @param minSup   minimum support used to calculate the minimum support by
     *                 class
     * @param minConf  minimum confidence for the mined rules
     */
    public FPGrowth(Dataset training, double minSup, double minConf) {
        super(training, minSup, minConf);

        supportByKlass = new HashMap<Short, Long>();
        for (Entry<Short, Long> entry : training.getFrequencyByKlass().entrySet()) {
            supportByKlass.put(entry.getKey(), (long) Math.ceil(entry.getValue() * minSup));
        }
    }

    @Override
    protected void generateRules(short[] itemset, int itemsetLength, long support,
            HashMap<Short, Long> counterByKlass) {
        short[] itemsetOutputBuffer = new short[itemsetLength];
        System.arraycopy(itemset, 0, itemsetOutputBuffer, 0, itemsetLength);
        Arrays.sort(itemsetOutputBuffer, 0, itemsetLength);

        for (Entry<Short, Long> entry : counterByKlass.entrySet()) {
            Rule rule = new Rule(itemsetOutputBuffer, entry.getKey());
            rule.setSupportAntecedent(support);
            rule.setSupportRule(entry.getValue());
            rule.setSupportKlass(dataset.getFrequencyByKlass().get(rule.getKlass()));

            if (rule.getSupportRule() >= this.supportByKlass.get(rule.getKlass())
                    && rule.getConfidence() >= this.minConf)
                rules.add(rule);
        }
    }
}
