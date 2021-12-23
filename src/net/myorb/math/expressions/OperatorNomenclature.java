
package net.myorb.math.expressions;

import java.util.HashMap;

/**
 * the names of recognized operators
 * @author Michael Druckman
 */
public class OperatorNomenclature
{


	/*
	 * keywords that invoke special processing
	 */

	public static
		final String
	ASSERT_KEYWORD = "ASSERT",					// ASSERT TOL >|| error	make an assertion
	ASSERTIONS_KEYWORD = "ASSERTIONS",			// ASSERTIONS FAILED	show assertions verified or failed				*TBD VERIFIED|FAILED|EXPIRED
	ASSIGNMENT_KEYWORD = "LET",					// let A = 1			identify variable assignment (@ is shortcut)
	DEFINITION_KEYWORD = "DEFINE",				// define f(x)=x-1		user function definition (arbitrary parameter count)
	RECOGNIZE_KEYWORD = "RECOGNIZE",			// RECOGNIZE script		add symbols to parent symbol table as background available
	CALCULATE_KEYWORD = "CALCULATE",			// CALCULATE 1+2+...	calculate value from expression and display to console
	ITERATE_KEYWORD = "ITERATE",				// ITERATE file			iterate over a script until a termination assertion is met
	BACKGROUND_KEYWORD = "BACKGROUND",			// BACKGROUND file		run a script as a background task with detached display
	PRETTYPRINT_KEYWORD = "PRETTYPRINT",		// PRETTYPRINT symbol	format and display (pretty print) the value of a symbol
	RENDER_KEYWORD = "RENDER",					// RENDER equation		format and display (pretty print) an equation using MathML
	RENDER_FUNCTION_KEYWORD = "RENDERF",		// RENDERF function		format and display (pretty print) the function using MathML
	RENDER_DIFFEQ_KEYWORD = "RENDERD",			// RENDERD function		format and display (pretty print) the function as DE using MathML
	POLYPRINT_KEYWORD = "POLYPRINT",			// POLYPRINT symbol		format and display (pretty print) the represented polynomial
	SCRIPTPRINT_KEYWORD = "SCRIPTPRINT",		// SCRIPTPRINT file		display the contents of a script file to the console
	CALC_ABBREVIATION_KEYWORD = "CALC",			// CALC 1+2+...			a simple short form abbreviation of CALCULATE command
	PRIMETABLE_KEYWORD = "PRIMETABLE",			// PRIMETABLE 1000		a tabulation of prime factorizations starting at spacified
	PRIMEGAPS_KEYWORD = "PRIMEGAPS",			// PRIMEGAPS 1000		a tabulation of prime gaps starting at spacified
	SHOW_SYMBOLS_KEYWORD = "SHOW",				// SHOW symbols			dump the symbol table (symbols, functions, all)
	DESCRIBE_KEYWORD = "DESCRIBE",				// DESCRIBE symbol text	add text as a description of a function
	SAVE_INPUT_KEYWORD = "SAVE",				// SAVE file-path		save a text file for source as input alternative
	READ_INPUT_KEYWORD = "READ",				// READ file-path		read a text file source as input alternative
	RUNSIEVE_KEYWORD = "RUNSIEVE",				// RUNSIEVE 100			populate the prime factorization table
	DUMPING_KEYWORD = "DUMPING",				// let DUMPING=1		trace enable flag (show value stack changes)
	FAMILY_KEYWORD = "FAMILY",					// FAMILY name count	load polynomial power function of named family
	LIBRARY_KEYWORD = "LIBRARY",				// LIBRARY L java.Math	import a class as a library of functions
	CONFIGURE_KEYWORD = "CONFIGURE",			// CONFIGURE L P1 ...	post symbols to a library of functions
	INSTANCE_KEYWORD = "INSTANCE",				// INSTANCE NAME LIB	post symbol defined by a parameter library
	IMPORT_KEYWORD = "IMPORT",					// IMPORT m  file		import data from file into specified matrix
	EXPORT_KEYWORD = "EXPORT",					// EXPORT m  file		export data to file from specified matrix
	SETMODE_KEYWORD = "SETMODE",				// SETMODE				show precision and value display mode options
	SETCONTOUR_KEYWORD = "SETCONTOUR",			// SETCONTOUR			select parameters of colors used in contour plots
	SETDOMAIN_KEYWORD = "SETDOMAIN",			// SETDOMAIN f,lo,hi	establish function domain constraints [lo,hi]
	STDDOMAIN_KEYWORD = "STDDOMAIN",			// STDDOMAIN function	perform domain change to set standard [-1,1] domain
	DCT_KEYWORD = "DCT",						// DCT function			apply discreet cosine transform to specified function
	EXPRESS_KEYWORD = "EXPRESS",				// EXPRESS function		generate Expression Tree for a user defined function
	SAVEJSON_KEYWORD = "SAVEJSON",				// SAVEJSON function	save Expression Tree to JSON file for later restore using load
	LOADJSON_KEYWORD = "LOADJSON",				// EXPRESS function		load Expression Tree for a user defined function from JSON file
	ENCODE_KEYWORD = "ENCODE",					// ENCODE function		generate Java code for a segmented function
	MAXMINOF_KEYWORD = "MAXMINOF",				// MAXMINOF function	find min/max of function near specified value
	ROOTOF_KEYWORD = "ROOTOF",					// ROOTOF function		find root of function near specified value 
	SELECT_KEYWORD = "SELECT",					// SELECT figure		choose the render display to write to
	SPLINE_KEYWORD = "SPLINE",					// SPLINE function		open anti derivative spline tool GUI
	PREPARE_KEYWORD = "PREPARE",				// PREPARE function	as	alias function and derivatives for DE
	PREPPOLY_KEYWORD = "PREPPOLY",				// PREPPOLY function as	alias polynomial and derivatives for DE
	OPTIMIZE_KEYWORD = "OPTIMIZE",				// OPTIMIZE function	optimize polynomial use by embedding coefficients
	DIFEQ_KEYWORD = "DIFEQ",					// DIFEQ function		identify differential equation and describe
	TDES_KEYWORD = "TDES",						// TDES function		Test differential equation solution
	RADIX_KEYWORD = "RADIX",					// RADIX 16 value		display value with specified radix
	REQUEST_KEYWORD = "REQUEST",				// REQUEST x			request value for symbol and verify result
	VERIFY_KEYWORD = "VERIFY",					// VERIFY x				lookup symbol and verify present
	HELP_KEYWORD = "HELP",						// HELP					list of operator descriptions
	DOCS_KEYWORD = "DOCS",						// DOCS					show JavaDocs HTML
	RPN_KEYWORD = "RPN"							// RPN					RPN calculator
		;


