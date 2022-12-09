
package net.myorb.math.linalg;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.matrices.decomposition.GenericSupport;

import net.myorb.data.notations.json.JsonLowLevel.JsonValue;
import net.myorb.data.notations.json.JsonSemantics;

import net.myorb.data.abstractions.SimpleStreamIO;

/**
 * processing for control of solution objects
 * @param <T> data type used in Matrix objects
 * @author Michael Druckman
 */
public class SolutionManager <T> extends GenericSupport <T>
{


	public SolutionManager (ExpressionSpaceManager <T> mgr) { super (mgr); }


	/**
	 * restore a text stored solution
	 * @param source the location of the stored copy
	 * @return the QRDecomposition
	 */
	public SolutionPrimitives <T> restore (SimpleStreamIO.TextSource source)
	{
		parseDecomposedMatrix (source);
		return toSolutionPrimitives (this.parsedSource);
	}


	/**
	 * convert a JSON tree to a solution object
	 * @param JSON the JSON tree source for the solution
	 * @return Solution Primitives described by JSON source
	 * @throws RuntimeException for parse and member errors
	 */
	@SuppressWarnings("unchecked") public SolutionPrimitives <T>
		toSolutionPrimitives (JsonValue JSON)
	throws RuntimeException
	{
		try
		{
			return this.solution =
				(SolutionPrimitives <T>) constructSolution
					( (JsonSemantics.JsonObject) JSON );
		}
		catch (Exception e) { throw new RuntimeException ("Error loading solution", e); }
	}


	/**
	 * construct object given by Solution member
	 * @param JSON the object parsed from JSON source
	 * @return the instance of the object constructed from Solution path
	 * @throws Exception for errors raised by the constructor
	 */
	public Object constructSolution (JsonSemantics.JsonObject JSON) throws Exception
	{
		Class <?> c = Class.forName (JSON.getMemberString ("Solution"));
		return c.getConstructor ( ExpressionSpaceManager.class ).newInstance (mgr);
	}


	/**
	 * restore a Decomposition object
	 * @return the Decomposition restored from the source
	 */
	public SolutionPrimitives.Decomposition getDecomposition ()
	{
		return this.solution.restore (this.parsedSource);
	}
	protected SolutionPrimitives <T> solution;


}

