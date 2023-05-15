
package net.myorb.math.expressions;

import net.myorb.math.expressions.charting.ContourPlotProperties;
import net.myorb.math.expressions.gui.rendering.MathMarkupNodes;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.evaluationstates.Subroutine;

import net.myorb.charting.DisplayGraphTypes;

/**
 * plot preparation for 3D contour type plots
 * - rendering support for plot property descriptions
 * @param <T> data type for plot
 * @author Michael Druckman
 */
public class ContourRendering <T> extends PrettyPrinter <T>
{


	public ContourRendering (Environment <T> environment) { super (environment); }


	/**
	 * equation only render
	 * @param s Subroutine to be rendered
	 * @return Widget component holding rendered content
	 * @throws Exception for any errors
	 */
	public Widget toRenderedWidget (Subroutine <T> s) throws Exception
	{ return toRenderedWidget (s.getFunctionTokens (), s.getParameterNameList ()); }


	/**
	 * describe an equation and domain
	 * @param s Subroutine to be rendered
	 * @param properties a description of the plot properties
	 * @return Widget component holding rendered content
	 * @throws Exception for any errors
	 */
	public Widget toRenderedWidget
		(
			Subroutine <T> s,
			ContourPlotProperties properties
		)
	throws Exception
	{
		String EqnMML = this.toMML
			( s.getFunctionTokens (), P = s.getParameterNameList () );
		String SpcMML = this.spaceDescriptionFor (properties);
		String MML = strip (EqnMML) + space () + SpcMML;
		return toWidget ( wrap (MML) );
	}
	protected Subroutine.ParameterList P;


	/**
	 * build an MML description of a 2D space
	 * @param contour the description of the plot
	 * @return MML text describing the 2D area being displayed
	 * @throws Exception for any errors
	 */
	public String spaceDescriptionFor (ContourPlotProperties contour) throws Exception
	{
		float edge = contour.getEdgeSize ();
		DisplayGraphTypes.Point lowCorner = contour.getLowCorner ();
		double xLo = lowCorner.x, xHi = xLo + edge, yLo = lowCorner.y, yHi = yLo + edge;
		String P1 = spaceDescriptionFor (P.get (0), xLo, xHi);
		String P2 = spaceDescriptionFor (P.get (1), yLo, yHi);
		return P1 + space () + P2;
	}


	/**
	 * describe the end-points of a domain axis
	 * @param parameter name of the parameter described
	 * @param lo the low value for the specified parameter
	 * @param hi the high value for the specified parameter
	 * @return MML text with format nodes for description
	 * @throws Exception for any errors
	 */
	public String spaceDescriptionFor (String parameter, double lo, double hi) throws Exception
	{
		StringBuffer definition = new StringBuffer ();
		definition.append (parameter).append (" >=< ").append ("\" ");
		definition.append (lo).append (" , ").append (hi).append (" \"");
		String MML = this.toMML (TokenParser.parse (definition), P);
		return strip (MML);
	}

	/**
	 * remove the outer MATH node from the MML
	 * @param MML the text of the MML being modified
	 * @return the modified MML
	 */
	String strip (String MML) { return MML.substring ( 6, MML.length () - 7 ); }

	/**
	 * replace the MATH outer node
	 * @param MML the body of the document
	 * @return the completed document
	 */
	String wrap (String MML) { return "<math>" + MML + "</math>"; }


	/**
	 * MML for 20 units of space
	 * @return provide an MML spacing node
	 */
	public String space () { return MathMarkupNodes.space ("20"); }


}

