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
import static org.junit.Assert.assertNotSame;

import org.junit.Test;

import junit.framework.TestSuite;

public class ReplaceTest extends TestSuite {

    @Test
    public void getters() {
        int indexCRule = 1;
        int idInstance = 2;
        short klass = 3;

        Replace replace = new Replace(indexCRule, idInstance, klass);
        assertEquals(indexCRule, replace.getIndexCRule());
        assertEquals(idInstance, replace.getdIdInstance());
        assertEquals(klass, replace.getKlass());
    }

    @Test
    public void cloneReturnsNewInstance() {
        Replace replace = new Replace(1, 2, (short) 3);
        assertNotSame(replace, replace.clone());
    }

    @Test
    public void compareToUsesIdInstance() {
        Replace replace1 = new Replace(2, 1, (short) 3);
        Replace replace2 = new Replace(2, 2, (short) 3);
        assertEquals(-1, replace1.compareTo(replace2));
        assertEquals(1, replace2.compareTo(replace1));
        assertEquals(0, replace1.compareTo(replace1));

    }

}
