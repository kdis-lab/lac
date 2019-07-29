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

/**
 * Class used when dataset is not compatible with the algorithm by the reason
 * specified in the message
 */
public class IncompatibleDataset extends RuntimeException {
    private static final long serialVersionUID = -3439456253098700910L;

    /**
     * Main constructor
     * 
     * @param message
     */
    public IncompatibleDataset(String message) {
        super(message);
    }
}