	/*
	 * charts / plots / graphs
	 */

	public static
		final String
	FFT_KEYWORD = "FFT",						// FFT(X,Y)				compute and plot a fast Fourier transform
	PLOTF_KEYWORD = "PLOTF",					// PLOTF f [...]		plot a specified range of a user defined function
	CHART_KEYWORD = "CHART",					// CHART kind  a b c	bar and pie charts with a kind specific to implementation
	SPLOT_KEYWORD = "SPLOT",					// SPLOT [...](f(x))	plot an array of structured values over a specified domain
	PLOTC_KEYWORD = "PLOTC",					// PLOTC f x y 			plot a specified 2D domain with contours of a 3D user defined function
	PLOTM_KEYWORD = "PLOTM",					// PLOTM matrix			tabular plot with data taken from matrix, 2 column or 3 column giving 2D/3D
	PLOTT_KEYWORD = "PLOTT",					// PLOTT filepath		tabular plot with data taken from file, 2/3 column giving 2D/3D
	PLOT3D_KEYWORD = "PLOT3D",					// PLOT3D f x y 		plot a specified 2D domain of a 3D user defined function
	PLOTRI_KEYWORD = "PLOTRI",					// PLOTRI f [...]		plot a specified range of a complex user defined function
	ENTITLED_KEYWORD = "ENTITLED",				// ENTITLED text		change the title of the last frame shown to improve context
	GRAPH_KEYWORD = "GRAPH",					// GRAPH [...](sin x)	plot an array of transformed over a specified domain
	MANDELBROT_KEYWORD = "MANDELBROT",			// MANDELBROT a			display the Mandelbrot set at specified coordinates
	POLAR_RADIAL_KEYWORD = "POLARRADIAL",		// POLARRADIAL f		plot a complex mapping using gradients of distance
	POLAR_ANGULAR_KEYWORD = "POLARANGULAR",		// POLARANGULAR f		plot a complex mapping using gradients of angle
	SIDEBYSIDE_KEYWORD = "SIDEBYSIDE",			// SIDEBYSIDE "title"	plots are shown in pairs for comparisons
	SCATTER_KEYWORD = "SCATTER"					// SCATTER X, Y			plot a scatter graph of X/Y points
		;


