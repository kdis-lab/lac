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
 *
 * This algorithm was adapted from the SPMF Library, which is is licensed 
 * under the GNU GPL v3 license.
 * Fournier-Viger, P., Lin, C.W., Gomariz, A., Gueniche, T., Soltani, A., 
 * Deng, Z., Lam, H. T. (2016). The SPMF Open-Source Data Mining Library 
 * Version 2. Proc. 19th European Conference on Principles of Data Mining 
 * and Knowledge Discovery (PKDD 2016) Part III, Springer LNCS 9853, pp. 36-40.
 */
package lac.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * This class contains some useful methods to work with set of items
 */
public class Utils {
    /**
     * Performs the intersection of two tidsets
     * 
     * @param tidsetI First tidset to be intersected
     * @param tidsetJ Second tidset to be intersected
     * @return a new tidset with the intersection
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

    /**
     * Check if first itemset is a subset of the second one
     * 
     * @param itemset1 first itemset
     * @param itemset2 second itemset
     * @return true if itemset2 is a subset of itemset1, false otherwise
     */
    public static Boolean isSubset(ArrayList<Short> itemset1, ArrayList<Short> itemset2) {
        return itemset1.containsAll(itemset2);
    }

    /**
     * Check if first itemset is a subset of the second one
     * 
     * @param itemset1 first itemset
     * @param itemset2 second itemset
     * @return true if itemset1 is a subset of itemset2, false otherwise
     */
    public static Boolean isSubset(ArrayList<Short> itemset1, Short[] itemset2) {
        if (itemset1 == null || itemset2 == null)
            return true;

        Short[] itemsetA = {};
        itemsetA = itemset1.toArray(itemsetA);

        return isSubset(itemsetA, itemset2);
    }

    /**
     * Check if first itemset is a subset of the second one
     * 
     * @param itemset1 first itemset
     * @param itemset2 second itemset
     * @return true if itemset1 is a subset of itemset2, false otherwise
     */
    public static Boolean isSubset(Short[] itemset1, Short[] itemset2) {
        short[] itemset = new short[itemset1.length];

        for (int i = 0; i < itemset1.length; i++)
            itemset[i] = itemset1[i];

        short[] secondItemset = new short[itemset2.length];

        for (int i = 0; i < itemset2.length; i++)
            secondItemset[i] = itemset2[i];

        return isSubset(itemset, secondItemset);
    }

    /**
     * Check if first itemset is a subset of the second one
     * 
     * @param itemset1 first itemset
     * @param itemset2 second itemset
     * @return true if itemset1 is a subset of itemset2, false otherwise
     */
    public static Boolean isSubset(short[] itemset1, short[] itemset2) {
        // Check for empty itemsets
        if (itemset1 == null)
            return true;
        if (itemset2 == null)
            return false;

        for (int index1 = 0; index1 < itemset1.length; index1++) {
            if (!memberOf(itemset1[index1], itemset2))
                return false;
        }

        // itemset1 is a subset of itemset2
        return true;
    }

    /**
     * @param item
     * @param itemset
     * @return
     */
    private static boolean memberOf(short item, short[] itemset) {
        for (int index = 0; index < itemset.length; index++) {
            // Makes use of lexicography order to be faster
            if (item < itemset[index])
                return false;
            else if (item == itemset[index])
                return true;
        }
        return false;
    }

    /**
     * Concatenates two sets of items
     * 
     * @param itemset1 first set to join
     * @param itemset2 second set to join
     * @return the concatenation of both sets of items
     */
    public static Short[] concatenate(Short[] itemset1, Short[] itemset2) {
        Short[] concatenation = new Short[itemset1.length + itemset2.length];
        System.arraycopy(itemset1, 0, concatenation, 0, itemset1.length);
        System.arraycopy(itemset2, 0, concatenation, itemset1.length, itemset2.length);
        return concatenation;
    }

