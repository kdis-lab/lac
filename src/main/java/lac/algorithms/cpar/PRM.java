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

import lac.data.Dataset;
import lac.utils.Utils;

/**
 * Class used for obtaining class association rules. PRM comes from Predictive
 * Rule Mining. An algorithm which modifies FOIL to achieve higher accuracy and
 * efficiency. Unlike FOIL, in PRM, after an example is correctly covered by a
 * rule, instead of removing it, its weight is decreased by multiplying a
 * factor. This “weighted” version of FOIL produces more rules and each positive
 * example is usually covered more than once. PRM speeds up FOIL by means of a
 * data structure to calculate the gain, called PNArray
 */
public class PRM {
    /**
     * PNArray has two dimnesions, in 0 positive examples are located
     */
    public final int POSITIVE_EXAMPLES = 0;

    /**
     * PNArray has two dimnesions, in 1 negative examples are located
     */
    public final int NEGATIVE_EXAMPLES = 1;

    /**
     * Similarity ratio of gain to consider
     */
    private final double GAIN_SIMILARITY_RATIO = 0.99;

    /**
     * Configuration for obtaining the classifier
     */
    private Config config;

    /**
     * Dataset to train the classifier
     */
    private Dataset dataset;

    /**
     * Obtained rules
     */
    private ArrayList<Rule> rules;

    /**
     * Array of each literal candidate of being included in rule
     */
    private Literal[] literals2 = null;

    /**
     * Array for storing both positive and negative examples. P2 and N2 are used as
     * copy while adding a new rule
     */
    private WeightedInstance[] P = null;
    private WeightedInstance[] N = null;
    private WeightedInstance[] P2 = null;
    private WeightedInstance[] N2 = null;

    /**
     * Structure used to speed up the algorithm
     */
    private double[][] pnArray = null;
    private double[][] pnArray2 = null;

    /**
     * Constructor
     * 
     * @param dataset dataset used to generate the classifier
     * @param config  configuration used to generate the classifier
     */
    public PRM(Dataset dataset, Config config) {
        this.dataset = dataset;
        this.config = config;
    }

    /**
     * Add discovered rule if it is new
     * 
     * @param antecedent antecedent forming the rule
     * @param consequent klass of the rule
     */
    private void addRule(short[] antecedent, short consequent) {
        // Rules without antecedent should not be included
        if (antecedent == null)
            return;

        Rule rule = new Rule(antecedent, consequent);
        rule.calculateLaplaceAccuracy(dataset);

        if (!rules.contains(rule)) {
            rules.add(rule);
        }

        revisePositiveExamples(antecedent, P);
    }

    /**
     * Recalculate gains for the literals not selected
     * 
     * @param oldPositiveWeigth total weight of the positive examples
     * @param oldNegativeWeigth total weight of the negative examples
     */
    private void recalculateGains(double oldPositiveWeigth, double oldNegativeWeigth) {
        for (int index = 0; index < literals2.length; index++) {
            // If attribute has not been selected previously, recalculate gain
            if (!literals2[index].selected) {
                double newPositiveWeigth = pnArray2[index][POSITIVE_EXAMPLES];
                double newNegativeWeigth = pnArray2[index][NEGATIVE_EXAMPLES];

                literals2[index].gain = calculateGain(newPositiveWeigth, newNegativeWeigth, oldPositiveWeigth,
                        oldNegativeWeigth);
            }
        }
    }

    /**
     * Calculates gain for specified weighting
     * 
     * @param positiveWeigth    weighting represented by current positive examples
     * @param negativeWeight    weighting represented by current negative examples
     * @param oldPositiveWeigth total weighting represented by previous positive
     *                          example
     * @param oldNegativeWeigth total weighting represented by previous negative
     *                          example
     */
    private double calculateGain(double positiveWeigth, double negativeWeigth, double oldPositiveWeigth,
            double oldNegativeWeigth) {
        if ((int) positiveWeigth == 0)
            return 0.0;

        // Calculate gain
        double oldGain = Math.log(oldPositiveWeigth / (oldPositiveWeigth + oldNegativeWeigth));
        double newGain = Math.log((double) positiveWeigth / (double) (positiveWeigth + negativeWeigth));
        return positiveWeigth * (newGain - oldGain);
    }