	/*
	 * property list management
	 */

	public static
		final String
	PCLR_KEYWORD = "PCLR",						// PCLR entry			clear all properties of an entry
	PSET_KEYWORD = "PSET",						// PSET entry prop val	set the value of a property in the entry
	PDEL_KEYWORD = "PDEL",						// PDEL entry prop		delete one property from an entry
	PLOAD_KEYWORD = "PLOAD",					// PLOAD entry file		load properties from JSON file
	PSAVE_KEYWORD = "PSAVE"						// PSAVE entry file		save properties to JSON file
		;


	/*
	 * functions & operators for array processing
	 */

	public static
		final String
	PI_OPERATOR = "PI",							// PI(1,2,3)			mathematical (product of array values)
	INTERVAL_FUNCTION = "INTERVAL",				// INTERVAL(A,lo,hi)	select sub-list of elements for interval lo-hi
	LENGTH_FUNCTION = "LENGTH",					// LENGTH(A)			conventional (length of an array) as unary operation
	NEGATE_OPERATOR = "NEGATE",					// NEGATE(value)		mathematical (additive inverse) as unary operation
	SUMMATION_OPERATOR = "SUMMATION",			// SUMMATION(1,2,3)		mathematical (sum of array values) as unary operation
	SIGMA_OPERATOR = "SIGMA",					// SIGMA(1,2,3)			mathematical (sum of array values) as unary operation with sigma notation
	INTEGRAL_OPERATOR = "INTEGRAL",				// INTEGRAL(1,2,3)		mathematical (sum of array values) as unary operation with integral notation
	INTERPOLATE_FUNCTION = "INTERPOLATE",		// INTERPOLATE(A1)		build Lagrange polynomial interpolation of function values found in an array
	CHEBINTERP_FUNCTION = "CHEBINTERP",			// CHEBINTERP(A1)		build Chebyshev polynomial interpolation of function values found in an array
	CLENQUAD_FUNCTION = "CLENQUAD",				// CLENQUAD(A1,x)		compute Clenshaw quadrature (numerical integration) on Chebyshev Polynomial
	ARRAYINT_FUNCTION = "ARRAYINT",				// ARRAYINT(A1)			build integral approximation of function values found in an array
	ARRAYDER_FUNCTION = "ARRAYDER",				// ARRAYDER(A1)			build derivative approximation of function values found in an array
	GAUSSQUAD_FUNCTION = "GAUSSQUAD",			// GAUSSQUAD (x,y)		produce a Lagrange interpolation and integrate the polynomial
	CHEBYSHEV_FUNCTION = "CHEBYSHEV",			// CHEBYSHEV (x,y)		produce a complete Chebyshev interpolation using Vandermonde matrix
	FITPOLY_FUNCTION = "FITPOLY",				// FITPOLY(X,Y)			fit data points to a polynomial power series using Vandermonde
	FITHARMONIC_FUNCTION = "FITHARMONIC",		// FITHARMONIC(x,y)		fit data points to a harmonic (Fourier) series
	LAGRANGE_FUNCTION = "LAGRANGE",				// LAGRANGE (x,y)		fit data points to a polynomial using Lagrange
	PEARSON_FUNCTION = "PEARSON",				// PEARSON(X,Y)			compute Pearson coefficient of linear correlation
	FITLINE_FUNCTION = "FITLINE",				// FITLINE(X,Y)			fit data points to a line using least squares
	FITEXP_FUNCTION = "FITEXP",					// FITEXP(X,Y)			fit data poonts to an exponential equation
	PRIMES_FUNCTION = "PRIMES",					// PRIMES(100)			build array of primes less than parameter
	FACTORS_FUNCTION = "FACTORS",				// FACTORS (123456)		compute the prime factorization of integer
	HYPOT_FUNCTION = "HYPOT",					// HYPOT(A1)			hypotenuse computation 2\(a0^2+a1^2+...)
	MAX_FUNCTION = "MAX",						// MAX(A1)				maximum value found in an array
	MIN_FUNCTION = "MIN",						// MIN(A1)				minimum value found in an array
	DOT_FUNCTION = "DOT"						// DOT(A1,A2)			mathematical (array dot product)
		;


