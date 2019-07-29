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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import lac.data.Dataset;

/**
 * Class used to mine rules from the training dataset
 */
public class ExtractorRules {
    /**
     * Configuration used to extract rules
     */
    private Config config;

    /**
     * Dataset used for the training phase
     */
    private Dataset dataset;

    /**
     * Constructor
     * 
     * @param training dataset used as training
     * @param config   used to obtain rules
     */
    public ExtractorRules(Dataset training, Config config) {
        this.dataset = training;
        this.config = config;
    }

    /**
     * Obtain rules from the training dataset
     * 
     * @return the rules
     */
    @SuppressWarnings("unchecked")
    public ArrayList<Rule> run() {
        ArrayList<Rule> rules = this.generateK();

        List<Rule> candidates = new ArrayList<Rule>();
        HashMap<Rule, Long> candidatesH = new HashMap<Rule, Long>();

        for (int i = 0; i < rules.size(); i++) {
            ArrayList<Short> antecedent = rules.get(i).getAntecedent();

            for (int j = 0; j < antecedent.size(); j++) {
                ArrayList<Short> newAntecedent = (ArrayList<Short>) antecedent.clone();
                newAntecedent.remove(j);

                Rule newRule = new Rule(newAntecedent, rules.get(i).getKlass());
                if (!candidates.contains(newRule))
                    candidates.add(newRule);
            }
        }
        // Evaluate candidates and count support
        for (int i = 0; i < candidates.size(); i++) {
            Rule rule = candidates.get(i);
            rule.calculateSupports(dataset);
        }
        candidates.removeIf(rule -> rule.getConfidence() < this.config.getMinConf());
        rules.addAll(candidates);

        do {
            int k = candidates.get(0).getAntecedent().size() - 1;
            for (int i = 0; i < candidates.size(); i++) {
                ArrayList<Short> antecedent = candidates.get(i).getAntecedent();

                for (int j = 0; j < antecedent.size(); j++) {
                    ArrayList<Short> newAntecedent = (ArrayList<Short>) antecedent.clone();
                    newAntecedent.remove(j);

                    Rule newRule = new Rule(newAntecedent, candidates.get(i).getKlass());
                    Long count = candidatesH.getOrDefault(newRule, 0L);

                    candidatesH.put(newRule, count + 1);
                }
            }

            // To consider one candidate, it must occur at least k times. Where k is the
            // size
            // of the antecedent being mined
            candidates = candidatesH.entrySet().stream().filter(candidate -> candidate.getValue() >= k)
                    .map(candidate -> candidate.getKey()).collect(Collectors.toList());

            candidatesH.clear();
            // Evaluate candidates and count support
            for (int i = 0; i < candidates.size(); i++) {
                Rule rule = candidates.get(i);
                rule.calculateSupports(dataset);
            }
            candidates.removeIf(rule -> rule.getConfidence() < this.config.getMinConf());
            rules.addAll(candidates);
            if (k == 1)
                break;
        } while (!candidates.isEmpty() && candidates.get(0).getAntecedent().size() > 1);

        return rules;
    }

    /**
     * Generates rules of size k
     * 
     * @return candidate rules of size k
     */
    private ArrayList<Rule> generateK() {
        ArrayList<Rule> rules = new ArrayList<Rule>();

        HashMap<ArrayList<Short>, ArrayList<Integer>> antecedentIndex = new HashMap<ArrayList<Short>, ArrayList<Integer>>();

        for (int i = 0; i < dataset.size(); i++) {
            Short[] antecedent = Arrays.copyOfRange(dataset.getInstance(i).asNominal(), 0,
                    this.dataset.getNumberAttributes());
            ArrayList<Short> antecedentArray = new ArrayList<Short>(Arrays.asList(antecedent));
            short klass = dataset.getKlassInstance(i);

            Rule rule = new Rule(antecedent, klass);

            if (antecedentIndex.containsKey(antecedentArray) && rules.contains(rule)) {
                ArrayList<Integer> indexAntecedents = antecedentIndex.getOrDefault(antecedent,
                        new ArrayList<Integer>());
                int index = rules.lastIndexOf(rule);

                rules.get(index).incrementSupportRule();
                rules.get(index).incrementSupportAntecedent();
                for (int j = 0; j < indexAntecedents.size(); j++) {
                    index = indexAntecedents.get(j);
                    rules.get(index).incrementSupportAntecedent();
                }
                antecedentIndex.put(antecedentArray, indexAntecedents);
            } else {
                rules.add(rule);
                rule.incrementSupportAntecedent();
                rule.incrementSupportRule();

                ArrayList<Integer> indexAntecedents = antecedentIndex.getOrDefault(antecedent,
                        new ArrayList<Integer>());
                indexAntecedents.add(rules.size() - 1);

                antecedentIndex.put(antecedentArray, indexAntecedents);
            }
        }

        // Filter only for those rules whose confidence is greater than the threshold
        rules.removeIf(rule -> rule.getConfidence() < this.config.getMinConf());

        return rules;
    }
}