    /**
     * Generates positive and negative weighted examples in function of the
     * specified class
     * 
     * @param klass to consider as positive examples
     */
    private void generatePN(short klass) {
        int numberPositiveInstances = 0, numberNeativeInstances = 0;

        // Get the total number of positve and negative examples
        for (int index = 0; index < this.dataset.size(); index++) {
            if (this.dataset.getKlassInstance(index) == klass)
                numberPositiveInstances++;
            else
                numberNeativeInstances++;
        }

        P = new WeightedInstance[numberPositiveInstances];
        N = new WeightedInstance[numberNeativeInstances];

        numberPositiveInstances = 0;
        numberNeativeInstances = 0;

        // Copy data
        for (int index = 0; index < this.dataset.size(); index++) {
            Short[] example = this.dataset.getInstance(index).asNominal();

            if (this.dataset.getKlassInstance(index) == klass) {
                P[numberPositiveInstances] = new WeightedInstance(example);
                numberPositiveInstances++;
            } else {
                N[numberNeativeInstances] = new WeightedInstance(example);
                numberNeativeInstances++;
            }
        }
    }

    /**
     * 
     * Remove examples not being covered by this antecedent, and adjust PN
     * 
     * @param flag       0 for positive examples and 1 for negative examples
     * @param antecedent part of the rule being checked
     * @param examples   training dataset
     * @return the new dataset after considering the current antecedent
     */
    private WeightedInstance[] removeExamplesNotCovered(int flag, short[] antecedent, WeightedInstance[] examples) {
        if (examples == null)
            return null;

        int size = this.getTotalPositiveExamples(antecedent, examples);
        if (size <= 0) {
            // every single instance is negative
            for (int index = 0; index < pnArray2.length; index++)
                pnArray2[index][flag] = 0.0;
            return null;
        }
        WeightedInstance[] newExamples = new WeightedInstance[size];

        // Loop through given examples array
        int newNumberExamples = 0;
        for (int i = 0; i < examples.length; i++) {
            // Examples being convered by current instances, are part of the new dataset
            if (Utils.isSubset(antecedent, examples[i].instance)) {
                newExamples[newNumberExamples] = new WeightedInstance(examples[i].instance, examples[i].weight);
                newNumberExamples++;
            } else {
                // examples not covered are not part of the new array and must be decremented
                int length = examples[i].instance.length - 1;
                for (int j = 0; j < length; j++) {
                    short attribute = examples[i].instance[j];
                    // Missing value
                    if (attribute >= 0)
                        pnArray2[attribute][flag] = pnArray2[attribute][flag] - examples[i].weight;
                }
            }
        }

        return newExamples;
    }

    /**
     * Revise weights for positive examples and adjust positive elements in the
     * PNarray
     * 
     * @param antecedent       rule being added
     * @param positiveExamples dataset containing the positive examples
     */
    private void revisePositiveExamples(short[] antecedent, WeightedInstance[] positiveExamples) {
        for (int i = 0; i < positiveExamples.length; i++) {
            // Check if rule satisfies current record
            if (Utils.isSubset(antecedent, positiveExamples[i].instance)) {
                double tempWeighting = positiveExamples[i].weight;

                // Reduce weighting associated with example
                positiveExamples[i].weight = tempWeighting * this.config.getAlpha();
                double difference = tempWeighting - positiveExamples[i].weight;

                int length = positiveExamples[i].instance.length - 1;

                // Decrement weightings in PN array
                for (int j = 0; j < length; j++) {
                    short attribute = positiveExamples[i].instance[j];
                    // Missing value
                    if (attribute >= 0)
                        pnArray[attribute][0] -= difference;
                }
            }
        }
    }

    /**
     * Get the number of examples that satisfy the given antecedent
     * 
     * @param antecedent to be used while counting positive examples
     * @param examples   dataset being used
     * @return number of positive examples fired for the current antecedent
     */
    private int getTotalPositiveExamples(short[] antecedent, WeightedInstance[] examples) {
        int total = 0;

        for (int i = 0; i < examples.length; i++) {
            if (Utils.isSubset(antecedent, examples[i].instance))
                total++;
        }

        return total;
    }

    /**
     * Generates PN array
     * 
     * @throws Exception
     */
    private void generatePNarray() throws Exception {
        // PNarray has two dimensions:
        // 0 -> positive examples
        // 1 -> negative examples
        pnArray = new double[this.dataset.getNumberSingletons() - this.dataset.getNumberKlasses()][2];

        // Positive examples
        for (int index = 0; index < P.length; index++) {
            short[] instance = P[index].instance;

            int length = instance.length - 1;
            for (int j = 0; j < length; j++) {
                // Missing value
                if (instance[j] >= 0)
                    pnArray[instance[j]][POSITIVE_EXAMPLES] = pnArray[instance[j]][POSITIVE_EXAMPLES] + P[index].weight;
            }
        }

        // Negative examples
        for (int index = 0; index < N.length; index++) {
            short[] instance = N[index].instance;
            int length = instance.length - 1;
            for (int j = 0; j < length; j++) {
                // Missing value
                if (instance[j] >= 0)
                    pnArray[instance[j]][NEGATIVE_EXAMPLES] = pnArray[instance[j]][NEGATIVE_EXAMPLES] + N[index].weight;
            }
        }
    }

