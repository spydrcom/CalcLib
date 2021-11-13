
package net.myorb.math.complexnumbers;

import net.myorb.math.OptimizedMathLibrary;
import net.myorb.math.SpaceManager;

/**
 * extend optimized library for use as complex library support
 * @param <T> type of component values on which operations are to be executed
 * @author Michael Druckman
 */
public class ComplexSupportImplementation<T> extends OptimizedMathLibrary<T>
	implements ComplexSupportLibrary<T>
{
	public ComplexSupportImplementation (SpaceManager<T> manager) { super (manager); }
}
