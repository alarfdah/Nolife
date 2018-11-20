{*********************************************************

This testcase checks subroutine calls, both recursive and
nonrecursive, as well as parameter passing.

*********************************************************}

PROGRAM subprog;
 VAR x:FLOAT;

FUNCTION f : FLOAT;
BEGIN
	RETURN 1.0
END;

{*main*}
BEGIN
	x := 10.0 + f();
	WRITE(x)
END
