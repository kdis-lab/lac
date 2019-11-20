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

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Base class used to represent a Dataset in LAC. All the other formats has to
 * extended from it.
 */
public abstract class Dataset {
    /**
     * Array with all the instances
     */
    protected ArrayList<Instance> instances = null;

    /**
     * Meta-data information of the class
     */
    protected Klass klass;

    /**
     * Frequecy by klass, calculated while reading the dataset
     */
    protected HashMap<Short, Long> frequencyByKlass;

    /**
     * Meta data information of the attributes
     */
    protected ArrayList<Attribute> attributes;

    /**
     * Dictionary used to do the matching between the internal value in short, to
     * the original value
     */
    protected HashMap<Short, String> indexIdentities;

    /**
     * Array to store the internal representation by attribute
     */
    protected ArrayList<ArrayList<Short>> indexes;

    /**
     * Internal field used to calculate the internal representation of the values
     */
    protected short lastIndex;

    /**
     * Position of the class in the instance
     */
    protected int indexKlass;

    /**
     * Flag to detect whether dataset has missing or not
     */
    protected boolean hasMissing = false;

    /**
     * Name of the dataset
     */
    protected String name;

    /**
     * Constructor
     */
    public Dataset() {
        this.lastIndex = -1; // In that way, it will start at 0
        this.frequencyByKlass = new HashMap<Short, Long>();
        this.indexIdentities = new HashMap<Short, String>();
        this.instances = new ArrayList<Instance>();
        this.attributes = new ArrayList<Attribute>();
        this.indexes = new ArrayList<ArrayList<Short>>();
    }

    /**
     * Get the number of classes
     * 
     * @return the number of classes
     */
    public int getNumberKlasses() {
        return this.klass.getNumberValues();
    }

    /**
     * Get the number of values per attribute
     * 
     * @param indexAttribute index of the attribute
     * @return the number of values for the variable situated in the specified index
     * @throws Exception
     */
    public int getNumberLabels(int indexAttribute) throws Exception {
        return this.attributes.get(indexAttribute).getNumberValues();
    }

    /**
     * Get the number of variables
     * 
     * @return the number of variables
     */
    public int getNumberAttributes() {
        return this.attributes.size();
    }

    /**
     * Get the number of instances contained into the dataset
     * 
     * @return the number of instances
     */
    public int size() {
        return this.instances.size();
    }

    /**
     * Get the meta-data for the specified attribute
     * 
     * @param indexAttribute index for the attribute
     * @return the metada information for the specified attribute
     */
    public Attribute getAttribute(int indexAttribute) {
        return this.attributes.get(indexAttribute);
    }

    /**
     * Get the example situated in this position
     * 
     * @param indexInstance index for the instance
     * @return the example situated in this index
     */
    public Instance getInstance(int indexInstance) {
        return this.instances.get(indexInstance);
    }

    /**
     * Read a dataset from disk, and return an instance of Dataset. It makes use of
     * the extension to determine which parser should be used
     * 
     * @param path where the file is stored
     * @return an instance of dataset
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static Dataset read(String path) throws Exception {
        // +1 and only the extension will be saved (without dot)
        String extension = path.substring(path.lastIndexOf(".") + 1).toLowerCase();
        // Extension with the first character as upcase
        String upperCaseExtension = extension.substring(0, 1).toUpperCase() + extension.substring(1);
        try {
            @SuppressWarnings("rawtypes")
            Class datasetKlass = Class.forName("lac.data." + upperCaseExtension + "Dataset");
            return (Dataset) datasetKlass.getDeclaredConstructor(String.class).newInstance(path);
        } catch (ClassNotFoundException exception) {
            System.err.println("File format (" + upperCaseExtension + "Dataset) not supported.");
            throw exception;
        }
    }

    /**
     * Adds an instance to the current dataset
     * 
     * @param instance to be added into the dataset
     * @throws Exception
     */
    public void addInstance(String[] instance) throws Exception {
        String klass = instance[indexKlass].trim();

        Short internalRepresentationKlass = this.getIndexByValueKlass(klass);
        Instance example = new Instance(instance.length);

        if (this.frequencyByKlass.containsKey(internalRepresentationKlass)) {
            this.frequencyByKlass.put(internalRepresentationKlass,
                    this.frequencyByKlass.get(internalRepresentationKlass) + 1);
        } else {
            this.frequencyByKlass.put(internalRepresentationKlass, 1L);
        }

        for (int i = 0, j = 0; i < instance.length; i++) {
            if (i == this.indexKlass)
                continue;

            if (this.attributes.get(j).isNominal()) {
                short internalRepresentation = this.getIndexByValue(j, instance[i].trim());

                if (internalRepresentation < 0)
                    this.hasMissing = true;

                example.set(j, internalRepresentation);

                if (!this.indexIdentities.containsKey(example.asNominal()[j])) {
                    this.indexIdentities.put(example.asNominal()[j], instance[i].trim());
                }
            } else
                example.set(j, null);

            j++;
        }
        if (internalRepresentationKlass < 0)
            this.hasMissing = true;
        example.setKlass(internalRepresentationKlass);

        if (!this.indexIdentities.containsKey(internalRepresentationKlass)) {
            this.indexIdentities.put(internalRepresentationKlass, klass);
        }

        this.instances.add(example);
    }

    /**
     * Get the internal representation from an attribute and its value
     * 
     * @param indexAttribute index of the attribute
     * @param value          for the attribute
     * @return the internal representation used to represent this value in this
     *         attribute
     * @throws Exception
     */
    public short getIndexByValue(int indexAttribute, String value) throws Exception {
        for (int i = 0; i < this.attributes.get(indexAttribute).getNumberValues(); i++) {
            if (this.attributes.get(indexAttribute).getValues()[i].equals(value))
                return this.indexes.get(indexAttribute).get(i);
        }
        return -1;
    }

