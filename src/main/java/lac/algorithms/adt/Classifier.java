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
import java.util.Collections;
import java.util.Comparator;
import java.util.Map.Entry;

import lac.data.Dataset;
import lac.utils.Utils;

/**
 * Class used to represent the classifier from ADT algorithm
 */
public class Classifier extends lac.algorithms.Classifier {
    /**
     * Field used to store the dataset
     */
    private Dataset training;

    /**
     * Configuration used while generating the classifier
     */
    private Config config;

    /**
     * Constructor
     * 
     * @param rules    used as base to form the classifier
     * @param config   Configuration used to generate the classifier
     * @param training dataset used as training set
     */
    public Classifier(ArrayList<Rule> rules, Config config, Dataset training) {
        this.training = training;
        this.config = config;

        Collections.sort(rules, new Comparator<Rule>() {
            public int compare(Rule arg0, Rule arg1) {
                if (Double.compare(arg0.getConfidence(), arg1.getConfidence()) != 0) {
                    return -Double.compare(arg0.getConfidence(), arg1.getConfidence());
                } else if (Double.compare(arg0.getSupportRule(), arg1.getSupportRule()) != 0) {
                    return -Double.compare(arg0.getSupportRule(), arg1.getSupportRule());
                } else if (Integer.compare(arg0.size(), arg1.size()) != 0) {
                    return Integer.compare(arg0.size(), arg1.size());
                } else {
                    // Lexicography order
                    for (int i = 0; i < arg0.size(); i++) {
                        short x = arg0.getAntecedent().get(i);
                        short y = arg1.getAntecedent().get(i);
                        if (Integer.compare(x, y) != 0)
                            return Integer.compare(x, y);
                    }
                    return Integer.compare(arg0.getKlass(), arg1.getKlass());
                }
            }
        });

        // Remove redundant rules
        rules = removeRedundant(rules);

        // Once rules are ranked, we need to calculate N(v) and E(v) for each node
        // having into account the previous order
        for (int indexInstance = 0; indexInstance < this.training.size(); indexInstance++) {
            Short[] instance = this.training.getInstance(indexInstance).asNominal();

            boolean match = false;
            for (int i = 0; i < rules.size() && !match; i++) {
                Rule rule = rules.get(i);

                if (rule.matching(instance)) {
                    match = true;
                    rule.addCoveredInstance(indexInstance);

                    if (rule.getKlass() == this.training.getKlassInstance(indexInstance)) {
                        rule.incrementHits();
                    } else {
                        rule.incrementMisses();
                    }
                }
            }
        }

        Rule defaultRule = extractDefaultRule();

        ADNode parent = new ADNode(defaultRule);

        for (int m = rules.size() - 1; m >= 0; m--) {
            ADNode tmpParent = parent;
            ADNode auxNode;

            Rule rule = rules.get(m);

            while ((auxNode = tmpParent.isChild(rule)) != null) {
                tmpParent = auxNode;
            }

            ADNode newNode = new ADNode(rule);
            newNode.parent = tmpParent;
            tmpParent.childs.add(newNode);
        }

        // Prune tree using pessimistic error estimate
        prune(parent);

        // transfrom from tree to list of rules to form the final classifier
        // while transformation is being done, merit is also calculate, and
        // rule do not satisfying the user-threshold are removed
        this.rules = transfromTreeToRules(parent);
    }

    /**
     * Transforms the tree to an array of rules while filtering by minMerit
     * 
     * @param node used as parent to filter
     * @return array with all the rules
     */
    private ArrayList<lac.algorithms.Rule> transfromTreeToRules(ADNode node) {
        ArrayList<lac.algorithms.Rule> rules = new ArrayList<>();

        for (int i = node.childs.size() - 1; i >= 0; i--) {
            rules.addAll(transfromTreeToRules(node.childs.get(i)));
        }

        if (node.rule.getMerit() >= this.config.getMinMerit())
            rules.add(node.rule);

        return rules;
    }

    /**
     * Performs pruning of the tree
     * 
     * @param node being pruned
     */
    private void prune(ADNode node) {
        if (node == null || node.childs.isEmpty())
            return;

        for (int i = 0; i < node.childs.size(); i++) {
            prune(node.childs.get(i));
        }

        ADNode leafNode = (ADNode) node.clone();
        // Check errors if this rule was acting as leaf
        double leafErrors = calculatePessimisticErrorEstimate(leafNode);

        // Calculate future errors if leaf are removed
        double treeErrors = node.rule.getPessimisticErrorEstimate();
        for (int i = 0; i < node.childs.size(); i++) {
            treeErrors += node.childs.get(i).rule.getPessimisticErrorEstimate();
        }

        if (leafErrors < treeErrors) {
            node.childs.clear();
            // Replace rule, to replace E and N
            node.rule = leafNode.rule;
        }

    }

    /**
     * Recalculates the pessimistic error rate for the specified node
     * 
     * @param node being used to recalculate per
     * @return the pessimistic error rate for this node
     */
    private double calculatePessimisticErrorEstimate(ADNode node) {
        for (int i = 0; i < node.childs.size(); i++) {
            ArrayList<Integer> instances = node.childs.get(i).rule.getCoveredInstances();

            for (int j = 0; j < instances.size(); j++) {
                Integer tid = instances.get(j);

                Short[] example = this.training.getInstance(tid).asNominal();
                if (node.rule.matching(example)) {
                    node.rule.addCoveredInstance(tid);
                    if (node.rule.getKlass() == this.training.getKlassInstance(tid)) {
                        node.rule.incrementHits();
                    } else {
                        node.rule.incrementMisses();
                    }
                }
            }
        }

        return node.rule.getPessimisticErrorEstimate();
    }

    /**
     * Extract the default rule, that is the majority class
     * 
     * @return the default rule
     */
    private Rule extractDefaultRule() {
        short majorityKlass = Collections
                .max(training.getFrequencyByKlass().entrySet(), Comparator.comparingLong(Entry::getValue)).getKey();

        return new Rule(majorityKlass);
    }

    /**
     * Removes redundant rule
     * 
     * @param rules to be filtered
     * @return non-redundant rules
     */
    private ArrayList<Rule> removeRedundant(ArrayList<Rule> rules) {
        ArrayList<Rule> finalRules = new ArrayList<Rule>();

        for (int i = 0; i < rules.size(); i++) {
            Rule ruleI = rules.get(i);

            boolean isGeneral = true;
            for (int j = 0; j < finalRules.size() && isGeneral; j++) {
                Rule ruleJ = finalRules.get(j);

                if (Utils.isSubset(ruleI.getAntecedent(), ruleJ.getAntecedent())) {
                    isGeneral = false;
                }
            }

            if (isGeneral) {
                finalRules.add(ruleI);
            }
        }

        return finalRules;
    }
}
