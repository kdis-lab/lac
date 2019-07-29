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
package lac.algorithms.mac;

import java.util.HashSet;
import java.util.Set;

/**
 * This class contains the logic for some utils operations performed in this
 * algorithm.
 */
public class Utils {
    /**
     * Performs the intersection of two tidsets
     * 
     * @param tidsetI First tidset to be intersected
     * @param tidsetJ Second tidset to be intersected
     * @return
     */
    public static Set<Integer> intersection(Set<Integer> tidsetI, Set<Integer> tidsetJ) {
        Set<Integer> result = new HashSet<Integer>();

        long supportI = tidsetI.size();
        long supportJ = tidsetJ.size();

        // Depending on the size of each tidset, the operation is performed on a
        // direction or another. That's a small optimization but it could help
        // with very large tidsets
        if (supportI > supportJ) {
            for (Integer tid : tidsetJ) {
                if (tidsetI.contains(tid)) {
                    result.add(tid);
                }
            }
        } else {
            for (Integer tid : tidsetI) {
                if (tidsetJ.contains(tid)) {
                    result.add(tid);
                }
            }
        }

        return result;
    }
}