    /**
     * Get the internal representation for a specified class
     * 
     * @param value to obtain the internal representation
     * @return the internal representation used to the specified parameter
     */
    public short getIndexByValueKlass(String value) {
        ArrayList<Short> klassIndexes = this.indexes.get(this.indexKlass);
        for (int i = 0; i < klassIndexes.size(); i++) {
            if (this.klass.getValue(i).equals(value)) {
                return klassIndexes.get(i);
            }
        }
        return -1;
    }

    /**
     * Add the metadata information for a new nominal attribute
     * 
     * @param name   for the attribute
     * @param values for this attribute
     */
    public void addNominalAttribute(String name, String[] values) {
        this.indexes.add(new ArrayList<Short>());

        int indexAttribute = this.attributes.size();
        for (short i = 1; i <= values.length; i++) {
            this.indexes.get(indexAttribute).add((short) (i + lastIndex));
        }
        lastIndex += values.length;
        this.attributes.add(new Attribute(name, values));
    }

    /**
     * Add the metadata information for a new numeric attribute
     * 
     * @param nameAttribute for the attribute
     */
    public void addNumericAttribute(String nameAttribute) {
        Attribute attribute = new Attribute(nameAttribute, Attribute.TYPE_NUMERIC);
        this.indexes.add(new ArrayList<Short>());
        this.attributes.add(attribute);
    }

    /**
     * Get the internal representation for the k-class value
     * 
     * @param k position of the value
     * @return the internal representation for the class
     */
    public short getKlass(int k) {
        return this.indexes.get(indexKlass).get(k);
    }

    /**
     * Get the metadata information for the class
     * 
     * @return the metadata information for the class
     */
    public Klass getKlass() {
        return this.klass;
    }

    /**
     * Get the internal representation for the value specified
     * 
     * @param indexAttribute index of the attribute
     * @param indexValue     index of the value in the attribute
     * @return the internal representation for this value
     */
    public Short getIndexAttribute(int indexAttribute, int indexValue) {
        return this.indexes.get(indexAttribute).get(indexValue);
    }

    /**
     * Get the original value from the internal representation value
     * 
     * @param index internal representation
     * @return the original value for this internal representation
     */
    public String getValueByIndex(short index) {
        return this.indexIdentities.get(index);
    }

    /**
     * Get internal representation for the class specified as parameter
     * 
     * @param klass original value to get internal representation
     * @return the internal representation for this class
     */
    public int getIndexKlass(String klass) {
        for (int i = 0; i < this.klass.getNumberValues(); i++) {
            if (klass.equals(this.klass.getValue(i)))
                return i;
        }
        return -1;
    }

    /**
     * Adds the metadata information for the class
     * 
     * @param values all the possible values which could take
     */
    public void addKlass(String[] values) {
        this.indexes.add(new ArrayList<Short>());

        indexKlass = this.attributes.size();

        for (short i = 1; i <= values.length; i++) {
            this.indexes.get(indexKlass).add((short) (i + lastIndex));
        }
        lastIndex += values.length;

        this.klass = new Klass(values);
    }

    /**
     * Get the class value for the instance at the specified position
     * 
     * @param i index for the instance
     * @return the class for this instance
     */
    public short getKlassInstance(int i) {
        return this.instances.get(i).getKlass();
    }

    /**
     * Get the number of instances per class
     * 
     * @param klass to check the number of instances
     * @return the number of instances for the specified class
     */
    public long getNumberInstancesPerKlass(Short klass) {
        return this.frequencyByKlass.containsKey(klass) ? this.frequencyByKlass.get(klass) : 0;
    }

    /**
     * Get frequency by each class. Key are the internal representation, and the
     * value is the frequency
     * 
     * @return the frequency by class
     */
    @SuppressWarnings("unchecked")
    public HashMap<Short, Long> getFrequencyByKlass() {
        return (HashMap<Short, Long>) this.frequencyByKlass.clone();
    }

    /**
     * Get the number of singletons
     * 
     * @return the number of singletons
     * @throws Exception
     */
    public int getNumberSingletons() throws Exception {
        int total = 0;

        for (int i = 0; i < this.getNumberAttributes(); i++) {
            total += this.getNumberLabels(i);
        }

        total += this.getNumberKlasses();

        return total;
    }

    /**
     * Checks if dataset has numeric attributes
     * 
     * @return true if dataset has numeric attributes, false otherwise
     */
    public boolean hasNumericAttributes() {
        boolean hasNumericAttributes = false;

        for (int i = 0; i < this.attributes.size() && !hasNumericAttributes; i++) {
            if (this.attributes.get(i).isNumeric())
                hasNumericAttributes = true;
        }

        return hasNumericAttributes;
    }

    /**
     * Get an attribute by the internal representation of one of its values
     * 
     * @param index internal representation to finds its attribute
     * @return the attribute containing this value
     */
    public Attribute getAttributeByIndex(Short index) {
        int indexAttribute = -1;
        boolean found = false;
        for (indexAttribute = 0; indexAttribute < this.attributes.size() && !found; indexAttribute++) {
            for (int j = 0; j < this.indexes.get(indexAttribute).size() && !found; j++) {
                if (this.indexes.get(indexAttribute).get(j) == index) {
                    found = true;
                }
            }
        }

        return this.attributes.get(indexAttribute - 1);
    }

    /**
     * Check if current dataset has missing values
     */
    public boolean hasMissing() {
        return this.hasMissing;
    }

    /**
     * Get the name of the dataset
     * 
     * @return the name of the dataset
     */
    public String getName() {
        return this.name;
    }
}
