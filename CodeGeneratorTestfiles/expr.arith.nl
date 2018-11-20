{*********************************************************
This file test all types of expressions.
*********************************************************}

PROGRAM Expr;
 VAR i,j,k,l,m:INTEGER;
     n : ARRAY[3..9] OF INTEGER;



BEGIN


	i:=1; j:=2; k:=3; l:=4;

	WRITE(i+j+k*l);

        WRITE(5 MOD 7);

	n[5] := 3;
	WRITE(n[5])


END
