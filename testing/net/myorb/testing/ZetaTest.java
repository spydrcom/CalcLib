
package net.myorb.testing;

import net.myorb.math.ExtendedPowerLibrary;
import net.myorb.math.complexnumbers.JreComplexSupportLibrary;
import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;
import net.myorb.math.specialfunctions.Zeta;
import net.myorb.math.specialfunctions.ZetaRealAnalytic;

public class ZetaTest
{

	public static void main (String [] args)
	{
		double tw = 1.00/12.0;
		ZetaTest z = new ZetaTest ();
		z.initZetaAnalytic ("1000");
		System.out.println (z.zeta(-1.0)+tw);
	}

	static ExpressionFloatingFieldManager realMgr = new ExpressionFloatingFieldManager ();

	public ZetaTest ()
	{
		this.powerLibrary = new JreComplexSupportLibrary (realMgr);
	}
	ExtendedPowerLibrary<Double> powerLibrary = null;

	/* (non-Javadoc)
	 * @see net.myorb.mpmath.algorithms.MpmathPrimitives#zeta(java.lang.Object)
	 */
	public Double zeta (Double parameter)
	{
		return zeta.eval (parameter);
	}
	public void initZeta (String parameter)
	{
		zeta = new Zeta<Double> (realMgr, powerLibrary, realMgr);
		zeta.configure (Integer.parseInt (parameter));
	}
	public void initZetaAnalytic (String parameter)
	{
		zeta = new ZetaRealAnalytic (realMgr, powerLibrary, realMgr);
		zeta.configure (Integer.parseInt (parameter));
	}
	Zeta<Double> zeta = null;

}
