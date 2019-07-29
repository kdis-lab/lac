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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import lac.data.Dataset;

/**
 * This class has the logic for the adaptation of FPGrowth. This algorithm has
 * been adapted to obtain class association rules instead of patterns, whose
 * support and confidence are greater than its thresholds.
 */
public class FPGrowth {
    /**
     * Minimum relative support
     */
    private long minSupportRelative;// the relative minimum support

    /**
     * Minimum value for confidence value
     */
    protected double minConf;

    /**
     * Maximum size for the antecedent
     */
    final int MAX_SIZE_ANTECEDENT = 2000;

    /**
     * Buffer for storing nodes in single path of the tree
     */
    private FPNode[] fpNodeSingleBuffer = null;

    /**
     * Dataset used to generate rules
     */
    protected Dataset dataset;

    /**
     * Support for singletons
     */
    HashMap<Short, Long> mapSupport;

    /**
     * Support for each item by class
     */
    HashMap<Short, HashMap<Short, Long>> mapSupportByKlass;

    /**
     * Obtained rules
     */
    protected ArrayList<lac.algorithms.Rule> rules;

    /**
     * Constructor
     * 
     * @param training to be used to generate rules
     * @param minSup   minimum support for mined rules
     * @param minConf  minimum confidence for mined rules
     */
    public FPGrowth(Dataset training, double minSup, double minConf) {
        this.minSupportRelative = (long) Math.ceil(minSup * training.size());
        this.minConf = minConf;

        this.dataset = training;

        calculateSingletons();
    }

    /**
     * Run the algorithm to obtain class association rules
     * 
     * @return array with the rules
     */
    public ArrayList<lac.algorithms.Rule> run() {
        rules = new ArrayList<lac.algorithms.Rule>();

        FPTree tree = new FPTree();

        for (int i = 0; i < dataset.size(); i++) {
            List<Short> instance = new ArrayList<Short>();
            short klass = dataset.getKlassInstance(i);

            for (int j = 0; j < dataset.getNumberAttributes(); j++) {
                Short item = dataset.getInstance(i).asNominal()[j];

                // only add items that have the minimum support
                if (mapSupport.get(item) >= minSupportRelative) {
                    instance.add(item);
                }
            }

            // sort item in instance by descending order of support
            Collections.sort(instance, new Comparator<Short>() {
                public int compare(Short item1, Short item2) {
                    int compare = mapSupport.get(item2).compareTo(mapSupport.get(item1));

                    if (compare == 0) {
                        // When support is equal, lexical order is used
                        return (item1 - item2);
                    }
                    return compare;
                }
            });

            tree.addInstance(instance, klass);
        }

        // Create the header table for the tree
        tree.createHeaderList(mapSupport);

        // Start to mine rules recursively
        if (tree.headerList.size() > 0) {
            short[] antecedentBuffer = new short[MAX_SIZE_ANTECEDENT];
            fpNodeSingleBuffer = new FPNode[MAX_SIZE_ANTECEDENT];

            fpgrowth(tree, antecedentBuffer, 0, dataset.size(), dataset.getFrequencyByKlass(), mapSupport,
                    mapSupportByKlass);
        }

        return rules;
    }

