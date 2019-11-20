package lac.algorithms.cba;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import junit.framework.TestSuite;

public class SelectedRuleTest extends TestSuite {
    @Test
    public void getters() {
        Rule rule = new Rule((short) 3);
        SelectedRule sRule = new SelectedRule(rule, (short) 2, 50L);

        assertEquals(rule, sRule.getRule());
        assertEquals(new Long(50), sRule.getTotalErrors());
        assertEquals(2, sRule.getDefaultKlass());
    }

    @Test
    public void compateToGreaterReturnMinus1() {
        SelectedRule sRule1 = new SelectedRule(null, (short) 2, 50L);
        SelectedRule sRule2 = new SelectedRule(null, (short) 2, 40L);

        assertEquals(-1, sRule1.compareTo(sRule2));
    }

    @Test
    public void compateToGreaterReturn1() {
        SelectedRule sRule1 = new SelectedRule(null, (short) 2, 40L);
        SelectedRule sRule2 = new SelectedRule(null, (short) 2, 50L);

        assertEquals(1, sRule1.compareTo(sRule2));
    }

    @Test
    public void compateToGreaterReturn0() {
        SelectedRule sRule1 = new SelectedRule(null, (short) 2, 40L);
        SelectedRule sRule2 = new SelectedRule(null, (short) 2, 40L);

        assertEquals(0, sRule1.compareTo(sRule2));
    }

}
