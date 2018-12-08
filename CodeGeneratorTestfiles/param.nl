{*********************************************************

This testcase checks subroutine calls, both recursive and
nonrecursive, as well as parameter passing.

*********************************************************}

PROGRAM subprog;
 VAR i,j,k,l:INTEGER;
     a,b,c,d:INTEGER;
     C1,C2:CHARACTER;

	
	FUNCTION b4 (a:INTEGER) :INTEGER;
	BEGIN
		WRITE(4);
		RETURN a+1
	END;
	
	
	{* test pass by ref parms *}
	PROCEDURE d2(a,b:INTEGER);
	VAR tmp:INTEGER;
	BEGIN
		WRITE(b);
	        WRITE(a);
		tmp:=a;
		a:=b;
		b:=tmp
	END;
	
	PROCEDURE d3(a,b,c,d:INTEGER);
	BEGIN
		a:=a+1;
		b:=b+2;
		c:=c+3;
		d:=d
	END;
	
	PROCEDURE d1(a:INTEGER);
	VAR b:INTEGER;
	
	BEGIN
		b:=a*200;
		WRITE(a); WRITE(b);
		d2(a,b);
		WRITE(a); WRITE(b);
		d3(a,a,a,b);
		WRITE(a); WRITE(b);
		d3(b,b,b,b);
		WRITE(a); WRITE(b)
	END;


{*main*}
BEGIN
	a:=1; b:=2; c:=3; d:=4;
	i:=1; j:=2; k:=3; l:=4;



	WRITE(b4(k));
	d1(a);
	WRITE(a);
	WRITE(b4(b4(1)))


END
