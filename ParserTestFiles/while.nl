PROGRAM while;

VAR a,b : ARRAY [1..10] OF INTEGER;
    i : INTEGER;

BEGIN
  i := 2;
  WHILE (i < 11) DO
     BEGIN
       a[i] := a[i-1] + 1;
       i := i + 1
     END
END
