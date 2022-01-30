
package net.myorb.math.expressions.algorithms;

import net.myorb.math.TrigPowImplementation;
import net.myorb.math.ExtendedPowerLibrary;

/**
 * combination of libraries for Trig and Power functions
 * @param <T> manager for data type
 * @author Michael Druckman
 */
public interface CyclicAndPowerLibrary<T> extends
		TrigPowImplementation.SubAtomic<T>,
		ExtendedPowerLibrary<T>
{}
