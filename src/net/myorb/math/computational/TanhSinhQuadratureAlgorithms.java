
package net.myorb.math.computational;

import net.myorb.data.abstractions.Function;

/**
 * integral approximation using fast numerical integration algorithm
 */
public class TanhSinhQuadratureAlgorithms
{


	// taken from C++ source (downloaded 6/25/2016) posted at 
	//   http://www.codeproject.com/Articles/31550/Fast-Numerical-Integration
	// translated to Java and refactored by Michael Druckman
	// original author: John D. Cook, info@johndcook.com


	/**
	 * compute integral of function over interval
	 * @param f the function to be integrated over interval
	 * @param lo the lo bound of the interval of integration
	 * @param hi the hi bound of the interval of integration
	 * @param targetAbsoluteError limit of acceptable error
	 * @param error output structure for computed error
	 * @return the computed integral
	 */
	public static double Integrate
	    (
	    	Function<Double> f,
	        double lo, double hi, double targetAbsoluteError,
	        TanhSinhQuadratureTables.ErrorEvaluation error
	    )
	{
		TanhSinhQuadratureAlgorithms quad =
			new TanhSinhQuadratureAlgorithms (lo, hi, f);
		if (error == null) error = new TanhSinhQuadratureTables.ErrorEvaluation ();
		quad.setErrorToBeObserved (targetAbsoluteError);
		int beyondHighestLevel = getHighestLevel()+1;

		quad.applyIteration (1); // no convergence check here, just run level 1
    	// Once convergence kicks in, error is approximately squared at each step.
        // Determine whether we're in the convergent region by looking at the trend in the error.
        // if (level == 1) continue; // previousDelta meaningless, so cannot check convergence.
		// loop starts with level 2 and the short-circuit test now makes sense

        for (int level = 2; level != beyondHighestLevel; ++level)
	    {
	    	quad.applyIteration (level);
	        // No infinite loop can result since there is an upper bound on the loop variable.
	        // Could possibly be replaced with a small positive upper limit on the size of currentDelta,
	        // but determining that upper limit would be difficult.  At worse, the loop is executed more times than necessary.  
	        if (quad.withinErrorConstraints (error)) break;
	    }
	    
	    return quad.finalizeResults (error);
	}


	/**
	 * @return highest index in offsets table
	 */
	static int getHighestLevel () { return TanhSinhQuadratureTables.offsets.length - 1; }


	/**
	 * margain for error = 10 * slope
	 * @param targetAbsoluteError requested error bound from user input
	 */
	void setErrorToBeObserved (double targetAbsoluteError)
	{
		errorToBeObserved = targetAbsoluteError * 0.1 / linear.slope;
	}
	double errorToBeObserved;


	/**
	 * compute error estimate and check against requested bounds
	 * @param error the object collecting estimates
	 * @return TRUE => error now within bounds
	 */
	boolean withinErrorConstraints (TanhSinhQuadratureTables.ErrorEvaluation error)
	{
        // Exact comparison with zero is harmless here.
		return currentDelta == 0.0 || computeCurrentError (error) < errorToBeObserved;
	}


	/**
	 * compute current estimate from deltas
	 * @param error the object collecting estimates
	 * @return current estimate of error
	 */
	double computeCurrentError (TanhSinhQuadratureTables.ErrorEvaluation error)
	{
        // previousDelta != 0 or would have been kicked out previously
        double r = Math.log( currentDelta ) / Math.log( previousDelta );

        if (r > 1.9 && r < 2.1) 
        {
            // If convergence theory applied perfectly,
        	// r would be 2 in the convergence region.  r close to 2 is good enough.
        	// We expect the difference between this integral estimate and the next one to be roughly delta^2.
        	error.errorEstimate = currentDelta * currentDelta; 
        }
        else
        {
            // Not in the convergence region.  Assume only that error is decreasing.
        	error.errorEstimate = currentDelta;
        }

        return error.errorEstimate;
	}


	/**
	 * multiply slope into
	 *  error estimate and final integral value.
	 * copy number of function into error object
	 * @param error the object collecting estimates
	 * @return the final integral value
	 */
	double finalizeResults (TanhSinhQuadratureTables.ErrorEvaluation error)
	{
	    error.numFunctionEvaluations = linear.numFunctionEvaluations;
	    error.errorEstimate *= linear.slope;
	    return integral * linear.slope;
	}


	/**
	 * compute contribution to integral for level
	 * @param level level number being applied
	 */
	void applyIteration (int level)
	{
        // difference in consecutive integral estimates
        double newContribution = linear.computeContribution (level) * (h *= 0.5);
        previousDelta = currentDelta; currentDelta = Math.abs (0.5*integral - newContribution); 
        integral = 0.5*integral + newContribution;
	}
    double previousDelta, currentDelta = Double.MAX_VALUE;
    double integral = 0.0, h = 1.0;


	TanhSinhQuadratureAlgorithms
		(
			double lo, double hi,
			Function<Double> f
		)
	{
	    linear = new TanhSinhLinearCoordinateChange (lo, hi, f);
	    integral = linear.initialValue ();
	}
	TanhSinhLinearCoordinateChange linear = null;


}


