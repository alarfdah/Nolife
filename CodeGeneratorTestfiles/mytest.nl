PROGRAM subprog;
 VAR x:ARRAY[1..10] OF INTEGER;

{* pass array as parm *}

PROCEDURE init (a:ARRAY[1..10] OF INTEGER);
VAR i,j:INTEGER;
BEGIN
    i := 1;
    a[i]:= 2;
    WRITE(a[i])
END;

{*main*}
BEGIN
    init(x);
    WRITE(x[1])
END