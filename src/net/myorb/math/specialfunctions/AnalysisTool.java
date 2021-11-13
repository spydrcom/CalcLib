
package net.myorb.math.specialfunctions;

import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;

import net.myorb.math.computational.IterativeProcessingSupportTabular;
import net.myorb.math.computational.IterativeIntegralApproximation;

import net.myorb.math.computational.TanhSinhQuadratureAlgorithms;
import net.myorb.math.computational.TanhSinhQuadratureTables;

import net.myorb.math.expressions.TypedRangeDescription;

import net.myorb.data.abstractions.PrimitiveRangeDescription;
import net.myorb.data.abstractions.Function;

/**
 * a tool for numerical analysis of
 *  function integrals seeking convergence of segments
 * @author Michael Druckman
 */
public class AnalysisTool extends IterativeProcessingSupportTabular.CommonExam
{

	/**
	 * @param range description of domain interval
	 * @param functionOfInterest the function being analyzed
	 * @param iterations the count of iterations to run
	 */
	public static void display
		(
			PrimitiveRangeDescription range,
			Function<Double> functionOfInterest,
			int iterations
		)
	{
		new AnalysisTool ().performEvaluation (range, functionOfInterest, iterations, 1E-6);
	}

	/**
	 * @param range description of domain interval
	 * @param functionOfInterest the function being analyzed
	 * @param iterations the count of iterations to run in the approximation
	 * @param errorMax maximum error to be allowed in evaluation
	 */
	public void performEvaluation
		(
			PrimitiveRangeDescription range,
			Function<Double> functionOfInterest,
			int iterations, double errorMax
		)
	{
		/*
		 * TSQ provides sanity check comparison
		 */
		runQuadrature (range, functionOfInterest, errorMax);

		/*
		 * showing trapezoid iterations provides convergence indication
		 */
		runIterativeApproximation (range, functionOfInterest, iterations);
	}

	/**
	 * @param range description of domain interval
	 * @param functionOfInterest the function being analyzed
	 * @param errorMax maximum error to be allowed in evaluation
	 */
	public void runQuadrature
		(
			PrimitiveRangeDescription range,
			Function<Double> functionOfInterest,
			double errorMax
		)
	{
		TsqError errorEval = new TsqError ();
		double integral = TanhSinhQuadratureAlgorithms.Integrate
			(
				functionOfInterest,
				range.getLo ().doubleValue (), range.getHi ().doubleValue (),
				errorMax, errorEval
			);
		show (integral, errorEval);
	}

	/**
	 * @param range description of domain interval
	 * @param functionOfInterest the function being analyzed
	 * @param iterations the count of iterations to run
	 */
	public void runIterativeApproximation
		(
			PrimitiveRangeDescription range,
			Function<Double> functionOfInterest,
			int iterations
		)
	{
		IterativeIntegralApproximation<Double> engine =
			new IterativeIntegralApproximation<Double>
			(
				TypedRangeDescription.getTypedRangeProperties (range, mgr),
				functionOfInterest, false
			);
		engine.setExam (this); engine.execute (iterations, 1);
	}
	ExpressionFloatingFieldManager mgr = new ExpressionFloatingFieldManager ();

	/**
	 * @param integral the computed integral value
	 * @param errorEval the error estimate
	 */
	void show (double integral, TsqError errorEval)
	{
		processCalculation (tsqComputation = integral);
		errorEval.display (this);
	}
	double tsqComputation;

	/**
	 * display error estimate
	 */
	static class TsqError extends TanhSinhQuadratureTables.ErrorEvaluation
	{
		void display (AnalysisTool tool) { tool.processEstimate (errorEstimate, numFunctionEvaluations); }
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.IterativeProcessingSupportTabular.CommonExam#processRow(java.lang.Object[])
	 */
	public void processRow (Object[] items)
	{
		Double approximation = Double.parseDouble (items[2].toString ());
		processApproximation (approximation, approximation - tsqComputation);
	}

	/**
	 * @param calculatedIntegral the calculated Integral value
	 */
	public void processCalculation (double calculatedIntegral)
	{
		System.out.print ("Computed TSQ = ");
		System.out.println (calculatedIntegral);
	}

	/**
	 * @param estimate the estimate of error in integral calculation
	 * @param samples the number of samples used in calculation
	 */
	public void processEstimate (double estimate, int samples)
	{
		System.out.print ("Error Estimate = "); System.out.println (estimate);
		System.out.print ("Function Evaluations = "); System.out.println (samples);
		System.out.println ("==="); System.out.println ();
	}

	/**
	 * @param approximation the iterative approximation of the integral value
	 * @param difference the difference between iterative approximation and quadrature evaluations
	 */
	public void processApproximation (double approximation, double difference)
	{
		System.out.print ("Iterative approximation = "); System.out.println (approximation);
		System.out.print ("Difference from TSQ = "); System.out.println (difference);
		System.out.println ("==="); System.out.println ();
	}

}
