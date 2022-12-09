
package net.myorb.math.structures.loaders;

import net.myorb.math.structures.Loader;

import net.myorb.math.linalg.SolutionManager;
import net.myorb.math.linalg.SolutionPrimitives;

import net.myorb.math.expressions.algorithms.ClMathSysEQ;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.data.notations.json.JsonLowLevel.JsonValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Loader extension for Decomposed Matrix structure data
 * @param <T> manager for data type
 * @author Michael Druckman
 */
public class DecomposedMatrix <T>
	implements Loader.PortableValueRestoration <T, Object>
{

	/* (non-Javadoc)
	 * @see net.myorb.math.structures.Loader.PortableValueRestoration#fromJson(net.myorb.data.notations.json.JsonLowLevel.JsonValue, net.myorb.math.expressions.evaluationstates.Environment)
	 */
	public Object fromJson (JsonValue value, Environment <T> environment)
	{
		Map <String, Object> options = new HashMap <> ();
		SolutionManager <T> solManager = new SolutionManager <T> (environment.getSpaceManager ());
		SolutionPrimitives <T> solution = solManager.toSolutionPrimitives (value);

		return ClMathSysEQ.getSolutionManagerFor
			(solution, options, environment, this.toString ())
			.wrap (solution.restore (value));
	}

}
