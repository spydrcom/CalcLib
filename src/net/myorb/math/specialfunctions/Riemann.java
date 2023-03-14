
package net.myorb.math.specialfunctions;

import net.myorb.math.complexnumbers.ComplexValue;
import net.myorb.math.expressions.managers.ExpressionFactorizedFieldManager;
import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;
import net.myorb.math.primenumbers.sieves.SieveOfSundaram;
import net.myorb.data.abstractions.SpaceDescription;
import net.myorb.math.primenumbers.*;
import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

/**
 * implementation of the approximation of the Riemann Harmonic function
 * @author Michael Druckman
 */
public class Riemann
{

	/*
	 * EP(z,n,x) = (z + b*i) * log(x) / n
	 * I(x,n) = INTEGRAL [EP(-INFINITY,n)<=z<=EP(a,n,x)] ( exp(z) / z * <*> z )
	 * T(x) = Re { SIGMA [1<=n<=INFINITY] ( mu(n)/n * I(x,n) ) }
	 * mu is the Mobius function
	 * 
	 * Riemann connection with J:
	 * log (zeta (s))/s = INTEGRAL [0<=x<=INFINITY] ( J(x)*x^(-s-1) * <*> x )
	 * I(x) = INTEGRAL [x<=t<=INFINITY] ( 1 / ( t * (t^2-1) * log t ) * <*> t )
	 * J(x) = li(x) - SIGMA [zeta(p)=0] ( li(x^p) - log 2 + I(x) )
	 * pi(x) = SIGMA [1<=n<=INFINITY] ( mu(n)/n * J(x^(1/n)) )
	 */

	/**
	 * initialize a table of composites for use in factorizations
	 * @param tableSize the number of composites to compute
	 */
	public static void init (int tableSize)
	{
		Factorization.setImplementation
		(support = new FactorizationImplementation (tableSize));					// version of implementation that uses table scan
		support.initFactorizationsWithStats (new SieveOfSundaram (support));		// using non-default sieve (SieveOfSundaram)
	}
	public static ExpressionFactorizedFieldManager
			mgr = new ExpressionFactorizedFieldManager ();
	public static FactorizationImplementation support;

	static SpaceManager <Double> manager = new ExpressionFloatingFieldManager ();

	/**
	 * object that defines the harmonic function
	 */
	public static class Harmonic implements Function <Double>
	{
		
		public Harmonic (Double criticalRoot)
		{
			this (new ComplexValue <Double> (0.5, criticalRoot, manager));
		}

		public Harmonic (ComplexValue <Double> seed)
		{ this.a = seed.Re (); this.b = seed.Im (); constants (); }
		double a, b;

		public void constants ()
		{
			offsetAngle = Math.atan (b / a);
			abs = Math.sqrt (a*a + b*b);
		}
		double offsetAngle, abs;

		/* (non-Javadoc)
		 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
		 */
		public Double eval (Double x)
		{
			double logx = Math.log (x), adjustedAngle = b*logx - offsetAngle;
			return Math.pow (x, a) * Math.cos (adjustedAngle) / (abs * logx);
		}

		/* (non-Javadoc)
		 * @see net.myorb.data.abstractions.ManagedSpace#getSpaceDescription()
		 */
		public SpaceDescription <Double> getSpaceDescription () { return manager; }
		public SpaceManager <Double> getSpaceManager () { return manager; }

	}


	/**
	 * sum the values of the harmonics at parameter x
	 * @param x the parameter to the pi function
	 * @return the sum of harmonics at x
	 */
	public double sumHarmonics (double x)
	{
		double sum = 0.0;
		for (Harmonic harmonic : harmonics)
		{
			sum += harmonic.eval (x);
		}
		return sum;
	}
	Harmonic [] harmonics;


	/**
	 * compute approximation of pi function using the harmonic function
	 * @param x the parameter value setting the upper limit of the count of primes
	 * @return the computed estimate of count of primes below x
	 */
	public double T (double x)
	{
		return Harmonic1.eval (x) - sumHarmonics (x);
	}
	Harmonic Harmonic1 = new Harmonic (new ComplexValue <Double> (1.0, 0.0, manager));


	public Riemann ()
	{
		int n = 0;
		harmonics = new Harmonic [ZEROES.length];
		for (double imag : ZEROES)
		{
			harmonics [n++] = new Harmonic (imag);
		}
	}


