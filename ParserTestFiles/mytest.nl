PROGRAM fib;

VAR globalCount, input, i, loop : INTEGER;

FUNCTION fib1 (x : INTEGER) : INTEGER;

BEGIN
	globalCount := fib1(x - 1) + fib1(x - 2)
END;

BEGIN
	fib1(x)
END
