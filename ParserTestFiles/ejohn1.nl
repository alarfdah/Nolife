{ 
Very bad fibinocci number finder: useful to test a lot of recursion.
Use small numbers.

Eric Johnson,  ejohn
}

PROGRAM fib;

VAR globalCount, input, i, loop : INTEGER;

FUNCTION fib1 (x : INTEGER) : INTEGER;

BEGIN
globalCount := globalCount + 1;
IF ((x = 1) OR (x = 0)) THEN
	RETURN (1)
ELSE
	RETURN fib1 (x - 1) + fib1 (x - 2)
END;

BEGIN
WRITE ('Enter number of fib numbers to find:');
READ (i);
loop := 1;
WHILE (loop <= i) DO
	BEGIN
	globalCount := 0;
	WRITE (' ');
	WRITE ('Enter fib number to find:');
	READ (input);
	input := fib1 (input);
	WRITE ('Result:');
	WRITE (input);
	WRITE ('Number of fib1s executed:');
	WRITE (globalCount);
	loop := loop + 1
	END;
WRITE ('Bye')
END
