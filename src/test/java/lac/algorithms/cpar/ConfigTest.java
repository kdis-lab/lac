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

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestSuite;

public class ConfigTest extends TestSuite {
    private Config config;

    @Before
    public void setup() {
        config = new Config();
    }

    @Test
    public void eachParameterHasADefaultValue() {
        assertEquals(5, config.getK());
        assertEquals(2 / 3.0, config.getAlpha(), 0.0);
        assertEquals(0.7, config.getMinBestGain(), 0.0);
        assertEquals(0.05, config.getDelta(), 0.0);
    }

    @Test
    public void eachParameterAcceptsBothIntegerAndDouble() {
        config.setK(1);
        assertEquals(1, config.getK());

        config.setAlpha(0.1);
        assertEquals(0.1, config.getAlpha(), 0.0);

        config.setAlpha(new Integer(0));
        assertEquals(0, config.getAlpha(), 0.0);

        config.setMinBestGain(1.0);
        assertEquals(1.0, config.getMinBestGain(), 0.0);

        config.setMinBestGain(new Integer(0));
        assertEquals(0.0, config.getMinBestGain(), 0.0);

        config.setDelta(1.0);
        assertEquals(1.0, config.getDelta(), 0.0);

        config.setDelta(new Integer(1));
        assertEquals(1.0, config.getDelta(), 0.0);
    }

    @Test
    public void configAsStringShowsAllFields() {
        assertEquals("k=5 minBestGain=0.7 delta=0.05 alpha=0.6666666666666666", config.toString());
    }
}
