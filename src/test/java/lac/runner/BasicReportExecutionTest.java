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
package lac.runner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.mockito.Mockito;

import junit.framework.TestSuite;
import lac.algorithms.Classifier;
import lac.data.Dataset;
import lac.data.Instance;

public class BasicReportExecutionTest extends TestSuite {

    @Test
    public void byDefaultFieldsAreEmpty() {
        BasicReportExecution report = new BasicReportExecution();
        assertEquals(0L, report.getTotalTime());
        assertEquals(0, report.getAccuracy(), 0.0);
    }

    @Test
    public void totalTimeIsCalculatedAsStartLessEnd() throws InterruptedException {
        BasicReportExecution report = new BasicReportExecution();
        report.startTime();
        Thread.sleep(500);
        report.stopTime();
        assertTrue(report.getTotalTime() >= 500);
    }

    @Test
    public void calculateAccuracyCorrectly() {
        Short klass = 2;
        Dataset dataset = Mockito.mock(Dataset.class);
        Mockito.when(dataset.size()).thenReturn(2);
        Mockito.when(dataset.getKlassInstance(0)).thenReturn(klass);
        Mockito.when(dataset.getKlassInstance(1)).thenReturn((short) -1000);

        Classifier classifier = Mockito.mock(Classifier.class);
        Instance instance0 = Mockito.mock(Instance.class);
        Mockito.when(instance0.getKlass()).thenReturn(klass);

        Instance instance1 = Mockito.mock(Instance.class);

        Mockito.when(dataset.getInstance(0)).thenReturn(instance0);
        Mockito.when(dataset.getInstance(1)).thenReturn(instance1);
        Mockito.when(classifier.predict(instance0)).thenReturn(klass);
        Mockito.when(classifier.predict(instance1)).thenReturn((short) -1);

        BasicReportExecution report = new BasicReportExecution();
        report.calculateAccuracy(dataset, classifier);
        assertEquals(0.5, report.getAccuracy(), 0.0);
    }
}