    /**
     * Mines recursively a fp-tree
     * 
     * @param tree                 the FP-Tree being mined
     * @param prefix               for current prefix
     * @param prefixLength         the length of the current prefix
     * @param prefixSupport        support for current prefix
     * @param prefixSupportByKlass support by klass for current prefix
     * @param mapSupport           minimum support for current prefix
     * @param mapSupportByKlass    support class for current prefix
     */
    @SuppressWarnings("unchecked")
    private void fpgrowth(FPTree tree, short[] prefix, int prefixLength, long prefixSupport,
            HashMap<Short, Long> prefixSupportByKlass, HashMap<Short, Long> mapSupport,
            HashMap<Short, HashMap<Short, Long>> mapSupportByKlass) {
        // Check if the maximum size has been achieved
        if (prefixLength == MAX_SIZE_ANTECEDENT) {
            return;
        }

        // Check if current tree has a single path
        boolean singlePath = true;

        int numberSingleItems = 0;

        // if the root has more than one child, it is not a single path
        if (tree.root.childs.size() > 1) {
            singlePath = false;
        } else {
            // Otherwise,

            // if the root has exactly one child, we need to recursively check childs
            // of the child to see if they also have one child
            FPNode currentNode = tree.root.childs.get(0);
            while (true) {
                // if current child has more than one child, it isn't a single path!
                if (currentNode.childs.size() > 1) {
                    singlePath = false;
                    break;
                }
                // buffer will be used to store nodes in single path
                fpNodeSingleBuffer[numberSingleItems] = currentNode;

                numberSingleItems++;

                // if this node has no child, that means that this is the end of this path
                // and it is a single path
                if (currentNode.childs.size() == 0) {
                    break;
                }
                currentNode = currentNode.childs.get(0);
            }
        }

        if (singlePath) {
            // As tree has single path, this case is obvious, maximal itemset is stored
            saveAllCombinationsOfPrefixPath(fpNodeSingleBuffer, numberSingleItems, prefix, prefixLength);
        } else {
            // For each frequent item in the header table list of the tree in reverse order.
            for (int i = tree.headerList.size() - 1; i >= 0; i--) {
                Short item = tree.headerList.get(i);
                Long support = mapSupport.get(item);

                // Create Beta by concatening prefix by adding the current item
                prefix[prefixLength] = item;

                // calculate the support of the new prefix
                long betaSupport = (prefixSupport < support) ? prefixSupport : support;
                HashMap<Short, Long> supportByKlass = mapSupportByKlass.get(item);

                // save beta to the output file
                generateRules(prefix, prefixLength + 1, betaSupport, supportByKlass);

                if (prefixLength + 1 < MAX_SIZE_ANTECEDENT) {
                    // It is a subdataset containing a set of prefix paths in the FP-tree
                    // co-occuring with the prefix pattern.
                    List<List<FPNode>> prefixPaths = new ArrayList<List<FPNode>>();
                    FPNode path = tree.mapItemNodes.get(item);

                    // Map to count the support of items in the conditional prefix tree
                    HashMap<Short, Long> mapSupportBeta = new HashMap<Short, Long>();
                    HashMap<Short, HashMap<Short, Long>> mapSupportByKlassBeta = new HashMap<Short, HashMap<Short, Long>>();

                    while (path != null) {
                        // if the path is not just the root node
                        if (path.parent.item != -1) {
                            List<FPNode> prefixPath = new ArrayList<FPNode>();
                            prefixPath.add(path);
                            long pathCount = path.support;

                            // Recursively add all the parents of node
                            FPNode parent = path.parent;
                            while (parent.item != -1) {
                                prefixPath.add(parent);

                                // Support is updated for each item
                                if (mapSupportBeta.get(parent.item) == null) {
                                    mapSupportBeta.put(parent.item, pathCount);
                                } else {
                                    mapSupportBeta.put(parent.item, mapSupportBeta.get(parent.item) + pathCount);
                                }

                                if (mapSupportByKlassBeta.get(parent.item) == null) {
                                    mapSupportByKlassBeta.put(parent.item,
                                            (HashMap<Short, Long>) path.supportByklass.clone());
                                } else {
                                    HashMap<Short, Long> currentByKlass = mapSupportByKlassBeta.get(parent.item);
                                    for (Entry<Short, Long> entry : path.supportByklass.entrySet()) {
                                        Long count = currentByKlass.get(entry.getKey());

                                        if (count == null) {
                                            currentByKlass.put(entry.getKey(), entry.getValue());
                                        } else {
                                            currentByKlass.put(entry.getKey(), count + entry.getValue());
                                        }
                                    }
                                }

                                parent = parent.parent;
                            }
                            prefixPaths.add(prefixPath);
                        }
                        path = path.nextNode;
                    }

                    // Construct beta's conditional FP-Tree
                    FPTree treeBeta = new FPTree();
                    for (List<FPNode> prefixPath : prefixPaths) {
                        treeBeta.addPrefixPath(prefixPath, mapSupportBeta, minSupportRelative);
                    }

                    // Mine recursively the Beta tree if the root is not empty
                    if (treeBeta.root.childs.size() > 0) {
                        treeBeta.createHeaderList(mapSupportBeta);

                        fpgrowth(treeBeta, prefix, prefixLength + 1, betaSupport, supportByKlass, mapSupportBeta,
                                mapSupportByKlassBeta);
                    }
                }
            }
        }

    }

