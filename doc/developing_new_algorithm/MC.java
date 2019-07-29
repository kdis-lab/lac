 package lac.algorithms.mc;

import java.util.Collections;
import java.util.HashMap;

import lac.algorithms.Algorithm;
import lac.algorithms.Rule;
import lac.data.Dataset;

// imports are obviated by space limitations

 public class MC extends Algorithm {
 	public MC(Config config) {
 		this.config = config;
 	}
    public Classifier train(Dataset training) throws Exception {
        HashMap<Short, Long> frequencyByKlass = this.getFrequencyByKlass(training);
    
        short klass;
        // Generate rule with minority or majority class
        if (((Config) this.config).getMajority()) {
            klass = Collections.max(frequencyByKlass.entrySet(), 
                        (e1, e2) -> e1.getValue().compareTo(e2.getValue())
                    ).getKey();
        } else {
            // Get this klass with the smallest frequency of occurrence
            klass = Collections.min(frequencyByKlass.entrySet(), 
                        (e1, e2) -> e1.getValue().compareTo(e2.getValue())
                    ).getKey();
        }
    
        return new Classifier(new Rule(new short[]{}, klass));
    }
    
    private HashMap<Short, Long> getFrequencyByKlass(Dataset dataset) {
        HashMap<Short, Long> frequencyByKlass = new HashMap<Short, Long>();
        
        for (int i = 0; i < dataset.size(); i++) {
            // Get the klass for the instance in position i
            short klass = dataset.getKlassInstance(i);
            
            Long count = frequencyByKlass.get(klass);
            if (count == null) {
                frequencyByKlass.put(klass, 1L);
            } else {
                frequencyByKlass.put(klass, count + 1);
            }
        }
        return frequencyByKlass;
    }
 }