    /**
     * Performs the intersection of two sets of items
     * 
     * @param antecedent first set
     * @param example    second set
     * @return the intersection of both sets
     */
    public static ArrayList<Short> intersect(ArrayList<Short> antecedent, Short[] example) {
        ArrayList<Short> exampleList = new ArrayList<Short>();
        for (int i = 0; i < example.length; i++)
            exampleList.add(example[i]);

        ArrayList<Short> intersection = new ArrayList<Short>();
        intersection.addAll(antecedent);
        intersection.retainAll(exampleList);

        return intersection;
    }

    /**
     * Performs the union of two set of items
     * 
     * @param antecedent  first set to join
     * @param antecedent2 second set to join
     * @return union of both sets of items
     */
    public static Short[] union(Short[] antecedent, Short[] antecedent2) {
        HashSet<Short> set = new HashSet<>();

        set.addAll(Arrays.asList(antecedent));

        set.addAll(Arrays.asList(antecedent2));

        Short[] union = {};
        union = set.toArray(union);
        return union;
    }

    /**
     * Performs the union of two set of items
     * 
     * @param antecedent  first set to join
     * @param antecedent2 second set to join
     * @return union of both sets of items
     */
    public static Short[] union(ArrayList<Short> antecedent, ArrayList<Short> antecedent2) {
        Short[] antecedentA = {};
        antecedentA = antecedent.toArray(antecedentA);
        Short[] antecedentB = {};
        antecedentB = antecedent2.toArray(antecedentB);

        return union(antecedentA, antecedentB);
    }

    /**
     * Performs the union of two set of items
     * 
     * @param antecedent1 first set to join
     * @param antecedent2 second set to join
     * @return union of both sets of items
     */
    public static Short[] union(Short[] antecedent1, ArrayList<Short> antecedent2) {
        Short[] antecedentB = {};
        antecedentB = antecedent2.toArray(antecedentB);

        return union(antecedent1, antecedentB);
    }

    /**
     * Creates a copy for the specified array. Specified array has to be
     * serializable
     * 
     * @param array to be copied
     * @return a deep copy for the specified array
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] copy(T[] array) {
        T[] temp = Arrays.copyOf(array, array.length);

        try {
            for (int i = 0; i < array.length; i++) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                objectOutputStream.writeObject(array[i]);
                ByteArrayInputStream bais = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                ObjectInputStream objectInputStream = new ObjectInputStream(bais);
                temp[i] = (T) objectInputStream.readObject();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

        return temp;
    }

    /**
     * Performs a deep copy of matrixes of double
     * 
     * @param matrix to be copied
     * @return a deep copy of the matrix
     */
    public static double[][] copy(double[][] matrix) {
        double[][] newMatrix = new double[matrix.length][];

        for (int i = 0; i < matrix.length; i++) {
            newMatrix[i] = new double[matrix[i].length];

            System.arraycopy(matrix[i], 0, newMatrix[i], 0, matrix[i].length);
        }

        return newMatrix;
    }

    /**
     * Adds a new element to the original set of items
     * 
     * @param currentItemset current itemset
     * @param newElement     to be added to itemset
     * @return the new set of items
     */
    public static short[] addNewElement(short[] currentItemset, short newElement) {
        if (currentItemset == null) {
            short[] newItemSet = { newElement };
            return newItemSet;
        }

        int currentLength = currentItemset.length;
        short[] newItemSet = new short[currentLength + 1];

        // Insertion using lexicography order
        for (int i = 0; i < currentLength; i++) {
            if (newElement < currentItemset[i]) {
                newItemSet[i] = newElement;
                // Add rest
                for (int j = i + 1; j < newItemSet.length; j++)
                    newItemSet[j] = currentItemset[j - 1];
                return newItemSet;
            } else
                newItemSet[i] = currentItemset[i];
        }

        newItemSet[newItemSet.length - 1] = newElement;

        return newItemSet;
    }
}