	/*
	 * keywords for array processing specific to polynomial coefficient arrays
	 */

	public static
		final String
	CONV_FUNCTION = "CONV",						// CONV (A1,A2)			multiplication of polynomials
	DECONV_FUNCTION = "DECONV",					// DECONV (A1, A2)		division of polynomials from coefficients
	POLYINT_FUNCTION = "POLYINT",				// POLYINT (1, 2, 3)	integral of polynomial from coefficients
	POLYDER_FUNCTION = "POLYDER",				// POLYDER (1, 2, 3)	derivative of polynomial from coefficients
	CHEBDER_FUNCTION = "CHEBDER",				// CHEBDER (1, 2, 3)	derivative of Chebyshev T polynomial from coefficients
	POLYHG_FUNCTION = "POLYHG",					// POLYHG (a1, a2)		build coefficients for hyper geometric polynomial
	POLYNOMIAL_KEYWORD = "POLYNOMIAL",			// POLYNOMIAL (1, 2)	evaluate a polynomial given coefficient array
	POLY_ABBREVIATION_KEYWORD = "POLY",			// POLY (1, 2, 3)		an abbreviation for POLYNOMIAL (short form)
	EVALSPLINE_FUNCTION = "EVALSPLINE",			// EVALSPLINE (M, b, x)	evaluate a Chebyshev spline function at X
	DERIVE_KEYWORD = "DERIVE",					// DERIVE (1, 2, 3)		show table of derivatives of polynomial
	APPEND_FUNCTION = "APPEND",					// APPEND (A1,A2)		append arrays in sequence presented
	DYADIC_FUNCTION = "DYADIC",					// DYADIC (A1,A2)		compute dyadic product of arrays
	ROOTS_KEYWORD = "ROOTS",					// ROOTS (1, 2, 3)		compute roots of polynomials
	PIVOT_FUNCTION = "PIVOT",					// PIVOT (A, order)		reorder a vector to pattern
	LUXB_FUNCTION = "LUXB",						// LUXB (L,U,b)			solve LUx=b general case
	VC31_FUNCTION = "VC31"						// VC31 (b)				solve LUx=b using VC31LU
		;


	/*
	 * keywords for matrix processing
	 */

	public static
		final String
	MATRIX_FUNCTION = "MATRIX",					// MATRIX (A,rows,cols)	convert array to matrix with specified dimensions
	IDENTITY_FUNCTION = "IDENTITY",				// IDENTITY (3)			build a square identity matrix with given size
	COLUMN_FUNCTION = "COL",					// COL (m,i)			get column vector from matrix at index specified
	ROW_FUNCTION = "ROW",						// ROW (m,i)			get row vector from matrix at index specified
	MATADD_FUNCTION = "MATADD",					// MATADD (M1, M2)		compute the sum of two matrices (comutitive operation)
	MATMUL_FUNCTION = "MATMUL",					// MATMUL (M1, M2)		compute the product of two matrices (non-comutitive operation)
	SOLVE_FUNCTION = "SOLVE",					// SOLVE (M,vec)		solve a system of linear equations with column substitution
	GAUSSIAN_FUNCTION = "GAUSSIAN",				// GAUSSIAN (M,vec)		perform Gaussian elimination on an augmented matrix
	AUGMENTED_FUNCTION = "AUGMENTED",			// AUGMENTED (M,vec)	build augmented matrix adding column vector
	TRANSPOSE_FUNCTION = "TRANSPOSE",			// TRANSPOSE (M)		build transposed matrix based on M as source
	MINOR_FUNCTION = "MINOR",					// MINOR (M,row,col)	get minor matrix removing row and col from M
	ADJ_FUNCTION = "ADJ",						// ADJ (M)				adjugate is the transpose of the comatrix
	EIG_FUNCTION = "EIG",						// EIG (M)				compute eigenvalues for specified matrix
	INV_FUNCTION = "INV",						// INV (M)				build inverse matrix based on M as source
	CHARACTERISTIC_FUNCTION = "CHARACTERISTIC",	// CHARACTERISTIC (M)	compute characteristic polynomial for matrix
	COMPANION_FUNCTION = "COMPANION",			// COMPANION (poly)		compute companion matrix for polynomial
	COFACTOR_FUNCTION = "COFACTOR",				// COFACTOR (m,r,c)		compute the determinant of a minor matrix
	COMATRIX_FUNCTION = "COMATRIX",				// COMATRIX (m)			compute the comatrix (matrix of cofactors)
	MATREP_FUNCTION = "MATREP",					// MATREP (m)			produce a decomposition report for the matrix
	GENKNOT_FUNCTION = "GENKNOT",				// GENKNOT (array)		generate a knot for a reflection across the y-axis
	VANCHE_FUNCTION = "VANCHE",					// VANCHE (array)		produce a Vandermonde matrix for a Chebyshev interpolation
	SVD_FUNCTION = "SVD",						// SVD (m)				compute the singular value decomposition of the specified matrix
	EVD_FUNCTION = "EVD",						// EVD (m)				compute the eigenvalue decomposition of the specified matrix
	QRD_FUNCTION = "QRD",						// QRD (m)				compute the QR decomposition of the specified matrix
	TRACE_FUNCTION = "TR",						// TR (m)				compute the trace of the specified matrix
	TRIL_FUNCTION = "TRIL",						// TRIL (m)				get lower triangle of specified matrix
	TRIU_FUNCTION = "TRIU",						// TRIU (m)				get upper triangle of specified matrix
	DET_FUNCTION = "DET"						// DET (m)				compute determinant of matrix
		;


