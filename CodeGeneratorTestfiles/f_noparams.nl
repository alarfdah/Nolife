{*********************************************************

This testcase checks subroutine calls, both recursive and
nonrecursive, as well as parameter passing.

*********************************************************}

PROGRAM subprog;
 VAR i,j,k,l:INTEGER;
     a,b,c,d:INTEGER;
     C1,C2:CHARACTER;
     x:ARRAY[1..10] OF INTEGER;

{*now test simple nonrecursive functions of zero or one parms *}

FUNCTION b1:INTEGER;
BEGIN
	WRITE(1);
	RETURN 1
END;

FUNCTION b2:INTEGER;
BEGIN
	WRITE(2);
	RETURN b1()
END;

FUNCTION b3:INTEGER;
BEGIN
	WRITE(3);
	RETURN b1()+b2()
END;

FUNCTION b4 :INTEGER;
BEGIN
	WRITE(4);
	RETURN b1()+b2()+b3()
END;



{*main*}
BEGIN
	WRITE(b1()+b2()+b3()+b4())

END
