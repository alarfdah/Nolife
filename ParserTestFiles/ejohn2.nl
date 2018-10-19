{
Test function calls as parameters

Eric Johnson	ejohn
}

PROGRAM e;

VAR i : INTEGER;

PROCEDURE print (x : INTEGER);
BEGIN
WRITE (x)
END;

FUNCTION inc (x : INTEGER) : INTEGER;
BEGIN
RETURN x + 1
END;

FUNCTION sqr (x : INTEGER) : INTEGER;
BEGIN
RETURN x * x
END;

BEGIN
READ (i);
WHILE (i) DO
	BEGIN
	print (i);
	print (inc (sqr (inc (i))));
	READ (i)
	END
END