	/*
	 * keywords for statistical processing
	 */

	public static
		final String
	MEAN_FUNCTION = "MEAN",						// MEAN(1,2,3)			average of an array of values
	VARIANCE_FUNCTION = "VAR",					// VAR(1,2,3)			variance of an array of values
	COV_FUNCTION = "COV",						// COV(1,2,3)			covariance of an array of values
	MEDIAN_FUNCTION = "MEDIAN",					// MEDIAN(1,2,3)		median value of an array of values
	MODE_FUNCTION = "MODE",						// MODE(1,2,3)			mode value of an array of values
	STDEV_FUNCTION = "STDEV"					// STDEV(1,2,3)			standard deviation of an array of values
		;


	/*
	 * functions for combinatoric processing
	 */

	public static
		final String
	ZETA_FUNCTION = "zeta",						// zeta(x)				Zeta function
	GAMMA_OPERATOR = "GAMMA",					// |^(t)				gamma function expressed as a unary operator
	HARMONIC_FUNCTION = "HARMONIC",				// HARMONIC(x)			Harmonic numbr function number
	BERNOULLI_FUNCTION = "BERNOULLI",			// BERNOULLI(m,n)		Bernoulli number
	LOGGAMMA_FUNCTION = "LOGGAMMA"				// LOGGAMMA(z)			Ln (|^(z))
		;


	/*
	 * characters treated as special case delimiters
	 */

	public static
		final String
	ASSIGNMENT_DELIMITER = "=",					// let A = 1		identify variable assignment
	GROUP_CONTINUATION_DELIMITER = ",",			// f(a,b,c,d,e)		separation of parameters
	END_OF_STATEMENT_DELIMITER = ";",			// @A=1; @B=2		separation of statements
	START_OF_GROUP_DELIMITER = "(",				// (a+b)*(c+d)		increased precedence
	START_OF_ARRAY_DELIMITER = "[",				// [0<=i<=10]()		limits of domain
	END_OF_ARRAY_DELIMITER = "]",
	END_OF_GROUP_DELIMITER = ")"
		;


	/*
	 * characters treated as operators
	 */

	public static
		final String
	ADDITION_OPERATOR			= "+",			// addition			a + b
	SUBTRACTION_OPERATOR		= "-",			// subtraction		a - b
	MULTIPLICATION_OPERATOR		= "*",			// multiplication	a * b
	DIVISION_OPERATOR			= "/",			// division			a / b (displayed as a over b)
	FRACTION_OPERATOR			= "#/#",		// fraction			a #/# b (displayed as a / b)

