
package net.myorb.math.expressions.gui;

import java.awt.event.ActionListener;

public class Functionality
{

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

	public interface Data
	{
		public ActionListener getPiAction ();
		public ActionListener getSigmaAction ();
		public ActionListener getDyadicAction ();
		public ActionListener getAugmentAction ();
		public ActionListener getHypotAction ();
		public ActionListener getDotAction ();
	}

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

	public interface Charts
	{
		public ActionListener getListAction ();
		public ActionListener getScatterAction ();
		public ActionListener getAngularAction ();
		public ActionListener getRadialAction ();
	}

	public interface Fractals
	{
		public ActionListener getJuliaAction ();
		public ActionListener getMandelbrotAction ();
		public ActionListener getNewtonAction ();
		public ActionListener getListAction ();
	}

	public interface ActionManager
	{
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
