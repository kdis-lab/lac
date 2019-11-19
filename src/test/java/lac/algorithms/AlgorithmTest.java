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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import junit.framework.TestSuite;
import lac.data.Dataset;

public class AlgorithmTest extends TestSuite {
    @Mock
    Dataset dataset;

    Algorithm absCls = Mockito.mock(Algorithm.class, Mockito.CALLS_REAL_METHODS);

    @Before
    public void setup() {
        dataset = Mockito.mock(Dataset.class);
    }

    @Test
    public void dontRaiseExceptionWithNotNumericDataset() {
        Mockito.when(dataset.hasNumericAttributes()).thenReturn(false);

        absCls.checkCompatibility(dataset);
    }

    @Test(expected = IncompatibleDataset.class)
    public void raiseExceptionWithNumericDataset() {
        Mockito.when(dataset.hasNumericAttributes()).thenReturn(true);

        absCls.checkCompatibility(dataset);
    }
}