	/**
	 * the zeroes of the zeta function in range 0..1000
	 */
	public static final double[] ZEROES = new double[] { 14.134725142, 21.022039639, 25.010857580, 30.424876126,
			32.935061588, 37.586178159, 40.918719012, 43.327073281, 48.005150881, 49.773832478, 52.970321478,
			56.446247697, 59.347044003, 60.831778525, 65.112544048, 67.079810529, 69.546401711, 72.067157674,
			75.704690699, 77.144840069, 79.337375020, 82.910380854, 84.735492981, 87.425274613, 88.809111208,
			92.491899271, 94.651344041, 95.870634228, 98.831194218, 101.317851006, 103.725538040, 105.446623052,
			107.168611184, 111.029535543, 111.874659177, 114.320220915, 116.226680321, 118.790782866, 121.370125002,
			122.946829294, 124.256818554, 127.516683880, 129.578704200, 131.087688531, 133.497737203, 134.756509753,
			138.116042055, 139.736208952, 141.123707404, 143.111845808, 146.000982487, 147.422765343, 150.053520421,
			150.925257612, 153.024693811, 156.112909294, 157.597591818, 158.849988171, 161.188964138, 163.030709687,
			165.537069188, 167.184439978, 169.094515416, 169.911976479, 173.411536520, 174.754191523, 176.441434298,
			178.377407776, 179.916484020, 182.207078484, 184.874467848, 185.598783678, 187.228922584, 189.416158656,
			192.026656361, 193.079726604, 195.265396680, 196.876481841, 198.015309676, 201.264751944, 202.493594514,
			204.189671803, 205.394697202, 207.906258888, 209.576509717, 211.690862595, 213.347919360, 214.547044783,
			216.169538508, 219.067596349, 220.714918839, 221.430705555, 224.007000255, 224.983324670, 227.421444280,
			229.337413306, 231.250188700, 231.987235253, 233.693404179, 236.524229666, 237.769820481, 239.555477573,
			241.049157796, 242.823271934, 244.070898497, 247.136990075, 248.101990060, 249.573689645, 251.014947795,
			253.069986748, 255.306256455, 256.380713694, 258.610439492, 259.874406990, 260.805084505, 263.573893905,
			265.557851839, 266.614973782, 267.921915083, 269.970449024, 271.494055642, 273.459609188, 275.587492649,
			276.452049503, 278.250743530, 279.229250928, 282.465114765, 283.211185733, 284.835963981, 286.667445363,
			287.911920501, 289.579854929, 291.846291329, 293.558434139, 294.965369619, 295.573254879, 297.979277062,
			299.840326054, 301.649325462, 302.696749590, 304.864371341, 305.728912602, 307.219496128, 310.109463147,
			311.165141530, 312.427801181, 313.985285731, 315.475616089, 317.734805942, 318.853104256, 321.160134309,
			322.144558672, 323.466969558, 324.862866052, 327.443901262, 329.033071680, 329.953239728, 331.474467583,
			333.645378525, 334.211354833, 336.841850428, 338.339992851, 339.858216725, 341.042261111, 342.054877510,
			344.661702940, 346.347870566, 347.272677584, 349.316260871, 350.408419349, 351.878649025, 353.488900489,
			356.017574977, 357.151302252, 357.952685102, 359.743754953, 361.289361696, 363.331330579, 364.736024114,
			366.212710288, 367.993575482, 368.968438096, 370.050919212, 373.061928372, 373.864873911, 375.825912767,
			376.324092231, 378.436680250, 379.872975347, 381.484468617, 383.443529450, 384.956116815, 385.861300846,
			387.222890222, 388.846128354, 391.456083564, 392.245083340, 393.427743844, 395.582870011, 396.381854223,
			397.918736210, 399.985119876, 401.839228601, 402.861917764, 404.236441800, 405.134387460, 407.581460387,
			408.947245502, 410.513869193, 411.972267804, 413.262736070, 415.018809755, 415.455214996, 418.387705790,
			419.861364818, 420.643827625, 422.076710059, 423.716579627, 425.069882494, 427.208825084, 428.127914077,
			430.328745431, 431.301306931, 432.138641735, 433.889218481, 436.161006433, 437.581698168, 438.621738656,
			439.918442214, 441.683199201, 442.904546303, 444.319336278, 446.860622696, 447.441704194, 449.148545685,
			450.126945780, 451.403308445, 453.986737807, 454.974683769, 456.328426689, 457.903893064, 459.513415281,
			460.087944422, 462.065367275, 464.057286911, 465.671539211, 466.570286931, 467.439046210, 469.536004559,
			470.773655478, 472.799174662, 473.835232345, 475.600339369, 476.769015237, 478.075263767, 478.942181535,
			481.830339376, 482.834782791, 483.851427212, 485.539148129, 486.528718262, 488.380567090, 489.661761578,
			491.398821594, 493.314441582, 493.957997805, 495.358828822, 496.429696216, 498.580782430, 500.309084942,
			501.604446965, 502.276270327, 504.499773313, 505.415231742, 506.464152710, 508.800700336, 510.264227944,
			511.562289700, 512.623144531, 513.668985555, 515.435057167, 517.589668572, 518.234223148, 520.106310412,
			521.525193449, 522.456696178, 523.960530892, 525.077385687, 527.903641601, 528.406213852, 529.806226319,
			530.866917884, 532.688183028, 533.779630754, 535.664314076, 537.069759083, 538.428526176, 540.213166376,
			540.631390247, 541.847437121, 544.323890101, 545.636833249, 547.010912058, 547.931613364, 549.497567563,
			550.970010039, 552.049572201, 553.764972119, 555.792020562, 556.899476407, 557.564659172, 559.316237029,
			560.240807497, 562.559207616, 564.160879111, 564.506055938, 566.698787683, 567.731757901, 568.923955180,
			570.051114782, 572.419984132, 573.614610527, 575.093886014, 575.807247141, 577.039003472, 579.098834672,
			580.136959362, 581.946576266, 583.236088219, 584.561705903, 585.984563205, 586.742771891, 588.139663266,
			590.660397517, 591.725858065, 592.571358300, 593.974714682, 595.728153697, 596.362768328, 598.493077346,
			599.545640364, 601.602136736, 602.579167886, 603.625618904, 604.616218494, 606.383460422, 608.413217311,
			609.389575155, 610.839162938, 611.774209621, 613.599778676, 614.646237872, 615.538563369, 618.112831366,
			619.184482598, 620.272893672, 621.709294528, 622.375002740, 624.269900018, 626.019283428, 627.268396851,
			628.325862359, 630.473887438, 630.805780927, 632.225141167, 633.546858252, 635.523800311, 637.397193160,
			637.925513981, 638.927938267, 640.694794669, 641.945499666, 643.278883781, 644.990578230, 646.348191596,
			647.761753004, 648.786400889, 650.197519345, 650.668683891, 653.649571605, 654.301920586, 655.709463022,
			656.964084599, 658.175614419, 659.663845973, 660.716732595, 662.296586431, 664.244604652, 665.342763096,
			666.515147704, 667.148494895, 668.975848820, 670.323585206, 672.458183584, 673.043578286, 674.355897810,
			676.139674364, 677.230180669, 677.800444746, 679.742197883, 681.894991533, 682.602735020, 684.013549814,
			684.972629862, 686.163223588, 687.961543185, 689.368941362, 690.474735032, 692.451684416, 693.176970061,
			694.533908700, 695.726335921, 696.626069900, 699.132095476, 700.296739132, 701.301742955, 702.227343146,
			704.033839296, 705.125813955, 706.184654800, 708.269070885, 709.229588570, 711.130274180, 711.900289914,
			712.749383470, 714.082771821, 716.112396454, 717.482569703, 718.742786545, 719.697100988, 721.351162219,
			722.277504976, 723.845821045, 724.562613890, 727.056403230, 728.405481589, 728.758749796, 730.416482123,
			731.417354919, 732.818052714, 734.789643252, 735.765459209, 737.052928912, 738.580421171, 739.909523674,
			740.573807447, 741.757335573, 743.895013142, 745.344989551, 746.499305899, 747.674563624, 748.242754465,
			750.655950362, 750.966381067, 752.887621567, 754.322370472, 755.839308976, 756.768248440, 758.101729246,
			758.900238225, 760.282366984, 762.700033250, 763.593066173, 764.307522724, 766.087540100, 767.218472156,
			768.281461807, 769.693407253, 771.070839314, 772.961617566, 774.117744628, 775.047847097, 775.999711963,
			777.299748530, 779.157076949, 780.348925004, 782.137664391, 782.597943946, 784.288822612, 785.739089701,
			786.461147451, 787.468463816, 790.059092364, 790.831620468, 792.427707609, 792.888652563, 794.483791870,
			795.606596156, 797.263470038, 798.707570166, 799.654336211, 801.604246463, 802.541984878, 803.243096204,
			804.762239113, 805.861635667, 808.151814936, 809.197783363, 810.081804886, 811.184358847, 812.771108389,
			814.045913608, 814.870539626, 816.727737714, 818.380668866, 819.204642171, 820.721898444, 821.713454133,
			822.197757493, 824.526293872, 826.039287377, 826.905810954, 828.340174300, 829.437010968, 830.895884053,
			831.799777659, 833.003640909, 834.651915148, 836.693576188, 837.347335060, 838.249021993, 839.465394810,
			841.036389829, 842.041354207, 844.166196607, 844.805993976, 846.194769928, 847.971717640, 848.489281181,
			849.862274349, 850.645448466, 853.163112583, 854.095511720, 855.286710244, 856.484117491, 857.310740603,
			858.904026466, 860.410670896, 861.171098213, 863.189719772, 864.340823930, 865.594664327, 866.423739904,
			867.693122612, 868.670494229, 870.846902326, 872.188750822, 873.098978971, 873.908389235, 875.985285109,
			876.600825833, 877.654698341, 879.380951970, 880.834648848, 882.386696627, 883.430331839, 884.198743115,
			885.272304480, 886.852801963, 888.475566674, 889.735294294, 890.813132113, 892.386433260, 893.119117567,
			894.886292321, 895.397919675, 896.632251556, 899.221522668, 899.858884608, 900.849739861, 902.243207587,
			903.099674443, 904.702902722, 905.829940758, 907.656729469, 908.333543645, 910.186334057, 911.234951486,
			912.331045600, 912.823999247, 914.730096958, 916.355000809, 917.825377570, 918.836535244, 919.448344440,
			921.156395507, 922.500629307, 923.285719802, 924.773483933, 926.551552785, 927.850858986, 928.663659329,
			929.874092851, 931.009211337, 931.852740746, 934.385306837, 934.995424864, 936.228649379, 937.532925712,
			939.024300899, 939.660940615, 941.156999642, 942.052341643, 944.188035810, 945.333562503, 946.765842205,
			947.079183096, 948.346646255, 950.151612685, 951.033248734, 952.727988620, 954.129719270, 954.829308938,
			956.675479343, 957.510052596, 958.414593390, 959.459168807, 961.669572474, 963.182086671, 963.567040192,
			965.055579624, 966.110754818, 967.371153766, 968.636301906, 970.125610557, 971.071491486, 973.185361294,
			973.873078993, 974.774635066, 976.178502421, 976.917202117, 978.766671535, 980.578000640, 981.288615302,
			982.396485169, 983.575076006, 985.186928656, 986.130515110, 986.756008408, 988.992622371, 990.223917804,
			991.374294148, 992.728696337, 993.214580957, 994.404590571, 996.205336164, 997.511934752, 998.827547137,
			999.791571557 };

	public static double pi (int value)
	{
		return support.piFunction (value).doubleValue ();
	}

	public static double piMinus (Double value, Double approx)
	{
		return Math.abs (pi (value.intValue ()) - approx);
	}

	public static void main (String[] args)
	{
		init (5_000_000);
		Riemann R = new Riemann ();
		for (Double x = 100.0; x < 10000.0; x+=100.0)
		{
			System.out.println
			(
				x + " =>" +
				" \t pi:" + pi (x.intValue ()) +
				" \t T:" + piMinus (x, R.T (x)) +
				" \t x/Lx:" + piMinus (x, x / Math.log (x)) +
				" \t li: " + piMinus (x, ExponentialIntegral.li (x))
			);
		}
	}
	// (2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97)
	//  1  2  3  4   5   6   7   8   9  10  11  12  13  14  15  16  17  18  19  20  21  22  23  24  25

}
