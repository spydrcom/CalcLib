
package net.myorb.math.expressions.algorithms;

import net.myorb.math.complexnumbers.ComplexValue;
import net.myorb.math.complexnumbers.ComplexLibrary;

import net.myorb.math.complexnumbers.GammaLanczos;
import net.myorb.math.complexnumbers.ZetaComplexAnalytic;
import net.myorb.math.complexnumbers.JreComplexSupportLibrary;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.managers.ExpressionComplexFieldManager;
import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;

/**
 * library interface implementation for complex classes
 * @author Michael Druckman
 */
public class CLmathComplexImplementations
	extends CLmathGenericImplementations<ComplexValue<Double>>
{


	static ExpressionComplexFieldManager cmplxMgr = new ExpressionComplexFieldManager ();
	static ExpressionFloatingFieldManager realMgr = new ExpressionFloatingFieldManager ();
	static ComplexLibrary<Double> library = new ComplexLibrary<Double> (realMgr, cmplxMgr);


	public CLmathComplexImplementations
	(Environment<ComplexValue<Double>> environment)
	{
		super (cmplxMgr, cmplxMgr, library, environment);
		this.jreLib = new JreComplexSupportLibrary (realMgr);
		library.setMathLib (jreLib);
	}
	protected JreComplexSupportLibrary jreLib;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.CLmathGenericImplementations#initZetaAnalytic(java.lang.String)
	 */
	public void initZetaAnalytic (String parameter)
	{
		zeta = new ZetaComplexAnalytic<Double> (library);
		zeta.configure (Integer.parseInt (parameter));
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.CLmathPrimitives#getExpWrapper()
	 */
	public CommonWrapper getExpWrapper ()
	{
		return new CommonWrapper
		(
			new CommonFunction ()
			{
				public ComplexValue<Double>
					eval (ComplexValue<Double> x)
				{ return library.exp (x); }
			}
		);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.CLmathPrimitives#initGamma(java.lang.String)
	 */
	public void initGamma (String parameter)
	{
		GammaLanczos<Double> impl = new GammaLanczos<Double> (library);
		impl.setPrecision (Integer.parseInt (parameter));
		GAMMA = impl;
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.CLmathPrimitives#getLoggammaWrapper()
	 */
	public CommonWrapper getLoggammaWrapper ()
	{
		return new CommonWrapper
		(
			new CommonFunction ()
			{
				public ComplexValue<Double>
					eval (ComplexValue<Double> x)
				{ return library.ln (GAMMA.eval (x)); }
			}
		);
	}

}

