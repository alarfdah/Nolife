{*********************************************************

This testcase checks subroutine calls, both recursive and
nonrecursive, as well as parameter passing.

*********************************************************}

PROGRAM subprog;
 VAR i,j,k,l:INTEGER;
     a,b,c,d:INTEGER;
     x:ARRAY[1..10] OF INTEGER;

{* pass array as parm *}

PROCEDURE init (a:ARRAY[1..10] OF INTEGER);
VAR i,j:INTEGER;
BEGIN
	i:=1; j:=10;
	WHILE i<=10 DO BEGIN
	  a[i]:= (i*0.01)+j;
	  WRITE(a[i]);
	  i:=i+1;
	  j:=j+1
	END
END;

PROCEDURE writearray(z:ARRAY[1..10] OF INTEGER);
BEGIN
	WRITE(z[1]);
	WRITE(z[2]);
	WRITE(z[3]);
	WRITE(z[4]);
	WRITE(z[5]);
	WRITE(z[6]);
	WRITE(z[7]);
	WRITE(z[8]);
	WRITE(z[9]);
	WRITE(z[10])
END;

PROCEDURE inc(a:INTEGER);
BEGIN
	a:=a+1
END;


{*main*}
BEGIN
	init(x);
	writearray(x)



END
