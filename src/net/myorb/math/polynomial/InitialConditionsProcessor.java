
package net.myorb.math.polynomial;

/**
 * description of infrastructure for processors 
 *  which provide Initial Conditions of polynomial solutions
 *  to given specific families of differential equations
 * @author Michael Druckman
 */
public class InitialConditionsProcessor
{

	/**
	 * specification of symbol table operations
	 */
	public static interface SymbolTranslator
	{

		/**
		 * get value for symbol
		 * @param symbol the name of the symbol
		 * @return the value of that symbol
		 */
		Double valueFor (String symbol);

		/**
		 * set the value of a symbol
		 * @param symbol the name of the symbol
		 * @param to the for the symbol
		 */
		void set (String symbol, Double to);

	}

	/**
	 * the defining functionality for a processor object
	 */
	public interface Calculator
	{
		/**
		 * given values set in the symbol table
		 *  set the Initial Conditions coefficients to value identified by algorithms
		 * @param coefficientManager the symbol manager for the coefficient computation protocol
		 */
		void computeCoefficients (SymbolTranslator coefficientManager);
	}

	/**
	 * map names of processors to the appropriate processor object
	 */
	public static class ProcessorMap extends java.util.HashMap <String, Calculator>
	{ private static final long serialVersionUID = 22081841563337176L; }
	private static ProcessorMap processors = null;
	
	/**
	 * identify a processor Implementation
	 * @param processorName the name of the processor
	 * @param implementation the object implementing the algorithm
	 */
	public static void addProcessor (String processorName, Calculator implementation)
	{
		if (processors == null) processors = new ProcessorMap ();
		processors.put (processorName, implementation);
	}

	/**
	 * allow for Polynomial Family support for Initial Conditions
	 * @param family a PolynomialFamily object that supports Calculator interface
	 */
	public static void addProcessor (PolynomialFamily <?> family)
	{
		addProcessor ( family.getName (), (Calculator) family );
	}

	/**
	 * find named processor and request symbol table update
	 * @param processorName the name assigned to the Implementation
	 * @param symbols the Symbol Translator object supplying and accepting algorithm data
	 * @throws RuntimeException for any failure to find the processor
	 */
	public static void computeInitialConditions
		(String processorName, SymbolTranslator symbols)
	throws RuntimeException
	{
		getProcessor ( processorName ).computeCoefficients ( symbols );
	}

	/**
	 * check processor for support of NamingConventions interface
	 * @param processorName the name assigned to the Implementation
	 * @return the Implementation of NamingConventions
	 */
	public static NamingConventions getNamingConventions (String processorName)
	{
		Calculator processor = getProcessor ( processorName );
		if ( processor instanceof NamingConventions ) return (NamingConventions) processor;
		throw new RuntimeException ("no support for naming conventions: " + processorName);
	}

	/**
	 * get access to the named Calculator
	 * @param processorName the name assigned to the Implementation
	 * @return functionality consistent with the Implementation interface
	 * @throws RuntimeException for failure to find identified processor
	 */
	public static Calculator getProcessor (String processorName) throws RuntimeException
	{
		if ( ! hasProcessorFor (processorName) )
		{ throw new RuntimeException ("Processor not found: " + processorName); }
		return processors.get (processorName);
	}

	/**
	 * determine if named processor has been posted
	 * @param processorName  the name assigned to the Implementation 
	 * @return TRUE if processor is available
	 */
	public static boolean hasProcessorFor (String processorName)
	{
		return processors != null && processors.containsKey (processorName);
	}

}