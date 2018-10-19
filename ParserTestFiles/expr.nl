PROGRAM expr;

VAR a:INTEGER;
    b:CHARACTER;
    c:FLOAT;
    d:ARRAY [1..10] OF INTEGER;

BEGIN

   IF (b = b) THEN
    BEGIN
     a := a + a;
     c := c * a;
     c := a - c
    END;

   IF (c >= a) THEN
     b := b OR b;

   a := a AND c

END
