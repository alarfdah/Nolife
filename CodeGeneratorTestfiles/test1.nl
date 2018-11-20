{*********************************************************

This testcase checks subroutine calls, both recursive and
nonrecursive, as well as parameter passing.

*********************************************************}

PROGRAM subprog;
 VAR x:INTEGER;

FUNCTION f : INTEGER;
BEGIN
	RETURN 1
END;

{*main*}
BEGIN
	x := 10 + f();
	WRITE(x)
END
