public class BellC extends net.myorb.math.expressions.symbols.SplineInRealDomain
{
	public void initialize ()
	{
		setName ("BellC");
		setHiConstraint (3.000000000000001);
		setSegmentLoConstraints (-2.7755575615628914E-17, 1.0, 2.0);
		addSegmentChebyshevPolynomialCalculus (0.6311258156400305, 0.026311693997248975, -0.33504438248014146, 0.016596345108502893, 0.02787060344711326, 0.006032491027636351, -0.005959198432714766, 9.460728639273452E-4);
		addSegmentChebyshevPolynomialCalculus (3.0434241489425418, -4.233526014342081, 2.5854633288882214, -1.5183741120062235, 0.6312356504989729, -0.16362563432067473, 0.025374612556174337, -0.002171924087607744, 7.938504228020938E-5);
		addSegmentChebyshevPolynomialCalculus (1.7060273405622775, -1.067935102074654, -0.5761752364404433, 0.44566703357612436, -0.12161123597820672, 0.017057589733339477, -0.0012364216593787687, 3.6868376203398654E-5);
	}
}
