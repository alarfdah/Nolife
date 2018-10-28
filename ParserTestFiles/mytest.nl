PROGRAM while;

VAR a,b : ARRAY [1..10] OF INTEGER;
    i : INTEGER;

BEGIN
  i := 1;
  WHILE (i < 11) DO
     BEGIN
       j := 10;
       WHILE (j > 0) DO
         BEGIN
           a[i] := a[j] + 1;
	   j := j - 1
         END;
       i := i + 1
     END
END
