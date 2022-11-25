
package net.myorb.math.linalg;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.matrices.decomposition.GenericSupport;
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
	@SuppressWarnings("unchecked")
	public SolutionPrimitives <T> restore (SimpleStreamIO.TextSource source)
	{
		try
		{
			parseDecomposedMatrix (source);
			Class <?> c = Class.forName (this.parsedObject.getMemberString ("Solution"));
			Object o = c.getConstructor ( ExpressionSpaceManager.class ).newInstance (mgr);
			this.solution = (SolutionPrimitives <T>) o;
			return solution;
		}
		catch (Exception e) { throw new RuntimeException ("Error loading solution", e); }
	}
	SolutionPrimitives <T> solution;

	/**
	 * @return the Decomposition restored from the source
	 */
	public SolutionPrimitives.Decomposition getDecomposition ()
	{
		return this.solution.restore (this.parsedSource);
	}

}
