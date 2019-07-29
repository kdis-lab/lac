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
package lac.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * This class contains some useful methods to work with set of items
 */
public class Utils {
    /**
     * Check if first itemset is a subset of the second one
     * 
     * @param itemset1 first itemset
     * @param itemset2 second itemset
     * @return true if itemset1 is a subset of itemset2, false otherwise
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