	POW_OPERATOR				= "^",			// x^n				integer exponent pow (x, n)
	REMAINDER_OPERATOR			= "%",			// rem				a % b remainder after division (integers only)
	FACTORIAL_RISING_OPERATOR	= "/#",			// n /# m			rising factorial power expressed as a binary operator
	FACTORIAL_FALLING_OPERATOR	= "#/",			// n #/ m			falling factorial power expressed as a binary operator
	FACTORIAL_OPERATOR			= "!",			// n!				conventional semantics (integers only) unary postfix operator
	DECIMAL_SHIFT_OPERATOR		= "*10^",		// 1 *10^ 2			implementation of scientific notation decimal shift as operator
	EXPONENTIATION_OPERATOR		= "**",			// x^y 				evaluated as exp (ln (x) * y) (X^0=1, 0^x=0, x<0 in complex domain)
	BINOMIAL_OPERATOR			= "##",			// (n ## m)			binomial coefficients expressed as binary function (parens optional)
	RADICAL_OPERATOR			= "*\\",		// left * 2\right	product of left operand and SQRT of right
	ROOT_OPERATOR				= "\\",			// binary root		3\x would be cube root of x
	RIGHT_SHIFT_OPERATOR		= ">>",			// right shift		a >> b is same as a / 2^b
	LEFT_SHIFT_OPERATOR			= "<<",			// left shift		a << b is same as a * 2^b

	ASSIGNMENT_PREFIX			= "@",			// @A = 1 			identify variable assignment ('let' is long form)
	DEFINITION_PREFIX			= "!!",			// !! f(x) = 		identify function definition ('define' is long form)
	HG_POLYNOMIAL_PREFIX		= "!*",			// !* f(x) = 		identify function as Hyper-Geometric polynomial in origin
	SEGMENTED_PREFIX			= "!$",			// !$ f(x) = 		identify segmented function with segment polynomials
	LIB_IMPORT_PREFIX			= "!+",			// !+ f(x) = 		identify function definition as library import
	TRANSFORM_PREFIX			= "!%",			// !% f(x) = 		identify function transformation as definition
	TRANSFORM_ENABLE			= "!^",			// !^ f(x) = 		enable function as transformation source
	COMMENT_PREFIX				= "//",			// // anything 		comment to put in output stream
	TIP_PREFIX					= "//*",		// //* anything 	comment to associate with script

	INDEXING_OPERATOR			= "#",			// A#1		 		as opposed to conventional form of index a(1) or a[1]
	COL_INDEX_OPERATOR			= "|#",			// M|#1 			column vector indexing for matrices (same as COL function)
	ROW_INDEX_OPERATOR			= "-#",			// M-#1 			matrix row vector indexing (same as ROW function, also works as M#1)
	DIAG_INDEX_OPERATOR			= "\\#",		// M\#1 			matrix diag vector indexing (array of values taken from diagnoal of matrix)
	TENSOR_OPERATOR				= "*^*",		// M1 *^* M2		tensor product of two matrices, result is matrix of order squared
	HAR_EVAL_OPERATOR			= "+#*",		// COEF +#*(o*t)	evaluate harmonic series using coefficients at t (o=omega)
	EXP_EVAL_OPERATOR			= "*^#",		// COEF *^# x		evaluate exponential defined by coefficients at X
	CLENSHAW_EVAL_OPERATOR		= "@*^",		// COEF @*^ x		evaluate Chebyshev polynomial at X using Clenshaw
	CLENSHAW_PRIME_OPERATOR		= "@*^'",		// COEF @*^' x		evaluate Chebyshev polynomial' at X using Clenshaw 
	CLENSHAW_DPRIME_OPERATOR	= "@*^''",		// COEF @*^'' x		evaluate Chebyshev polynomial'' at X using Clenshaw 
	POLY_DPRIME_OPERATOR		= "+*^''",		// COEF +*^'' x		evaluate polynomial'' defined by coefficients at X 
	POLY_PRIME_OPERATOR			= "+*^'",		// COEF +*^' x		evaluate polynomial' defined by coefficients at X 
	POLY_EVAL_OPERATOR			= "+*^",		// COEF +*^ x		evaluate polynomial defined by coefficients at X
	ARRAY_EVAL_OPERATOR			= "@#",			// ARRAY @# x		evaluate function defined by array at X
	RANGE_EVAL_OPERATOR			= "..",			// LO .. HI			evaluate range between LO and HI

	CMPLX_OPERATOR				= "+!*",		// + i *			complex value = left + i * right
	CMPLX_CONJ_OPERATOR			= "-!*",		// - i *			complex value = left - i * right
	CMPLX_CIS_OPERATOR			= "@!#",		// r @!# theta		same as r*cis(theta) = cos+i*sin
	PLUS_OR_MINUS_OPERATOR		= "+|-",		// plus or minus	-b +|- sqrt (b^2 - 4*a*c)
	MINUS_OR_PLUS_OPERATOR		= "-|+",		// minus or plus	(result is 2 entry array)

