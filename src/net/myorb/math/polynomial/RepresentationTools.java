
package net.myorb.math.polynomial;

import net.myorb.math.expressions.tree.JsonBinding;
import net.myorb.math.expressions.ExpressionSpaceManager;

import net.myorb.math.GeneratingFunctions.Coefficients;

import net.myorb.data.notations.json.JsonLowLevel.JsonValue;
import net.myorb.data.notations.json.JsonTools;

import java.util.ArrayList;
import java.util.List;

/**
 * helper class for conversion of Java objects to / from JSON
 * @author Michael Druckman
 */
public class RepresentationTools
{

	public static <T> void loadCoefficients
	(Coefficients <T> coefficients, JsonBinding.Node fromNode, ExpressionSpaceManager<T> using)
	{ toList (coefficients, fromNode.getMember ("Coefficients"), using); }

	public static <T> void toList (List <T> list, JsonValue from, ExpressionSpaceManager<T> mgr)
	{ toList (list, JsonTools.toNumberArray (from), mgr); }

	public static <T> void toList (List <T> list, Number[] from, ExpressionSpaceManager<T> mgr)
	{ for (Number n : from) list.add (mgr.convertFromDouble (n.doubleValue ())); }

	public static <T> List <T> toList (Number[] array, ExpressionSpaceManager<T> mgr)
	{
		List <T> list = new ArrayList <T> ();
		toList (list, array, mgr);
		return list;
	}

	public static <T> List <T> toList (T[] array)
	{
		List <T> list = new ArrayList <T> ();
		for (T t : array) list.add (t);
		return list;
	}

}
