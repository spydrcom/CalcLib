
package net.myorb.math.expressions.charting.multidimensional;

import net.myorb.data.abstractions.SpaceConversion;
import net.myorb.data.abstractions.CommonDataStructures;

import net.myorb.math.expressions.charting.colormappings.TemperatureModelColorScheme;
import net.myorb.math.expressions.charting.Plot3D;

import net.myorb.math.expressions.ExpressionComponentSpaceManager;
import net.myorb.math.expressions.ValueManager;

import net.myorb.charting.DisplayGraphTypes;

/**
 * low-level data types providing descriptions of vector fields
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class VectorFieldPlotDescriptors <T> extends Plot3D <T>
{


	/**
	 * a local description of the field data
	 */
	public static class VectorFieldPoints
		extends DisplayGraphTypes.VectorField.Locations
	{ private static final long serialVersionUID = -955249300987318028L; }


	public VectorFieldPlotDescriptors ()
	{
		setColorSelectionFactory (TemperatureModelColorScheme.getColorSchemeFactory ());
	}


	/**
	 * treat list of values as vector
	 */
	public class Vector extends CommonDataStructures.Vector <T>
	{

		public Vector () {}

		/**
		 * construct a parameter list for a function call
		 * @param items the values of the parameter to be included
		 * @param converter a conversion object for the data type
		 */
		public Vector (Number [] items, SpaceConversion <T> converter) { super (items, converter); }

		/**
		 * use components of discrete value as vector components
		 * @param DV the generic wrapper for a discrete value
		 * @param manager a manager for the data type
		 */
		public Vector (ValueManager.DiscreteValue <T> DV, ExpressionComponentSpaceManager <T> manager)
		{
			T value = DV.getValue ();
			for (int i = 0; i < manager.getComponentCount (); i++)
			{ this.add (manager.convertFromDouble (manager.component (value, i))); }
		}

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

