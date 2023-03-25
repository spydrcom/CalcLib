
package net.myorb.math.polynomial.algebra;

import net.myorb.math.polynomial.algebra.Elements;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.evaluationstates.Subroutine;

/**
 * command implementation for Series Expansion Solve algorithm
 * @author Michael Druckman
 */
public class Solution <T> extends Elements
{


	public Solution
		(Environment <T> environment) { this.environment = environment; }
	protected Environment <T> environment;


	public void analyze (SeriesExpansion <T> series, Subroutine <T> profile)
	{
		this.stream = environment.getOutStream ();
		this.series = series; this.profile = profile;
		this.analysis = series.analysis;

		showAnalysis ();
	}
	protected java.io.PrintStream stream;
	protected Manipulations.Powers analysis;
	protected SeriesExpansion <T> series;
	protected Subroutine <T> profile;


	public void showAnalysis ()
	{
		java.util.Set <String> refs;

		stream.println (); stream.println ("===");

		stream.print (series.expandedRoot);

		stream.println (); stream.println ("===");

		for (Double power : analysis.getPowers ())
		{
			refs = references (analysis.getTermFor (power));
			stream.print (Constant.asInteger (power.toString ()));
			stream.print ("\t"); stream.print (refs);
			stream.println ();
		}

		stream.println ("===");
		stream.println ();
	}
	
	java.util.Set <String> references (Factor factor)
	{
		java.util.Set <String> symbols =
			new java.util.HashSet <String> ();
		factor.identify (symbols);
		return symbols;
	}


}