    /**
     * Check if there are more literals with gain above a user-specified threshold
     * 
     * @return true if there are more, false otherwise
     */
    private boolean existsMoreLiteralsWithGains() {
        double oldPositiveWeigth = P2.length;
        double oldNegativeWeigth = N2.length;

        for (int i = 0; i < pnArray2.length; i++) {
            double newPositiveWeigth = pnArray2[i][0];
            double newNegativeWeigth = pnArray2[i][1];

            double gain = calculateGain(newPositiveWeigth, newNegativeWeigth, oldPositiveWeigth, oldNegativeWeigth);

            // there are at least this literal with enough gain to be added
            if (gain >= this.config.getMinBestGain())
                return true;
        }

        return false;
    }

    /**
     * Get the total weighting for the given dataset
     * 
     * @param examples dataset to calculate weights
     * @return the total sum of weight for the given dataset
     */
    private double getTotalWeighting(WeightedInstance[] examples) {
        double total = 0.0;
        if (examples == null)
            return total;

        for (int i = 0; i < examples.length; i++) {
            total += examples[i].weight;
        }

        return total;
    }

    /**
     * Obtains class association rules
     * 
     * @return the candidate rules
     * @throws Exception
     */
    public ArrayList<Rule> run() throws Exception {
        rules = new ArrayList<Rule>();

        // Generate literals array
        Literal[] literals = new Literal[this.dataset.getNumberSingletons() - this.dataset.getNumberKlasses()];
        for (int i = 0; i < literals.length; i++) {
            literals[i] = new Literal(0.0, false);
        }

        // Iterate for each class, and consider the dataset as positive examples for
        // current class and negative otherwise
        for (int indexInstance = 0; indexInstance < this.dataset.getNumberKlasses(); indexInstance++) {
            short klass = this.dataset.getKlass(indexInstance);

            // Generate positive and negative examples from training set and
            // PN array
            generatePN(klass);
            generatePNarray();

            // calculate start total weight threshold of positive examples
            double totalWeightThreshold = config.getDelta() * getTotalWeighting(P);

            // Continue searching rules until total weight has been reduced enough
            while (getTotalWeighting(P) > totalWeightThreshold) {
                P2 = Utils.copy(P);
                N2 = Utils.copy(N);
                pnArray2 = Utils.copy(pnArray);
                literals2 = Utils.copy(literals);

                if (!existsMoreLiteralsWithGains())
                    break;

                // Proceed with generation process, starting with an empty antecedent
                addLiteralsToRule(null, klass);
            }
        }

        return rules;
    }

    /**
     * Add recursively the best literal to the current antecedent following a greedy
     * fashion
     * 
     * @param antecedent items forming the antecedent until the moment
     * @param consequent the class
     */
    private void addLiteralsToRule(short[] antecedent, short consequent) {
        double oldPositiveWeight = (double) P2.length;
        double oldNegativeWeight = (double) N2.length;

        // Recalculate gain in function of P and N
        this.recalculateGains(oldPositiveWeight, oldNegativeWeight);

        // Get the best possible gain
        double bestGain = 0.0;
        for (int i = 0; i < literals2.length; i++) {
            if (!literals2[i].selected && literals2[i].gain > bestGain) {
                bestGain = literals2[i].gain;
            }
        }

        // if best gain is less than user-specified minimum, there are no need to
        // continue
        if (bestGain <= this.config.getMinBestGain()) {
            this.addRule(antecedent, consequent);
            return;
        }

        // Determine gain threshold above which a gain value is considered
        // to be sufficiently high to be
        double gainThreshold = bestGain * GAIN_SIMILARITY_RATIO;
        if (gainThreshold < this.config.getMinBestGain())
            gainThreshold = this.config.getMinBestGain();

        for (int index = 0; index < literals2.length; index++) {
            // if literal has not been included, and gain is above thredhold
            if (!literals2[index].selected && literals2[index].gain >= gainThreshold) {
                short attribute = (short) index;

                short[] newAntecedent = Utils.addNewElement(antecedent, attribute);
                literals2[attribute].selected = true;

                P2 = this.removeExamplesNotCovered(POSITIVE_EXAMPLES, newAntecedent, P2);
                N2 = this.removeExamplesNotCovered(NEGATIVE_EXAMPLES, newAntecedent, N2);

                // If negative examples are empty, there are no need to continue
                if (N2 == null) {
                    this.addRule(newAntecedent, consequent);
                    continue;
                }

                // Recursively add new literals to the rule
                this.addLiteralsToRule(newAntecedent, consequent);
            }
        }
    }

}
