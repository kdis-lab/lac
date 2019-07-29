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

public class CBAM2 {
    private ArrayList<Rule> U;
    private ArrayList<Rule> Q;
    private ArrayList<SelectedRule> C;
    private ArrayList<Structure> A;
    private Dataset dataset;

    private ArrayList<? extends Rule> rules;

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
     * <p>
     * Function to get stored classifier
     * </p>
     * 
     * @return RuleBase The whole classifier
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
