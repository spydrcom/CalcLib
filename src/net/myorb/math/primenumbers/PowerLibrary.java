
package net.myorb.math.primenumbers;

import net.myorb.math.ExtendedPowerLibrary;
import net.myorb.math.expressions.JavaPowerLibrary;

import net.myorb.data.abstractions.FunctionWrapper;
import net.myorb.data.abstractions.Function;

import java.util.Map;

/**
 * Factorization power functions
 * @author Michael Druckman
 */
public class PowerLibrary extends CommonFunctionBase
{


	public enum Operations { SQRT, LN, EXP }

	public PowerLibrary () { super ("POWER"); }


	/*
	 * implementation of function
	 */

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public Factorization eval (Factorization z) { return function.eval (z); }

	/**
	 * @param called the type of the operation
	 */
	public void setFunction (String called)
	{
		switch (Operations.valueOf (called))
		{
			case LN:	setFunctionTo ( (x) -> lib.ln (x) );	break;
			case EXP:	setFunctionTo ( (x) -> lib.exp (x) );	break;
			case SQRT:	setFunctionTo ( (x) -> lib.sqrt (x) );	break;
		}
	}

	/**
	 * @param f the body of the function
	 */
	public void setFunctionTo (FunctionWrapper.F < Factorization > f)
	{ this.function = new FunctionWrapper < Factorization > (f, manager); }
	protected Function < Factorization > function;


	/*
	 * accept configuration
	 */

	/* (non-Javadoc)
	 * @see net.myorb.math.complexnumbers.CommonFunctionBase#addConfiguration(java.util.Map)
	 */
	public void addConfiguration (Map <String, Object> parameters)
	{
		super.addConfiguration (parameters);
		try { setFunction (parameters.get ("op").toString ()); }
		catch (Exception e) { throw new RuntimeException (ORDER_ERROR_TEXT); }
	}
	static String ORDER_ERROR_TEXT = "Configration parameter 'op' must identify the power function desired";

	public static ExtendedPowerLibrary <Factorization> lib = new JavaPowerLibrary <Factorization> (manager);

}
