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

import java.util.HashMap;

public class DummyDataset {
    protected static HashMap<Short, Long> FREQUENCY_KLASSES = new HashMap<Short, Long>() {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        {
            put((short) 2, 2L);
            put((short) 3, 1L);
        }
    };
    protected static int NUMBER_INSTANCES = 3;
    protected static int NUMBER_KLASSES = 2;
    protected static int INDEX_NOMINAL_ATTRIBUTE = 0;
    protected static int INDEX_NUMERIC_ATTRIBUTE = 1;
    protected static String[] NOMINAL_LABELS = { "val1", "val2" };
    protected static String[] KLASS_LABELS = { "class1", "class2" };
    protected static int NUMBER_ATTRIBUTES = 2;
    protected static String NAME_NOMINAL = "attr1";
    protected static String NAME_NUMERIC = "attr2";
}
