
package net.myorb.math.specialfunctions.polylog;

import net.myorb.math.expressions.symbols.CommonFunctionBase;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.computational.integration.polylog.JonquierePolylog;

import net.myorb.math.realnumbers.CommonRealFunctionBase;
import net.myorb.math.complexnumbers.CommonComplexFunctionBase;
import net.myorb.math.complexnumbers.ComplexSpaceCore;
import net.myorb.math.complexnumbers.ComplexValue;

import net.myorb.data.abstractions.Function;

import java.util.Map;

/**
 * Li function computed from Jonquiere equations
 * - generic type breaks out to object specific to session data type
 * - real domain treated as wrapper of complex version
 * @author Michael Druckman
 */
public class Jonquiere <T> extends CommonFunctionBase <T>
{


	public Jonquiere () { super ("Li", "x"); }


	/*
	 * implementation of function
	 */

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public T eval (T z)
	{ return Li.eval (z); }
	protected Function < T > Li;


	/*
	 * accept configuration
	 */

	/* (non-Javadoc)
	 * @see net.myorb.math.complexnumbers.CommonFunctionBase#addConfiguration(java.util.Map)
	 */
	public void addConfiguration (Map <String, Object> parameters)
	{
		super.addConfiguration (parameters);
		JonquiereAC JAC = new JonquiereAC ();	// complex object always allocated
		JAC.addConfiguration (parameters);
		this.complexLi = JAC;
		
	}
	protected JonquiereAC complexLi;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.CommonFunctionBase#setEnvironment(net.myorb.math.expressions.evaluationstates.Environment)
	 */
	public void setEnvironment (Environment <T> environment)
	{
		super.setEnvironment (environment);

		switch (manager.getDataType ())					// switch on session data type as identified by data type manager in environment object
		{
			case Complex: identifyAsComplex (); break;
			case Real: identifyAsReal (this.complexLi); break;					// function wrapper allows complex function to process reals
			default: throw new RuntimeException ("Unexpected data type");
		}
	}


	/*
	 * acceptance of function object based on data type
	 */

	@SuppressWarnings("unchecked")
	void identifyAsReal (Function < ComplexValue<Double> > complexLi)
	{ this.Li = ( Function < T > )  new RealJonquiere (this.complexLi); }				// real wrapper for complex function
	@SuppressWarnings("unchecked") void identifyAsComplex ()
	{ this.Li = ( Function < T > ) this.complexLi; }									//  complex function as configured

}


/**
 * real domain version of Li function
 */
class RealJonquiere extends CommonRealFunctionBase
{

	public RealJonquiere
	(JonquiereAC complexLi)
	{ super ("Li"); this.complexLi = complexLi; }
	protected JonquiereAC complexLi;


	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public Double eval (Double x)
	{
		ComplexValue <Double>
			result = complexLi.eval
				(ComplexSpaceCore.manager.C (x, 0.0));
		if (this.complexLi.enforced != null && result.Im () != 0.0)
		{
			throw new RuntimeException ("function result is complex");
		}
		return result.Re ();
	}

}


/**
 * complex analytic continuation of Li function
 */
class JonquiereAC extends CommonComplexFunctionBase
{

	public JonquiereAC () { super ("Li"); }

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public ComplexValue<Double> eval
		(ComplexValue <Double> z) { return Li.eval (z); }
	protected Function < ComplexValue <Double> > Li = null;
	protected Object enforced = null;


	/* (non-Javadoc)
	 * @see net.myorb.math.complexnumbers.CommonFunctionBase#addConfiguration(java.util.Map)
	 */
	public void addConfiguration (Map <String, Object> parameters)
	{
		int s; // order of Li to be used

		super.addConfiguration (parameters);

		try { s = Integer.parseInt (parameters.get ("s").toString ()); }
		catch (Exception e) { throw new RuntimeException (ORDER_ERROR_TEXT); }

		this.enforced = parameters.get ("enforced");
		Object terms = parameters.get ("terms");

		if (terms == null)
			Li = JonquierePolylog.Li (s);
		else Li = JonquierePolylog.Li (s, Integer.parseInt (terms.toString ()));
	}
	static String ORDER_ERROR_TEXT = "Configration parameter 's' must identify the order of Li desired";

}

