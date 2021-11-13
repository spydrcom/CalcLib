
package net.myorb.math;

/**
 * 
 * all functionality around management of value sign (+/-)
 * 
 * @author Michael Druckman
 *
 */
public class SignManager implements SignManagementOperations
{


	/**
	 * initialize object with negative flag false
	 */
	public SignManager () { negative = false; }
	public SignManager (boolean negative) { this.negative = negative; }
	protected boolean negative;


	/* (non-Javadoc)
	 * @see net.myorb.math.SignManagementOperations#isNegative()
	 */
	public boolean isNegative () { return negative; }

	/* (non-Javadoc)
	 * @see net.myorb.math.SignManagementOperations#setSign(boolean)
	 */
	public void setSign (boolean negative)
	{ this.negative = negative; }

	/* (non-Javadoc)
	 * @see net.myorb.math.SignManagementOperations#invertSign()
	 */
	public void invertSign ()
	{ this.negative = !this.negative; }

	/* (non-Javadoc)
	 * @see net.myorb.math.SignManagementOperations#copySign(net.myorb.math.SignManagementOperations)
	 */
	public void copySign (SignManagementOperations other)
	{
		setSign (other.isNegative ());
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.SignManagementOperations#copyInvertedSign(net.myorb.math.SignManagementOperations)
	 */
	public void copyInvertedSign (SignManagementOperations other)
	{
		setSign (!other.isNegative ());
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.SignManagementOperations#setSignXor(net.myorb.math.SignManagementOperations, net.myorb.math.SignManagementOperations)
	 */
	public void setSignXor
	(SignManagementOperations factor1, SignManagementOperations factor2)
	{ setSign (xor (factor1.isNegative (), factor2.isNegative ())); }
	
	/**
	 * simple logical x XOR y = (x or y) and not (x and y)
	 * @param x left side value of equation
	 * @param y right side value
	 * @return x xor y
	 */
	public static boolean xor (boolean x, boolean y)
	{ return (x || y) && !(x && y); }

	/**
	 * provide a monitor that toggles the flag on each reference
	 * @return the current value of the flag
	 */
	public boolean signToggle ()
	{
		boolean currently;
		currently = this.isNegative (); this.invertSign ();
		return currently;
	}

	/**
	 * use sign manager to toggle value sign on alternating terms
	 * @param value the value object to toggle
	 */
	public void toggleSign (SignManagementOperations value)
	{
		if (signToggle ()) value.invertSign ();
	}


	// sign support for odd and even terms

	public static int termIsOddAlt (int termNo)
	{ return termIsEven (termNo)? 0: (((termNo-1)/2) % 2 == 1? -1: 1); }
	public static boolean termIsOdd (int termNo) { return termNo % 2 == 1; }
	public static boolean termIsEven (int termNo) { return termNo % 2 == 0; }
	public static int termIsEvenAlt (int termNo)
	{ return termIsOddAlt (termNo+1); }


}