    /**
     * Saves all the rules for current prefix with enough support
     * 
     * @param fpNodeTempBuffer current tree
     * @param position         in current tree
     * @param prefix           prefix
     * @param prefixLength     length of current prefix
     */
    private void saveAllCombinationsOfPrefixPath(FPNode[] fpNodeTempBuffer, int position, short[] prefix,
            int prefixLength) {

        long support = 0;
        HashMap<Short, Long> supportByKlass = null;

        // Generates all subsets of the prefixPath except the empty set
        loop1: for (long i = 1, max = 1 << position; i < max; i++) {
            int newPrefixLength = prefixLength;

            for (int j = 0; j < position; j++) {
                int isSet = (int) i & (1 << j);

                // if yes, add the bit position as an item to the new subset
                if (isSet > 0) {
                    if (newPrefixLength == MAX_SIZE_ANTECEDENT) {
                        continue loop1;
                    }

                    prefix[newPrefixLength++] = fpNodeTempBuffer[j].item;
                    support = fpNodeTempBuffer[j].support;
                    supportByKlass = fpNodeTempBuffer[j].supportByklass;
                }
            }

            // Generate rules for current antecedent
            generateRules(prefix, newPrefixLength, support, supportByKlass);
        }
    }

    /**
     * Scans dataset to calculate the support of single items
     */
    private void calculateSingletons() {
        mapSupport = new HashMap<Short, Long>();
        mapSupportByKlass = new HashMap<Short, HashMap<Short, Long>>();

        for (int i = 0; i < dataset.size(); i++) {
            Short klass = dataset.getKlassInstance(i);

            for (int j = 0; j < dataset.getNumberAttributes(); j++) {
                Short item = dataset.getInstance(i).asNominal()[j];
                Long count = mapSupport.getOrDefault(item, 0L);

                mapSupport.put(item, ++count);

                HashMap<Short, Long> byKlass = mapSupportByKlass.get(item);

                if (byKlass == null) {
                    mapSupportByKlass.put(item, new HashMap<Short, Long>());
                    mapSupportByKlass.get(item).put(klass, 1L);
                } else {
                    Long counter = byKlass.getOrDefault(klass, 0L);
                    byKlass.put(klass, counter + 1);
                }
            }
        }
    }

    /**
     * Generate rules for current antecedent
     * 
     * @param antecedent       for current rule
     * @param antecedentLength number of items forming antecedent
     * @param support          of the rule
     * @param counterByKlass   support for each class
     */
    protected void generateRules(short[] antecedent, int antecedentLength, long support,
            HashMap<Short, Long> counterByKlass) {
        short[] itemsetOutputBuffer = new short[antecedentLength];

        System.arraycopy(antecedent, 0, itemsetOutputBuffer, 0, antecedentLength);
        Arrays.sort(itemsetOutputBuffer, 0, antecedentLength);

        for (Entry<Short, Long> entry : counterByKlass.entrySet()) {
            Rule rule = new Rule(itemsetOutputBuffer, entry.getKey());
            rule.setSupportAntecedent(support);
            rule.setSupportRule(entry.getValue());
            rule.setSupportConsequent(dataset.getFrequencyByKlass().get(rule.getKlass()));

            if (rule.getSupportRule() >= this.minSupportRelative && rule.getConfidence() >= this.minConf)
                rules.add(rule);
        }
    }
}
