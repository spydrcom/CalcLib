
package net.myorb.math.expressions.gui;

import java.awt.Component;
import java.awt.event.ActionListener;

/**
 * Functionality features
 *  organized by actions provided in menu objects
 * @author Michael Druckman
 */
public class Functionality
{

	/**
	 * actions of Home menu
	 */
	public interface Home
	{
		public ActionListener getTabAction ();
		public ActionListener getSymAction ();
		public ActionListener getFuncAction ();
		public ActionListener getSaveAction ();
		public ActionListener getHelpAction ();
		public ActionListener getAllAction ();
		public ActionListener getRpnAction ();
	}

	/**
	 * actions of Data menu
	 */
	public interface Data
	{
		public ActionListener getPiAction ();
		public ActionListener getSigmaAction ();
		public ActionListener getDyadicAction ();
		public ActionListener getAugmentAction ();
		public ActionListener getHypotAction ();
		public ActionListener getDotAction ();
	}

	/**
	 * actions of Primes menu
	 */
	public interface Primes
	{
		public ActionListener getGcfAction ();
		public ActionListener getGapsAction ();
		public ActionListener getTableAction ();
		public ActionListener getPrimesAction ();
		public ActionListener getFactorsAction ();
		public ActionListener getSieveAction ();
		public ActionListener getLcmAction ();
	}

	/**
	 * actions of Polynomials menu
	 */
	public interface Polynomials
	{
		public ActionListener getConvAction ();
		public ActionListener getRootsAction ();
		public ActionListener getFormatAction ();
		public ActionListener getDerivativeAction ();
		public ActionListener getIntegralAction ();
		public ActionListener getDeriveAction ();
		public ActionListener getPolyAction ();
	}

	/**
	 * actions of Matrix menu
	 */
	public interface Matrix
	{
		public ActionListener getEigAction ();
		public ActionListener getTransposeAction ();
		public ActionListener getCharacteristicAction ();
		public ActionListener getComatrixAction ();
		public ActionListener getTraceAction ();
		public ActionListener getDetAction ();
		public ActionListener getAddAction ();
		public ActionListener getMulAction ();
		public ActionListener getInvAction ();
		public ActionListener getAdjAction ();
	}

	/**
	 * actions of SimEq menu
	 */
	public interface SimEq
	{
		public ActionListener getEvAction ();
		public ActionListener getSvdAction ();
		public ActionListener getSolveAction ();
		public ActionListener getGaussianAction ();
		public ActionListener getMatRptAction ();
		public ActionListener getVC31Action ();
		public ActionListener getQrAction ();
	}

	/**
	 * actions of Statistics menu
	 */
	public interface Statistics
	{
		public ActionListener getMinAction ();
		public ActionListener getMaxAction ();
		public ActionListener getMeanAction ();
		public ActionListener getMedianAction ();
		public ActionListener getStDevAction ();
		public ActionListener getVarAction ();
		public ActionListener getCovAction ();
	}

	/**
	 * actions of Regression menu
	 */
	public interface Regression
	{
		public ActionListener getVmAction ();
		public ActionListener getFftAction ();
		public ActionListener getLinearAction ();
		public ActionListener getLagrangeAction ();
		public ActionListener getNonlinearAction ();
		public ActionListener getChebyshevAction ();
		public ActionListener getHarmonicAction ();
		public ActionListener getSeriesAction ();
		public ActionListener getGaussAction ();
	}

	/**
	 * actions of Charts menu
	 */
	public interface Charts
	{
		public ActionListener getListAction ();
		public ActionListener getScatterAction ();
		public ActionListener getAngularAction ();
		public ActionListener getRadialAction ();
	}

	/**
	 * actions of Fractals menu
	 */
	public interface Fractals
	{
		public ActionListener getJuliaAction ();
		public ActionListener getMandelbrotAction ();
		public ActionListener getNewtonAction ();
		public ActionListener getListAction ();
	}

	/**
	 * management of actions of all menus
	 */
	public interface ActionManager
	{
		void setAppParent (Component appParent);

		Home getHomeActions ();
		SimEq getSimEqActions ();
		Matrix getMatrixActions ();
		Statistics getStatisticsActions ();
		Polynomials getPolynomialsActions ();
		Regression getRegressionActions ();
		Fractals getFractalActions ();
		Primes getPrimesActions ();
		Charts getChartActions ();
		Data getDataActions ();
	}

}
