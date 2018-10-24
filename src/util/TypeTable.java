package util;

public final class TypeTable {
	
	public final static int INTEGER = 0;
	public final static int FLOAT = 1;
	public final static int CHARACTER = 2;
	public final static int ANYTYPE = 3;
	public final static int NOTYPE = 4;
	
	
	public final static int[][] arithmeticOperators = {
			{INTEGER, FLOAT}, 	// E.g. INTEGER + X
			{FLOAT, FLOAT}		// E.g. FLOAT + X
	};
	
	public final static int[][] logicOperators = {
			{CHARACTER, NOTYPE, NOTYPE}, 	// E.g. CHARACTER OR X	
			{NOTYPE, INTEGER, INTEGER},	// E.g. INTEGER OR X
			{NOTYPE, INTEGER, FLOAT}		// E.g. FLOAT OR X
	};
	
	public final static int[][] relationalOperators = {
			{INTEGER, NOTYPE, NOTYPE},	// E.g. CHARACTER < X
			{NOTYPE, INTEGER, INTEGER},	// E.g. INTEGER < X
			{NOTYPE, INTEGER, INTEGER}	// E.g. FLOAT < X
	};

}
