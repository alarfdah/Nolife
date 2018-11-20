{*********************************************************
This file test all types of expressions.
*********************************************************}

PROGRAM Expr;
 VAR i,j,k,l,m:INTEGER;



BEGIN


	i:=1; j:=2; k:=3; l:=4;

	m:=i<j;
	WRITE(m);
	WRITE(l>k);
	WRITE(NOT(l>k));
        WRITE(0 OR ((i<j)AND(k<>l)))

END
