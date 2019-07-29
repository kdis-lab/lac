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

import lac.data.Dataset;

/**
 * Class used to represent CR-Tree a data structure which enables to facilitate
 * the pruning of rules
 */
class CRTree {
    /**
     * Number of singletons in training dataset
     */
    protected static int NUMBER_SINGLETONS;

    /**
     * Root node for current tree
     */
    private CRNode rootNode = null;

    /**
     * Minimum times an instance need to be covered
     */
    private static int MIN_COVER = 4;

    /**
     * Critical threshold for 25% "significance" level (assuming "degree of freedom"
     * equivalent to 1).
     */
    private static final double THRESHOLD_20 = 1.6424;

    /**
     * Critical threshold value for CHI_SQUARE
     */
    private static double THRESHOLD_CHI_SQUARE = THRESHOLD_20; // Default

    /**
     * Dataset being used to generate CRTree
     */
    public Dataset dataset;

    /**
     * Constructor
     * 
     * @param dataset used to generate CRTree
     * @param config  configuration used to generate CRTree
     */
    CRTree(Dataset dataset, Config config) {
        MIN_COVER = config.getDelta();
        this.dataset = dataset;
    }

    /**
     * Insert a rule in current tree
     * 
     * @param baseRule being inserted into cRTree
     */
    protected void insert(lac.algorithms.Rule baseRule) {
        Rule rule = (Rule) baseRule;

        // Test rule using Chi-Squared testing
        if (rule.getChiSquare() <= THRESHOLD_CHI_SQUARE)
            return;

        // Create new node
        CRNode newNode = new CRNode(rule);

        // First rule being added to the tree
        if (rootNode == null) {
            rootNode = newNode;
            return;
        }

        // If more general rule with higher ranking exists, current rule could be
        // discarded
        if (isMoreGeneralNode(newNode))
            return;

        // Add current node as the first node
        if (newNode.rule.isGreater(rootNode.rule)) {
            newNode.next = rootNode;
            rootNode = newNode;
            return;
        }

        // Search position in function of ranking to be inserted
        CRNode currentNode = rootNode;
        CRNode nextNode = rootNode.next;
        while (nextNode != null) {
            if (newNode.rule.isGreater(nextNode.rule)) {
                currentNode.next = newNode;
                newNode.next = nextNode;
                return;
            }
            currentNode = nextNode;
            nextNode = nextNode.next;
        }

        // Add new node at the very end
        currentNode.next = newNode;
    }

    /**
     * Checks whether there are a more general rule, with higher ranking in current
     * tree
     * 
     * @param ruleNode to be inserted
     * @return true if more general rule exists
     */
    private boolean isMoreGeneralNode(CRNode ruleNode) {
        CRNode currentNode = rootNode;

        // Search in tree
        while (currentNode != null) {
            if (ruleNode.rule.isMoreGeneral(currentNode.rule) && ruleNode.rule.isGreater(currentNode.rule))
                return true;
            currentNode = currentNode.next;
        }

        return false;
    }

    /**
     * Prunes the current CRTree according to the cover principle
     */
    protected void pruneUsingCover() {
        Short[][] dataset = new Short[this.dataset.size()][];
        for (int i = 0; i < this.dataset.size(); i++) {
            dataset[i] = this.dataset.getInstance(i).asNominal();
        }

        // Number of times each instance is covered
        int[] numberTimesCovered = new int[this.dataset.size()];

        // Define rule list references
        CRNode newStart = null;
        CRNode markerRef = null;
        CRNode currentNode = rootNode;

        while (currentNode != null) {
            // If dataset is empty, there are no need to continue pruning
            if (isEmptyDataSet(dataset))
                break;

            boolean coveredFlag = false;
            for (int index = 0; index < dataset.length; index++) {
                // Increment the counter for each instance being covered
                if (currentNode.rule.matching(dataset[index])) {
                    numberTimesCovered[index]++;
                    coveredFlag = true;
                }
            }

            // If current rule has covered at least to one rule
            if (coveredFlag) {
                if (newStart == null)
                    newStart = currentNode;
                else
                    markerRef.next = currentNode;

                markerRef = currentNode;
                currentNode = currentNode.next;
                markerRef.next = null;
            } else
                currentNode = currentNode.next;

            // Remove examples already enough covered
            for (int index = 0; index < numberTimesCovered.length; index++) {
                if (numberTimesCovered[index] > MIN_COVER)
                    dataset[index] = null;
            }
        }

        rootNode = newStart;
    }

    /**
     * Check if specified dataset is empty
     * 
     * @param dataset
     * @return true if it is empty, or false otherwise
     */
    private boolean isEmptyDataSet(Short[][] dataset) {
        for (int index = 0; index < dataset.length; index++)
            if (dataset[index] != null)
                return false;

        return true;
    }

    /**
     * Returns the number of generated classification rules
     * 
     * @return array of rules
     */
    public ArrayList<lac.algorithms.Rule> getRules() {
        ArrayList<lac.algorithms.Rule> rules = new ArrayList<lac.algorithms.Rule>();
        CRNode currentNode = rootNode;

        while (currentNode != null) {
            rules.add(currentNode.rule);
            currentNode = currentNode.next;
        }

        return rules;
    }
}
