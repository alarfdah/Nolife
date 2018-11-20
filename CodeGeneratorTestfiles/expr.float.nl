{*********************************************************
This file test all types of expressions.
*********************************************************}

PROGRAM Expr;
 VAR a,b,c,d:FLOAT;
     i,j,k,l,m:INTEGER;



BEGIN


	a:=1.1; b:=2.2; c:=3.3; d:=4.4;
	WRITE(a+b+c+d);

        i := 1; j := 2; k := 3; l := 4;
	WRITE((i+j+k*l) + (a+b+c+d));

	m:=a<b;
	WRITE(m)


END
