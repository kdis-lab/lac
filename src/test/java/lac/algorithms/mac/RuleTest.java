package lac.algorithms.mac;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

import junit.framework.TestSuite;

public class RuleTest extends TestSuite {
    @Test
    public void constructorsSetSupports() {
        Rule rule = new Rule((short) 1);
        assertEquals(0, rule.getSupportAntecedent());
        assertEquals(0, rule.getSupportRule());

        rule = new Rule((short) 1, (short) 2);
        assertEquals(0, rule.getSupportAntecedent());
        assertEquals(0, rule.getSupportRule());

        rule = new Rule(new Short[] { 1 }, new Short((short) 2));
        assertEquals(0, rule.getSupportAntecedent());
        assertEquals(0, rule.getSupportRule());

        rule = new Rule(new Short[] { 1 }, new HashSet<Integer>(Arrays.asList(1, 2)), new Short((short) 2),
                new HashSet<Integer>(Arrays.asList(1, 2, 3)));
        assertEquals(2, rule.getSupportAntecedent());
        assertEquals(3, rule.getSupportRule());

        rule = new Rule((short) 2, new HashSet<Integer>(Arrays.asList(1, 2)), new Short((short) 2),
                new HashSet<Integer>(Arrays.asList(1, 2, 3)));
        assertEquals(2, rule.getSupportAntecedent());
        assertEquals(3, rule.getSupportRule());
    }

    @Test
    public void setTidsetAntecedentUpdatesSupportAntecedent() {
        Rule rule = new Rule((short) 1);

        rule.setTidsetAntecedent(new HashSet<Integer>(Arrays.asList(1, 2)));
        assertEquals(2, rule.getSupportAntecedent());

        rule.setTidsetAntecedent(new HashSet<Integer>(Arrays.asList(1, 2, 3)));
        assertEquals(3, rule.getSupportAntecedent());
    }

    @Test
    public void setTidsetUpdatesSupport() {
        Rule rule = new Rule((short) 1);

        rule.setTidsetRule(new HashSet<Integer>(Arrays.asList(1, 2)));
        assertEquals(2, rule.getSupportRule());

        rule.setTidsetRule(new HashSet<Integer>(Arrays.asList(1, 2, 3)));
        assertEquals(3, rule.getSupportRule());
    }
    
    @Test
    public void isCombinableReturnsTrueWithSameKlass() {
        assertTrue(new Rule((short)1).isCombinable(new Rule((short) 1)));
    }

    @Test
    public void isCombinableReturnsFalseWithDifferentKlass() {
        assertFalse(new Rule((short)1).isCombinable(new Rule((short) 2)));
    }
}
