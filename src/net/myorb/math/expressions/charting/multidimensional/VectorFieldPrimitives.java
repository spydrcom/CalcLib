
package net.myorb.math.expressions.charting.multidimensional;

import net.myorb.data.abstractions.CommonDataStructures;
import net.myorb.data.abstractions.SpaceConversion;

import net.myorb.math.expressions.charting.Plot3D;
import net.myorb.math.expressions.ValueManager;

import net.myorb.charting.DisplayGraphTypes;

/**
 * low-level data types providing descriptions of vector fields
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class VectorFieldPrimitives <T> extends Plot3D <T>
{


	/**
	 * a local description of the field data
	 */
	public static class VectorFieldPoints
		extends DisplayGraphTypes.VectorField.Locations
	{ private static final long serialVersionUID = -955249300987318028L; }


	/**
	 * treat list of values as vector
	 */
	public class Vector extends CommonDataStructures.Vector <T>
	{

		public Vector () {}

		/**
		 * construct a parameter list for a function call
		 * @param items the values of the parameter to be included
		 */
		public Vector (double [] items, SpaceConversion <T> converter) { super (items, converter); }

		/**
		 * use array of values as vector components
		 * @param DV the generic wrapper for an array of values
		 */
		public Vector (ValueManager.DimensionedValue <T> DV) { super ( DV.getValues () ); }

		/**
		 * extract row of gradient matrix and express as vector
		 * @param MV the generic wrapper for a gradient matrix
		 */
		public Vector (ValueManager.MatrixValue <T> MV)
		{
			super ( MV.getMatrix ().getRow (1).getElementsList () );
		}

		private static final long serialVersionUID = 8236433415557812064L;

	}


	/**
	 * get the compiled set of vector points in the field
	 * - this is the set of locations identified for the field path plot
	 * @return access to collected description of field
	 */
	public VectorFieldPoints getVectorPoints () { return this.vectorPoints; }
	public void setVectorPoints (VectorFieldPoints vectorPoints) { this.vectorPoints = vectorPoints; }
	protected VectorFieldPoints vectorPoints;


	/**
	 * allocate a new VectorField Location set
	 * @return a new structure that will hold a field description
	 */
	public static VectorFieldPoints pointsList () { return new VectorFieldPoints (); }


	private static final long serialVersionUID = -5736953963357328599L;

}

