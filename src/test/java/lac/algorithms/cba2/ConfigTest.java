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
package lac.algorithms.cba2;

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
        assertEquals(0.5, config.getMinConf(), 0.0);
        assertEquals(0.01, config.getMinSup(), 0.0);
    }

    @Test
    public void eachParameterAcceptsBothIntegerAndDouble() {
        config.setMinSup(0.1);
        assertEquals(0.1, config.getMinSup(), 0.0);

        config.setMinSup(new Integer(0));
        assertEquals(0, config.getMinSup(), 0.0);

        config.setMinConf(0.1);
        assertEquals(0.1, config.getMinConf(), 0.0);

        config.setMinConf(new Integer(0));
        assertEquals(0, config.getMinConf(), 0.0);
    }

    @Test
    public void configAsStringShowsAllFields() {
        assertEquals("minSup=0.01 minConf=0.5", config.toString());
    }
}
