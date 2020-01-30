/**
 * 
 */
package auctionhouse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author pbj
 *
 */
public class MoneyTest
{

    @Test
    public void testAdd()
    {
        Money val1 = new Money("12.34");
        Money val2 = new Money("0.66");
        Money result = val1.add(val2);
        assertEquals("13.00", result.toString());
    }

    /*
     ***********************************************************************
     * BEGIN MODIFICATION AREA
     ***********************************************************************
     * Add all your JUnit tests for the Money class below.
     */

    @Test
    public void testSubtract()
    {
        Money val1 = new Money("10.57");
        Money val2 = new Money("2.56");
        Money result = val1.subtract(val2);
        assertEquals("8.01", result.toString());
        Money a = new Money("10.2");
        Money b = new Money("0");
        Money result2 = a.subtract(b);
        assertEquals("10.20", result2.toString());
    }

    @Test
    public void testAddPercent()
    {
        Money val1 = new Money("10.00");
        double val2 = 45;
        Money result = val1.addPercent(val2);
        assertEquals("14.50", result.toString());
        Money val3 = new Money("10.00");
        double val4 = 0;
        Money result2 = val3.addPercent(val4);
        assertEquals("10.00", result2.toString());
    }

    @Test
    public void testToString()
    {

        Money val1 = new Money("13.3");
        Money val2 = new Money("10.111");
        Money val3 = new Money("10.115");

        String result1 = val1.toString();
        String result2 = val2.toString();
        String result3 = val3.toString();

        assertEquals("13.30", result1);
        assertEquals("10.11", result2);
        assertEquals("10.12", result3);

    }

    @Test
    public void testcompareTo()
    {
        Money val1 = new Money("12.34");
        Money val2 = new Money("0.66");
        Money val3 = new Money("0.66");

        int resultEqual = val2.compareTo(val3); // returns 0 when x == y
        assertEquals(0, resultEqual);

        int resultGreater = val1.compareTo(val3); // returns greater than 0 when x>y
        assertTrue(resultGreater > 0);

        int resultLess = val2.compareTo(val1); // returns less than 0 when x < y
        assertTrue(resultLess < 0);
    }

    @Test
    public void testLessEqual()
    {
        Money val1 = new Money("12.34");
        Money val2 = new Money("0.66");
        Money val3 = new Money("0.66");

        boolean resultTrue = val2.lessEqual(val1); // for less than
        assertTrue(resultTrue);
        boolean resultTrueEqual = val2.lessEqual(val3); // for equal to
        assertTrue(resultTrueEqual);
        boolean resultFalse = val1.lessEqual(val2); // for greater than, should return false
        assertFalse(resultFalse);
    }

    @Test
    public void testEqual()
    {
        Money val1 = new Money("12.34");
        Money val2 = new Money("0.66");
        Money val3 = new Money("0.66");

        boolean resultLessThan = val2.equals(val1); // for less than
        assertFalse(resultLessThan); // must be false for it to pass the test

        boolean resultTrueEqual = val2.equals(val3); // for equal to
        assertTrue(resultTrueEqual);

        boolean resultGreaterThan = val1.equals(val2); // for greater than, should return false
        assertFalse(resultGreaterThan); // must be false for it to pass the test
    }

    @Test
    public void testHashCode()
    {
        Money val1 = new Money("201.95");
        int value = val1.hashCode();
        assertEquals(20195, value);

    }

    /*
     * Put all class modifications above.
     ***********************************************************************
     * END MODIFICATION AREA
     ***********************************************************************
     */

}
