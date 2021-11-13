
package net.myorb.math.expressions.commands;

import net.myorb.math.primenumbers.Factorization;
import net.myorb.math.primenumbers.ReportGenerators;
import net.myorb.math.primenumbers.sieves.SieveOfEratosthenes;
import net.myorb.math.expressions.controls.FactorizedEvaluationControl;
import net.myorb.math.expressions.evaluationstates.Environment;

import java.io.PrintStream;

/**
 * support for commands using prime numbers
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class PrimeNumbers<T> extends Utilities<T>
{


	public interface PrimeCommand
	extends KeywordCommand {}


	public PrimeNumbers (Environment<T> environment)
	{ super (environment); this.out = environment.getOutStream (); }


	/**
	 * redirect output to new console frame
	 * @param title text of title for new display
	 */
	public void directOutput (String title)
	{ out = openNewConsole (title, environment); }
	protected PrintStream out;


	/**
	 * run a sieve to populate the primes table
	 * @return a keyword command for the RUNSIEVE keyword
	 */
	public PrimeCommand constructRunsieveKeywordCommand ()
	{
		return new PrimeCommand ()
		{
			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.KeywordMap.KeywordCommand#describe()
			 */
			public String describe ()
			{ return "Run a sieve to populate the primes table"; }

			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.KeywordMap.KeywordCommand#execute(java.util.List)
			 */
			public void execute (CommandSequence tokens)
			{
				int size = Integer.parseInt (tokens.get (1).getTokenImage ());
				FactorizedEvaluationControl.initializeFactorizationTable (size);
			}
		};
	}


	/**
	 * compute data for Prime Table
	 * @return a keyword command for the PRIMETABLE keyword
	 */
	public PrimeCommand constructPrimetableKeywordCommand ()
	{
		return new PrimeCommand ()
		{
			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.KeywordMap.KeywordCommand#describe()
			 */
			public String describe ()
			{ return "Tabulate prime factors starting from specified"; }

			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.KeywordMap.KeywordCommand#execute(java.util.List)
			 */
			public void execute (CommandSequence tokens)
			{
				int starting = Integer.parseInt (tokens.get (1).getTokenImage ());
				directOutput ("Factors of first 100 integers over range starting at " + starting);
				getReportGenerators ().dumpFactors (starting, starting+100, out);
			}
		};
	}


	/**
	 * compute data for Prime Gaps display
	 * @return a keyword command for the PRIMEGAPS keyword
	 */
	public PrimeCommand constructPrimegapsKeywordCommand ()
	{
		return new PrimeCommand ()
		{
			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.KeywordMap.KeywordCommand#describe()
			 */
			public String describe ()
			{ return "Tabulate gaps between primes"; }

			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.KeywordMap.KeywordCommand#execute(java.util.List)
			 */
			public void execute (CommandSequence tokens)
			{
				int starting = Integer.parseInt (tokens.get (1).getTokenImage ());
				directOutput ("Gaps in Primes over range starting at " + starting);
				getReportGenerators ().dumpPrimeGapsBetween (starting, starting+100, out);
			}
		};
	}


	/**
	 * @return an implementation of the report generators
	 */
	private ReportGenerators getReportGenerators ()
	{
		ReportGenerators implementation;
		if ((implementation = (ReportGenerators)Factorization.getImplementation ()) == null)
		{
			Factorization.setImplementation (implementation = new ReportGenerators (100*1000));
			implementation.initFactorizationsWithStats (new SieveOfEratosthenes (implementation));
		}
		return implementation;
	}


}

