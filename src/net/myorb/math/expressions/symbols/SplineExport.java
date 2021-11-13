
package net.myorb.math.expressions.symbols;

import net.myorb.math.expressions.*;
import net.myorb.math.*;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

/**
 * export descriptor contents as Java code
 * describe a spline given the descriptor basis
 * @author Michael Druckman
 */
public class SplineExport<T>
{


	/**
	 * export master
	 * @param descriptor to be exported
	 */
	public void forDescriptor (SplineDescriptor<T> descriptor)
	{
		manager =
			descriptor.getExpressionSpaceManager ();
		String name = descriptor.getName ();

		try { out = new PrintWriter (new FileWriter (new File ("exports/" + name + ".java"))); }
		catch (Exception e) { throw new RuntimeException ("Export failed"); }

		formatHeader (name);
		out.println ("{");

		out.println ("\tpublic void initialize ()");
		out.println ("\t{");

		formatName (name);
		setHiConstraint (descriptor);
		setSegmentLoConstraints (descriptor);
		addSegmentPolynomials (descriptor);

		out.println ("\t}");
		out.println ("}");

		out.close ();
	}
	protected ExpressionSpaceManager<T> manager;
	protected PrintWriter out;


	/**
	 * process hi constraint
	 * @param descriptor object holding data being exported
	 */
	private void setHiConstraint (SplineDescriptor<T> descriptor)
	{
		out.print ("\t\tsetHiConstraint (");
		out.print (descriptor.getHiConstraint ());
		out.print (");");
		out.println ();
	}


	/**
	 * comma separated list
	 * @param list the list of values
	 * @return text of the list
	 */
	private String formatList (List<T> list)
	{
		String sep = "";
		StringBuffer buffer = new StringBuffer ();
		for (T t : list) { buffer.append (sep).append (t); sep = ", "; }
		return buffer.toString ();
	}


	/**
	 * list of value of lo constraints from each segment
	 * @param descriptor object holding data being exported
	 */
	private void setSegmentLoConstraints (SplineDescriptor<T> descriptor)
	{
		out.print ("\t\tsetSegmentLoConstraints (");
		out.print (formatList (descriptor.getSegmentLoConstraints ()));
		out.println (");");
	}


	/**
	 * list of polynomials associated with segments
	 * @param descriptor object holding data being exported
	 */
	private void addSegmentPolynomials (SplineDescriptor<T> descriptor)
	{
		List<Polynomial.PowerFunction<T>> p = descriptor.getSegmentTransforms ();
		for (Polynomial.PowerFunction<T> c : p) {addSegmentPolynomial (c); }
	}


	/**
	 * format polynomial descriptions
	 * @param function the PowerFunction associated with this polynomial
	 */
	private void addSegmentPolynomial (Polynomial.PowerFunction<T> function)
	{
		String name = function.getPolynomial ()
			.getClass ().getSimpleName ();
		out.print ("\t\taddSegment" + name + " (");
		out.print (formatList (function.getCoefficients ()));
		out.println (");");
	}


	/**
	 * name of function is carried with metadata
	 * @param name the name of the function
	 */
	private void formatName (String name)
	{
		out.print ("\t\tsetName (\"");
		out.print (name); out.print ("\");");
		out.println ();
	}


	/**
	 * format class definition
	 * @param name the name of the function
	 */
	private void formatHeader (String name)
	{
		out.print ("public class "); out.print (name);
		out.print (" extends "); out.print (BASE_CLASS);
		out.println ();
	}
	static final String BASE_CLASS =
	"net.myorb.math.expressions.symbols.SplineInRealDomain";


}

