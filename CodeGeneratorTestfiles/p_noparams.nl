{*********************************************************

This testcase checks subroutine calls, both recursive and
nonrecursive, as well as parameter passing.

*********************************************************}

PROGRAM subprog;
 VAR i,j,k,l:INTEGER;

{* test simple nonrecursive procedure calls of no parms *}

PROCEDURE a1;
BEGIN
	WRITE(1)
END;

PROCEDURE a2;
BEGIN
	a1(); 
	WRITE(2) 
END;

PROCEDURE a3;
BEGIN
	a1();
	a2();
	WRITE(3) 
END;

PROCEDURE a4;
BEGIN
	a1();
	a2();
	a3();
	WRITE(4)
END;


{*main*}
BEGIN


	a1(); a2(); a3(); a4() 



END
