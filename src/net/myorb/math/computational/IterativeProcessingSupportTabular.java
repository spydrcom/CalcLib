
package net.myorb.math.computational;

import net.myorb.gui.components.SimpleTableAdapter;
import net.myorb.gui.components.SimpleScreenIO;

import net.myorb.data.abstractions.Function;
//import net.myorb.math.Function;

/**
 * extended version using Table object for display
 * @param <T> data type to be displayed
 * @author Michael Druckman
 */
public class IterativeProcessingSupportTabular<T> extends IterativeProcessingSupport<T>
{

	public interface Exam
	{
		void processRow (Object[] items);
	}

	public IterativeProcessingSupportTabular
	(Function<T> function, String title)
	{
		super (function, null);
		if (title == null) return;
		tableDisplay = new TableFrame
		(table = new Table (), title);
	}
	protected TableFrame tableDisplay = null;

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.IterativeProcessingSupport#done()
	 */
	public void done () { if (table != null) tableDisplay.done (); }

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.IterativeProcessingSupport#showCurrentApproximation(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void showCurrentApproximation (String count, String approx, String change)
	{ if (table == null) return; table.extendBy (1); table.appendRow (new Object[]{count, change, approx, timeStamp ()}); }
	protected Table table = null;


	public static void enableDisplay () { TableFrame.enableDisplay(); }


	/**
	 * table component for display
	 */
	static class Table extends SimpleTableAdapter
	{

		public Table () { super (COLUMNS); }
		static final String[] COLUMNS = new String[]
		{"Count", "Change", "Result", "ms"};

		/* (non-Javadoc)
		 * @see net.myorb.gui.components.SimpleTableAdapter#doDoubleClick(int)
		 */
		public void doDoubleClick (int forRowNumber)
		{
			if (exam != null) exam.processRow (getRow (forRowNumber));
		}
		public void setExam (Exam exam) { this.exam = exam; }
		protected Exam exam = new CommonExam ();

	}
	public void setExam (Exam exam) { table.setExam (exam); }

	/**
	 * display selected row
	 */
	public static class CommonExam implements Exam
	{
		public void processRow (Object[] items)
		{
			for (Object col : items)
			{ System.out.print (col); System.out.print ("\t"); }
			System.out.println ();
		}
	}

	/**
	 * component structure for display
	 */
	static class TableFrame extends SimpleScreenIO
	{

		public TableFrame (SimpleTableAdapter adapter, String title)
		{
			Panel p = new Panel ();
			adapter.addTableToPanel (p);
			if (showing) frame = show (p, title + " (working...)", 600, 300);
			this.title = title;
		}

		public void done () { if (showing) frame.setTitle (title); }
		protected Frame frame; String title;

		public static void enableDisplay () { showing = true; }
		static boolean showing = false;

	}

}

