{ The sample program from the Oard document }

PROGRAM example;
VAR x, z, f: FLOAT;

	y: ARRAY[1..10] OF INTEGER;

BEGIN
	y[2] := 5;
	WRITE(y[2])
END