{*********************************************************
This file test all types of expressions.
*********************************************************}

PROGRAM Expr;
 VAR o : ARRAY['a'..'f'] OF INTEGER;
     C1,C2:CHARACTER;



BEGIN


	C1:='A'; C2:='z';
	WRITE(C1 > C2);
	WRITE(NOT(C1 > C2));

	o['d'] := 3;
	WRITE(o['d'])


END
