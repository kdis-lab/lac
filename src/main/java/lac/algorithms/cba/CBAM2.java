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
package lac.algorithms.cba;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import lac.algorithms.Classifier;
import lac.data.Dataset;

/**
 * This class has the logic for building a classifier using CARs. To produce the
 * best classifier out of the whole set of rules would involve evaluating all
 * the possible subsets of it on the training data and selecting the subset with
 * the right rule sequence that m gives the least number of errors. There are
 * 2^m such subsets, where m is the number of rules, which can be more than
 * 10,000, not to mention different rule sequences. This is clearly infeasible,
 * this class contains the heuristic proposed by the original authors to solve
 * this problem
 */
public class CBAM2 {
    private ArrayList<Rule> U;
    private ArrayList<Rule> Q;
    private ArrayList<SelectedRule> C;
    private ArrayList<Structure> A;

    /**
     * Dataset for the rules
     */
    private Dataset dataset;

    /**
     * Rules forming the classifier
     */
    private ArrayList<? extends Rule> rules;

    /**
     * Constructor to post-process the rules
     * 
     * @param dataset of the rules being processed
     * @param rules   being used as base for the classifier
     */
    @SuppressWarnings("unchecked")
    public CBAM2(Dataset dataset, ArrayList<? extends Rule> rules) {
        this.dataset = dataset;
        this.rules = rules;

        this.U = new ArrayList<Rule>();
        this.Q = new ArrayList<Rule>();
        this.A = new ArrayList<Structure>();

        Collections.sort(this.rules);

        this.stage1();
        this.stage2();
        this.stage3();
    }

    /**
     * Sort the set of generated rules considering confidence, support and size.
     * This is to ensure that we will choose the highest precedence rules for our
     * classifier.
     */
    private void stage1() {
        int cRule, wRule;
        short y;
        Short[] example;
        Rule rule;

        for (int i = 0; i < this.dataset.size(); i++) {
            example = this.dataset.getInstance(i).asNominal();
            y = this.dataset.getKlassInstance(i);

            cRule = -1;
            wRule = -1;

            for (int j = 0; j < this.rules.size() && (cRule < 0 || wRule < 0); j++) {

                rule = this.rules.get(j);

                if (rule.matching(example)) {
                    if ((cRule < 0) && (y == rule.getKlass()))
                        cRule = j;
                    if ((wRule < 0) && (y != rule.getKlass()))
                        wRule = j;
                }
            }

            if (cRule > -1) {
                rule = this.rules.get(cRule);

                if (this.isNew(this.U, rule))
                    this.U.add(rule);

                rule.incrementKlassCovered(y);
                if ((cRule < wRule) || (wRule < 0)) {
                    rule.mark();
                    if (this.isNew(this.Q, rule))
                        this.Q.add(rule);
                } else {
                    Structure str = new Structure(i, y, cRule, wRule);
                    this.A.add(str);
                }
            } else if (wRule > -1) {
                Structure str = new Structure(i, y, cRule, wRule);
                this.A.add(str);
            }
        }
    }

    /**
     * Select rules for the classifier from R following the sorted sequence. For
     * each rule r, we go through D to find those cases covered by r (they satisfy
     * the conditions of r). We mark r if it correctly classifies a case d . d.id is
     * the unique identification number of d. If r can correctly classify at least
     * one case (i.e., if r is marked), it will be a potential rule in our
     * classifier. Those cases it covers are then removed from D. A default class is
     * also selected (the majority class in the remaining data),which means that if
     * we stop selecting more rules for our classifier C this class will be the
     * default class of C. We then compute and record the total number of errors
     * that are made by the current C and the default class . This is the sum of the
     * number of errors that have been made by all the selected rules in C and the
     * number of errors to be made by the default class in the training data. When
     * there is no rule or no training case left, the rule selection process is
     * completed.
     */
    private void stage2() {
        int poscRule, poswRule;
        Structure str;
        Rule cRule, wRule, rule;

        for (int i = 0; i < this.A.size(); i++) {
            str = this.A.get(i);
            poscRule = str.getIndexCRule();
            poswRule = str.getIndexWRule();

            wRule = this.rules.get(poswRule);
            if (wRule.isMark()) {
                if (poscRule > -1)
                    this.rules.get(poscRule).decrementKlassCovered(str.getKlass());
                wRule.incrementKlassCovered(str.getKlass());
            } else {
                for (int j = 0; j < this.U.size(); j++) {
                    rule = this.U.get(j);

                    if (rule.matching(this.dataset.getInstance(str.getdIdInstance()).asNominal())
                            && rule.getKlass() != str.getKlass()) {
                        if (poscRule > -1) {
                            cRule = this.rules.get(poscRule);
                            if (rule.isPrecedence(cRule)) {
                                rule.addReplace(new Replace(poscRule, str.getdIdInstance(), str.getKlass()));
                                rule.incrementKlassCovered(str.getKlass());

                                if (this.isNew(this.Q, rule))
                                    this.Q.add(rule);
                            }
                        } else {
                            rule.addReplace(new Replace(poscRule, str.getdIdInstance(), str.getKlass()));
                            rule.incrementKlassCovered(str.getKlass());

                            if (this.isNew(this.Q, rule))
                                this.Q.add(rule);
                        }
                    }
                }
            }
        }
    }

