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
package lac.algorithms;

import lac.data.Dataset;

/**
 * Base class used for all the algorithms of LAC
 */
public abstract class Algorithm {
    /**
     * Configuration used to generate the classifier
     */
    protected Config config;

    /**
     * Main method used to create the classifier
     * 
     * @param training Dataset used to train the classifier
     * @return associative classifier
     * @throws Exception
     */
    public abstract Classifier train(Dataset training) throws Exception;

    /**
     * Check if dataset is compatible with this algorithm
     * 
     * @param training Dataset to check if it is compatible or not with this
     *                 algorithm
     * @throws RuntimeException when the dataset is not compatible
     */
    public void checkCompatibility(Dataset training) {
        if (training.hasNumericAttributes()) {
            throw new IncompatibleDataset("Datasets with numeric attributes are not compatible with this algorithm");
        }
    }
}
