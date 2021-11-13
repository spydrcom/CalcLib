
package net.myorb.math.computational;

import net.myorb.math.realnumbers.DoubleFloatingFieldManager;
import net.myorb.data.abstractions.Function;

/**
 * shift domain to interval [-1, 1]
 */
public class TanhSinhLinearCoordinateChange extends LinearCoordinateChange<Double>
{


	// taken from C++ source (downloaded 6/25/2016) posted at 
	//   http://www.codeproject.com/Articles/31550/Fast-Numerical-Integration
	// translated to Java and refactored by Michael Druckman
	// original author: John D. Cook, info@johndcook.com


	public static final double
		weights[] = TanhSinhQuadratureTables.doubleExponentialWeights,
		abcissas[] = TanhSinhQuadratureTables.doubleExponentialAbcissas;
	public static final int offsets[] = TanhSinhQuadratureTables.offsets;


	// Apply the linear change of variables x = ct + d
    // $$\int_a^b f(x) dx = c \int_{-1}^1 f( ct + d ) dt$$
    // c = (b-a)/2, d = (a+b)/2


	/**
	 * framed for Tanh-Sinh, interval change to [-1,1]
	 * @param lo the lo bound of the original interval
	 * @param hi the hi bound of the original interval
	 * @param f the function that will be evaluated
	 */
	public TanhSinhLinearCoordinateChange
		(
			double lo, double hi,
			Function<Double> f
		)
	{
		super (lo, hi, f, new DoubleFloatingFieldManager ());
	}


	/**
	 * apply contributions of level
	 * @param level number of level being applied
	 * @return sum of terms of level
	 */
	public double computeContribution (int level)
	{
        double newContribution = 0.0;
        if (offsets.length-1 < level+1) return 0;
        for (lastOffset = offsets[level]; lastOffset != offsets[level+1]; ++lastOffset)
        { newContribution += weights[lastOffset] * evalSmp (abcissas[lastOffset]); }
        numFunctionEvaluations = 2*lastOffset - 1;
        return newContribution;
	}
	protected int lastOffset = 0, numFunctionEvaluations = 0;


	/**
	 * the function evaluates at abcissas[0] + level 0 contribution
	 * @return sum of 0 term and level 0 contributions
	 */
	public double initialValue ()
	{
		return weights[0] * eval (0.0) + computeContribution (0);
	}


}

