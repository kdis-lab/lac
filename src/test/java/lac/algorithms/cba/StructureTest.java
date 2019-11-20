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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import junit.framework.TestSuite;

public class StructureTest extends TestSuite {
    @Test
    public void getters() {
        int idInstance = 1;
        short klass = 2;
        int indexCRule = 3;
        int indexWRule = 4;

        Structure st = new Structure(idInstance, klass, indexCRule, indexWRule);

        assertEquals(idInstance, st.getdIdInstance());
        assertEquals(klass, st.getKlass());
        assertEquals(indexCRule, st.getIndexCRule());
        assertEquals(indexWRule, st.getIndexWRule());
    }
}