	DOT_OPERATOR				= ".",			// dot product		of two arrays
	TSQUAD_OPERATOR				= "$|",			// f$|(lo,hi,err)	tanh-sinh numeric integral approximation
	DCTQUAD_OPERATOR			= "$@",			// dct$@()			Clenshaw-Curtis numeric integral approximation
	TRAPQUAD_OPERATOR			= "$#",			// f$#(lo,hi,dx)	Trapezoidal iterative numeric integral approximation
	TRAPADJUST_OPERATOR			= "$%",			// f$%(lo,hi,dx)	Trapezoidal adjustment for brute force approximation
	INTERVAL_EVAL_OPERATOR		= "||",			// f||(lo,hi)		evaluate f(hi) - f(lo) {definite integral evaluation}
	DELTA_INCREMENT_OPERATOR	= "<>",			// delta			incrementation {used in array declaration and derivative approximation}
	DELTA_INTEGRATION_OPERATOR	= "<*>",		// delta			variable change limit {used in integration approximation}
	DPRIME_OPERATOR				= "''",			// f''(x)			second derivative evaluation
	PRIME_OPERATOR				= "'"			// f'(x)			first derivative evaluation
		;


	/*
	 * built-in function names
	 */

	public static
		final String
	SIN_FUNCTION				= "sin",		// sin
	COS_FUNCTION				= "cos",		// cos
	ASIN_FUNCTION				= "asin",		// arc sine
	ATAN_FUNCTION				= "atan",		// arc tangent
	TAN_FUNCTION				= "tan",		// tan = sin/cos

	SQRT_FUNCTION				= "sqrt",		// square root
	EXP_FUNCTION				= "exp",		// e^x exponentiation
	LOG_FUNCTION				= "ln",			// ln(x) natural logarithm

	Re_FUNCTION					= "Re",			// Re(x) complex real part
	Im_FUNCTION					= "Im",			// Im(x) complex imag part
	CONJ_FUNCTION				= "conj",		// conj(x) complex conjugate
	ARG_FUNCTION				= "arg",		// angle of complex value from x-axis
	CIS_FUNCTION				= "cis",		// cis(x) = cos(x) + i*sin(x)

	GCF_FUNCTION				= "gcf",		// gcf(x,y) greatest common factor
	LCM_FUNCTION				= "lcm",		// lcm(x,y) least common multiple
	SIGN_FUNCTION				= "sgn",		// SGN(x) = x<0? -1: 1
	ABSOLUTE_VALUE_FUNCTION		= "abs"			// absolute value
		;


	/*
	 * named values
	 */

	public static
		final String
	FALSE_SYMBOL				= "false",		// 0
	TRUE_SYMBOL					= "true",		// 1
	I_SYMBOL					= "i",			// sqrt (-1)
	E_SYMBOL					= "e",			// 2.7182818
	PI_SYMBOL					= "pi"			// 3.1415926
		;


	/*
	 * logical operators
	 */

	public static
		final String
	LT_OPERATOR					= "<",			// less than
	GT_OPERATOR					= ">",			// greater than
	LE_OPERATOR					= "<=",			// less than or equal to
	GE_OPERATOR					= ">=",			// greater than or equal to
	NE_OPERATOR					= "~=",			// not equal to
	EQ_OPERATOR					= "==",			// equals
	NOT_OPERATOR				= "~",			// not
	AND_OPERATOR				= "&",			// and
	OR_OPERATOR					= "|",			// or
	NAND_OPERATOR				= "~&",			// not and
	NOR_OPERATOR				= "~|",			// not or
	XOR_OPERATOR				= "|~",			// or but not both
	NXOR_OPERATOR				= "~|~",		// logical equality
	IMPLIES_OPERATOR			= "=>>",		// implies (~x | y)
	NOT_IMPLIES_OPERATOR		= "~=>>",		// not implies (x & ~y)
	IMPLIED_BY_OPERATOR			= "<<=",		// implied by (x | ~y)
	NOT_IMPLIED_BY_OPERATOR		= "~<<=",		// not implied by (~x & y)
	LT_ABS_OPERATOR				= "<||",		// less than absolute value of
	GT_ABS_OPERATOR				= ">||",		// greater than absolute value of
	CHOOSE_OPERATOR				= ":",			// choice based on condition code
	SET_CONDITION_OPERATOR		= "?"			// set condition code
		;


