PROGRAM proc;

VAR c: INTEGER;
	a: ARRAY[1..2] OF INTEGER; 

PROCEDURE b(x:FLOAT);

  BEGIN
    x:=1.0
  END;

BEGIN
  b(1.0);
  b(c);
  b(a[1])
END