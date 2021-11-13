
package net.myorb.math.expressions.gui;

import net.myorb.math.specialfunctions.AnalysisTool;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.gui.SequenceForm;
import net.myorb.math.expressions.DataConversions;

import net.myorb.data.abstractions.PrimitiveRangeDescription;
import net.myorb.data.abstractions.Function;

import net.myorb.gui.components.SimpleScreenIO.TextItemList;
import net.myorb.gui.components.SimpleTableAdapter;
import net.myorb.gui.components.SimpleScreenIO;

import net.myorb.gui.components.MenuItem;
import net.myorb.gui.components.Menu;

/**
 * tabulate segment areas of a function for AD Spline generation
 * @param <T> data type for calculations
 * @author Michael Druckman
 */
public class SplineTool<T> extends AnalysisTool
{


	/**
	 * @param function the description of the function being integrated
	 * @param spaceManager the space manager for the domain type
	 */
	public SplineTool (Function<T> function, ExpressionSpaceManager<T> spaceManager)
	{
		this.cvt =
			new DataConversions<T> (spaceManager);
		this.function = cvt.toRealFunction (function);
		this.tableDisplay = new TableFrame (table = new Table ());
		this.table.splineTool = this;
	}
	protected DataConversions<T> cvt = null;
	protected Function<Double> function = null;
	protected TableFrame tableDisplay = null;
	protected Table table = null;


	/**
	 * @param lo the lo interval value of the new segment
	 * @param hi the hi interval value of the new segment
	 */
	public void evaluateRange (String lo, String hi)
	{
		PrimitiveRangeDescription
			range = new PrimitiveRangeDescription (lo, hi, "1");
		performEvaluation (range, function, iterationCount, maxError);
	}
	protected int iterationCount = 15;
	protected double maxError = 1E-6;


	/*
	 * override result processing in AnalysisTool
	 */


	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.AnalysisTool#processCalculation(double)
	 */
	public void processCalculation (double calculatedIntegral)
	{ table.set (2, calculatedIntegral); }

	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.AnalysisTool#processEstimate(double, int)
	 */
	public void processEstimate (double estimate, int samples)
	{ table.set (3, samples); table.set (4, estimate); }

	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.AnalysisTool#processApproximation(double, double)
	 */
	public void processApproximation (double approximation, double difference)
	{ table.set (5, approximation); table.set (6, difference); }


	/**
	 * table component for display
	 */
	static class Table extends SimpleTableAdapter implements SequenceForm.Publisher
	{

		public Table () { super (COLUMNS, 1); }
		static final String[] COLUMNS = new String[]
		{"Lo", "Hi", "TSQ", "Samples", "Error Estimate", "Approximation", "Difference"};

		/* (non-Javadoc)
		 * @see net.myorb.gui.components.SimpleTableAdapter#doDoubleClick(int)
		 */
		public void doDoubleClick (int forRowNumber)
		{
			activeRow = forRowNumber;
			splineTool.evaluateRange (get (forRowNumber, 0).toString (), get (forRowNumber, 1).toString ());
		}
		@SuppressWarnings("rawtypes") SplineTool splineTool;
		int activeRow = 0;

		/**
		 * @param column the column number of the update cell
		 * @param value new value for the cell
		 */
		public void set (int column, Object value)
		{
			set (activeRow, column, value);
			refreshTable ();
		}

		/**
		 * @param lo the lo for the new segment
		 * @param hi the hi for the new segment
		 */
		public void addRange (String lo, String hi)
		{
			extendBy (1);
			Object[] row = new Object[]{"", "", "", "", "", "", ""};
			row[0] = lo; row[1] = hi; appendRow (row);
			activeRow = getCurrentSize () - 1;
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.gui.SequenceForm.Publisher#publish(net.myorb.gui.components.SimpleScreenIO.TextItemList)
		 */
		public void publish (TextItemList items)
		{
			addRange (items.get (0), items.get (1));
		}

		/**
		 * query for lo and hi of segment
		 */
		public void addRange ()
		{
			SequenceForm.requestRange (this);
		}

		/**
		 * output data to system console
		 */
		public void print ()
		{
			System.out.println (column (0));
			System.out.println (column (2));
		}
		public TextItemList column (int number)
		{
			TextItemList buffer = new TextItemList ();
			for (int row = 0; row < this.nextRow; row++)
			{ buffer.add (get (row, number).toString ()); }
			return buffer;
		}

		/* (non-Javadoc)
		 * @see net.myorb.gui.components.SimpleTableAdapter#getMenu()
		 */
		public Menu getMenu ()
		{
			Menu mc = new Menu ();
			mc.add
			(
				new MenuItem ("Add Range")
				{
					/* (non-Javadoc)
					 * @see net.myorb.gui.components.SimpleCallback.Adapter#executeAction()
					 */
					public void executeAction () throws Exception
					{
						addRange ();
					}
				}
			);
			mc.add
			(
				new MenuItem ("Print")
				{
					/* (non-Javadoc)
					 * @see net.myorb.gui.components.SimpleCallback.Adapter#executeAction()
					 */
					public void executeAction () throws Exception
					{
						print ();
					}
				}
			);
			return mc;
		}

	}


	/**
	 * component structure for display
	 */
	static class TableFrame extends SimpleScreenIO
	{
		public TableFrame (SimpleTableAdapter adapter)
		{
			Panel p = new Panel ();
			adapter.addTableToPanel (p);
			frame = show (p, "Spline Tool", 600, 300);
		}
		protected Frame frame;
	}


}