	/*
	 *         Table Of
	 *  Binary Logical Operators
	 *  
	 *    X Y   X Y   X Y   X Y
	 *    0 0   0 1   1 0   1 1
	 *    ---   ---   ---   ---
	 *     0     0     0     0    FALSE 
	 *     0     0     0     1    AND (X & Y)
	 *     0     0     1     0    ~ IMPLIES (X ~=> Y defined as X & ~Y)
	 *     0     0     1     1    X
	 *     0     1     0     0    ~ IMPLIED BY (X ~<<= Y defined as ~X & Y)
	 *     0     1     0     1    Y
	 *     0     1     1     0    XOR (X |~ Y defined as X | Y & ~(X & Y)) a.k.a. logical in-equality (X ~= Y)
	 *     0     1     1     1    OR (X | Y)
	 *     1     0     0     0    NOR (X ~| Y defined as ~X & ~Y)
	 *     1     0     0     1    NXOR (X ~|~ Y defined as ~(X xor Y)) a.k.a. logical equality (X == Y)
	 *     1     0     1     0    ~Y
	 *     1     0     1     1    IMPLIED BY (X <<= Y defined as X | ~Y)
	 *     1     1     0     0    ~X
	 *     1     1     0     1    IMPLIES (X => Y defined as ~X | Y)
	 *     1     1     1     0    NAND (X ~& Y defined as ~X | ~Y)
	 *     1     1     1     1    TRUE
	 */


	/*
	 * operators that define transform types
	 */

	public static
		final String
	TRANSFORM_DEF				= "^=",			// transform		generic transform
	DERIVATIVE_DEF				= "<>=",		// derivative		define derivative function
	ANTIDERIVATIVE_DEF			= "$=",			// anti derivative	define anti-derivative function
	CLENSHAW_EVAL_DEF			= "@*^=",		// define series	define Chebyshev Polynomial series
	EXPONENTIAL_DEF				= "*^#=",		// define expon		define exponential function a * e ^ (bX)
	POLYNOMIAL_DEF				= "+*^=",		// define poly		define ordinary Polynomial series
	HARMONIC_DEF				= "+#*=",		// define har		define harmonic series
	DCT_DEF						= "@="			// DCT				cosine transform
		;


	/**
	 * enumeration of transform types
	 */
	public enum TRANSFORM_TYPE {GENERIC, DERIVATIVE, INTEGRAL, CHEBYSHEV, EXPONENTIAL, POLYNOMIAL, HARMONIC, DCT}

	/**
	 * map identifiers to enumeration
	 */
	public static final HashMap<String, TRANSFORM_TYPE> TRANFORM_MAP = new HashMap<String, TRANSFORM_TYPE> ();


	/**
	 * populate TRANFORM_MAP
	 */

	static
	{
		TRANFORM_MAP.put (TRANSFORM_DEF, TRANSFORM_TYPE.GENERIC);			// use transform interface
		TRANFORM_MAP.put (DERIVATIVE_DEF, TRANSFORM_TYPE.DERIVATIVE);		// use calculus interface to construct derivative
		TRANFORM_MAP.put (ANTIDERIVATIVE_DEF, TRANSFORM_TYPE.INTEGRAL);		// use calculus interface to construct anti-derivative
		TRANFORM_MAP.put (CLENSHAW_EVAL_DEF, TRANSFORM_TYPE.CHEBYSHEV);		// establish Chebyshev polynomial series function
		TRANFORM_MAP.put (EXPONENTIAL_DEF, TRANSFORM_TYPE.EXPONENTIAL);		// establish function of exponential form ae^bx
		TRANFORM_MAP.put (POLYNOMIAL_DEF, TRANSFORM_TYPE.POLYNOMIAL);		// establish polynomial from coefficients
		TRANFORM_MAP.put (HARMONIC_DEF, TRANSFORM_TYPE.HARMONIC);			// establish series from coefficients
		TRANFORM_MAP.put (DCT_DEF, TRANSFORM_TYPE.DCT);						// discrete cosine transform
	}


}

