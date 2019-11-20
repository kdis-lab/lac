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
package lac.metrics;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestSuite;

public class ConfusionMatrixTest extends TestSuite {
    private ConfusionMatrix matrix = null;

    @Before
    public void setup() throws IOException {
        matrix = new ConfusionMatrix();
        matrix.add((short) 1, (short) 0);
        matrix.add((short) 0, (short) 0);
        matrix.add((short) 0, (short) 1);
        matrix.add((short) 1, (short) 1);
        matrix.add((short) 2, (short) 1);
    }

    @Test
    public void getAverageRecall() {
        assertEquals(0.33, matrix.getAverageRecall(), 0.01);
        matrix.add((short) 0, (short) 1);
        assertEquals(0.27, matrix.getAverageRecall(), 0.01);
    }

    @Test
    public void getAveragePrecision() {
        assertEquals(0.27, matrix.getAveragePrecision(), 0.01);
        matrix.add((short) 1, (short) 1);
        assertEquals(0.33, matrix.getAveragePrecision(), 0.01);
        matrix.add((short) 1, (short) 0);
        assertEquals(0.27, matrix.getAveragePrecision(), 0.01);
    }

    @Test
    public void getMicroFMeasure() {
        assertEquals(0.4, matrix.getMicroFMeasure(), 0.01);
        matrix.add((short) 0, (short) 0);
        assertEquals(0.5, matrix.getMicroFMeasure(), 0.01);
    }

    @Test
    public void getMacroFMeasure() {
        assertEquals(0.3, matrix.getMacroFMeasure(), 0.01);
        matrix.add((short) 0, (short) 0);
        assertEquals(0.35, matrix.getMacroFMeasure(), 0.01);
    }

    @Test
    public void getAccuracy() {
        assertEquals(0.4, matrix.getAccuracy(), 0.01);
        matrix.add((short) 0, (short) 0);
        assertEquals(0.5, matrix.getAccuracy(), 0.01);
    }

    @Test
    public void getKappa() {
        assertEquals(0.0, matrix.getKappa(), 0.01);
        matrix.add((short) 0, (short) 0);
        assertEquals(0.14, matrix.getKappa(), 0.01);
    }

    @Test
    public void addAsShortObject() {
        matrix.add(Short.valueOf((short) 0), Short.valueOf((short) 0));
        assertEquals(0.14, matrix.getKappa(), 0.01);
    }

    @Test
    public void addAsNativeShort() {
        matrix.add((short) 0, (short) 0);
        assertEquals(0.14, matrix.getKappa(), 0.01);
    }
}
