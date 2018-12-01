PROGRAM helloworld;
VAR x, q: INTEGER;
	y, f1, f2, f3, f4: FLOAT;
	z: ARRAY[1..3] OF INTEGER;
FUNCTION b4 (a:INTEGER) :INTEGER;
BEGIN
	WRITE(4);
	RETURN a+1
END;
BEGIN
	f1 := 1.0;
	f2 := 2.0;
	f3 := 3.0;
	x := 2;
	
	WRITE(x)
END