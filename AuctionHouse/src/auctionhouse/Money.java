/**
 * 
 */
package auctionhouse;

/**
 * This is a class Money which Implements the Comparable Interface .
 * <p>It contains methods which carries out some basic operations on Money.
 * <p>
 * It also contains a variable Value of type double which holds the amount of Money.
 * @author Kuenzang Losel
 * @author Stylianos Charalambous
 * 
 */
public class Money implements Comparable<Money>
{

    private double value;

    private static long getNearestPence(double pounds)
    {
        return Math.round(pounds * 100.0);
    }

    private static double normalise(double pounds)
    {
        return getNearestPence(pounds) / 100.0;

    }

    public Money(String pounds)
    {
        value = normalise(Double.parseDouble(pounds));
    }

    private Money(double pounds)
    {
        value = pounds;
    }

    /**
     * Calculates the sum of two Money objects
     * 
     * @param m -The Money object to be added
     * @return -returns an object of type Money with the sum of two Money objects,
     *         which is the sum of their values.
     */

    public Money add(Money m)
    {
        return new Money(value + m.value);
    }

    /**
     * Calculates the difference between two Money objects.
     * 
     * @param m - The Money object to be subtracted
     * @return -returns an object of type Money with the difference of two Money
     *         objects, which is the difference of their values.
     */

    public Money subtract(Money m)
    {
        return new Money(value - m.value);
    }

    /**
     * Returns an object type Money with an added percentage.
     * 
     * @param percent - double representing a certain percentage which needs to be
     *                added to the Money
     * @return -returns an object of type Money with the added percentage, which is
     *         the sum of the value and the added percentage after they've been
     *         normalised.
     */

    public Money addPercent(double percent)
    {
        return new Money(normalise(value * (1 + percent / 100.0)));
    }

    /**
    * 
    */
    @Override
    public String toString()
    {
        return String.format("%.2f", value);

    }

    /**
     * Compares the value of two Money objects and returns an integer
     * 
     * @param m - Money object to be compared
     * @return -returns an integer that is:
     *         <p>
     *         a) Equal to zero if the values of the two Money objects are equal
     *         <p>
     *         b) Greater than zero if the value of the Money object passed as the
     *         parameter is less
     *         <p>
     *         c) Less than zero if the value of the Money object passed as the
     *         parameter is greater
     */

    public int compareTo(Money m)
    {
        return Long.compare(getNearestPence(value), getNearestPence(m.value));
    }

    /**
     * Checks whether the value of a Money object is less than or equal to the value
     * of another Money object and returns a Boolean
     * 
     * @param m - Money object to check if it is less than or equal to
     * @return -returns a Boolean that is True if the value of the Money object
     *         passed as the parameter is greater and False if otherwise
     */
    public Boolean lessEqual(Money m)
    {
        return compareTo(m) <= 0;
    }

    /**
     * Checks whether two Money objects are equal
     * 
     * @return -returns a Boolean that is True if the value of the Money object
     *         passed as the parameter is equal and False if otherwise
     */
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof Money)) return false;
        Money oM = (Money) o;
        return compareTo(oM) == 0;
    }

    /**
     * 
     */
    @Override
    public int hashCode()
    {
        return Long.hashCode(getNearestPence(value));
    }

}