    /**
     * Discard those rules in C that do not improve the accuracy of the classifier.
     * The first rule at which there is the least number of errors recorded on D is
     * the cutoff rule. All the rules after this rule can be discarded because they
     * only produce more errors. The undiscarded rules and the default class of the
     * last rule in C form our classifier.
     */
    @SuppressWarnings("unchecked")
    private void stage3() {
        long ruleErrors, errorsOfRule;
        int posLowest;
        long lowestTotalErrors;
        Long totalErrors = 0L;
        HashMap<Short, Long> compClassDistr;
        int[] exampleCovered;
        Short[] example;
        Rule rule;
        Replace rep;
        SelectedRule sel;

        this.C = new ArrayList<SelectedRule>();

        compClassDistr = this.dataset.getFrequencyByKlass();
        ruleErrors = 0;

        exampleCovered = new int[this.dataset.size()];
        for (int i = 0; i < this.dataset.size(); i++)
            exampleCovered[i] = 0;

        Collections.sort(this.Q);

        for (int i = 0; i < this.Q.size(); i++) {
            rule = this.Q.get(i);
            if (rule.getKlassesCovered(rule.getKlass()) > 0) {
                for (int j = 0; j < rule.getNumberReplace(); j++) {
                    rep = rule.getReplace(j);
                    if (exampleCovered[rep.getdIdInstance()] > 0)
                        rule.decrementKlassCovered(rep.getKlass());
                    else {
                        if (rep.getIndexCRule() > -1) {
                            this.rules.get(rep.getIndexCRule()).decrementKlassCovered(rep.getKlass());
                        }
                    }
                }

                errorsOfRule = 0;
                for (int j = 0; j < this.dataset.size(); j++) {
                    if (exampleCovered[j] < 1) {
                        example = this.dataset.getInstance(j).asNominal();

                        if (rule.matching(example)) {
                            exampleCovered[j] = 1;
                            short klass = this.dataset.getKlassInstance(j);
                            compClassDistr.put(klass, compClassDistr.get(klass) - 1);

                            if (rule.getKlass() != this.dataset.getKlassInstance(j))
                                errorsOfRule++;
                        }
                    }
                }
                ruleErrors += errorsOfRule;

                short defaultKlass = this.dataset.getKlass(0);
                for (Entry<Short, Long> entry : compClassDistr.entrySet()) {
                    if (compClassDistr.get(defaultKlass) < entry.getValue())
                        defaultKlass = entry.getKey();
                }

                Long defaultErrors = 0L;
                for (Entry<Short, Long> entry : compClassDistr.entrySet()) {
                    if (!entry.getKey().equals(defaultKlass))
                        defaultErrors += entry.getValue();
                }

                totalErrors = ruleErrors + defaultErrors;
                this.C.add(new SelectedRule(rule, defaultKlass, totalErrors));
            }
        }

        if (this.C.size() > 0) {
            lowestTotalErrors = this.C.get(0).getTotalErrors();
            posLowest = 0;
            for (int i = 1; i < this.C.size(); i++) {
                sel = this.C.get(i);
                if (sel.getTotalErrors() < lowestTotalErrors) {
                    lowestTotalErrors = sel.getTotalErrors();
                    posLowest = i;
                }
            }
            while (this.C.size() > (posLowest + 1))
                this.C.remove(posLowest + 1);
        }
    }

    /**
     * Returns the classifier
     * 
     * @return Classifier with the whole of the classifier
     * @throws CloneNotSupportedException
     */
    public Classifier getClassifier() throws CloneNotSupportedException {
        short defaultKlass;
        Classifier rb = new Classifier();
        SelectedRule sel;

        if (this.C.size() > 0) {
            for (int i = 0; i < this.C.size(); i++) {
                sel = this.C.get(i);
                rb.add((Rule) sel.getRule().clone());
            }

            sel = this.C.get(this.C.size() - 1);
            rb.add(new Rule(sel.getDefaultKlass()));
        } else {
            defaultKlass = this.dataset.getKlass(0);
            for (int i = 0; i < this.dataset.getNumberKlasses(); i++) {
                defaultKlass = this.dataset.getKlass(0);
                short klass = this.dataset.getKlass(i);

                if (this.dataset.getNumberInstancesPerKlass(klass) > this.dataset
                        .getNumberInstancesPerKlass(defaultKlass))
                    defaultKlass = klass;
            }
            rb.add(new Rule(defaultKlass));
        }

        return rb;
    }

    /**
     * Check if the rule was contained in the set of rules
     * 
     * @param rb   set of rules
     * @param rule to check if it was contained or not
     * @return true if rule was not contained
     */
    private boolean isNew(ArrayList<Rule> rb, Rule rule) {
        Rule r;

        for (int i = 0; i < rb.size(); i++) {
            r = rb.get(i);
            if (rule.equals(r))
                return false;
        }

        return true;
    }
}
