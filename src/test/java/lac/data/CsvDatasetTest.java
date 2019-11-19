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
package lac.data;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestSuite;

public class CsvDatasetTest extends TestSuite {
    private Dataset dataset;

    @Before
    public void setup() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("data/dataset.csv").getFile());
        dataset = new CsvDataset(file.getAbsolutePath());
    }

    @Test
    public void countInstancesIgnoringSpaces() {
        assertEquals(DummyDataset.NUMBER_INSTANCES, dataset.size());
    }

    @Test
    public void countNumberClasses() {
        assertEquals(DummyDataset.NUMBER_KLASSES, dataset.getNumberKlasses());
    }

    @Test
    public void correctFrequencyOfKlasses() {
        assertEquals(DummyDataset.FREQUENCY_KLASSES, dataset.getFrequencyByKlass());
    }

    @Test
    public void correctLabelForNominalAttribute() throws Exception {
        assertEquals(DummyDataset.NOMINAL_LABELS.length, dataset.getNumberLabels(DummyDataset.INDEX_NOMINAL_ATTRIBUTE));
    }

    @Test
    public void correctNumberAttributes() {
        assertEquals(DummyDataset.NUMBER_ATTRIBUTES, dataset.getNumberAttributes());
    }

    @Test
    public void correctTypeOfAttributes() {
        assertTrue(dataset.getAttribute(DummyDataset.INDEX_NOMINAL_ATTRIBUTE).isNominal());
        assertFalse(dataset.getAttribute(DummyDataset.INDEX_NUMERIC_ATTRIBUTE).isNominal());
    }

    @Test
    public void correctNameOfAttributes() {
        assertEquals(DummyDataset.NAME_NOMINAL, dataset.getAttribute(DummyDataset.INDEX_NOMINAL_ATTRIBUTE).getName());
        assertEquals(DummyDataset.NAME_NUMERIC, dataset.getAttribute(DummyDataset.INDEX_NUMERIC_ATTRIBUTE).getName());
    }

    @Test
    public void correctValueOfAttributes() {
        assertArrayEquals(DummyDataset.NOMINAL_LABELS,
                dataset.getAttribute(DummyDataset.INDEX_NOMINAL_ATTRIBUTE).getValues());
        assertArrayEquals(DummyDataset.KLASS_LABELS, dataset.getKlass().getValues());
    }
}
