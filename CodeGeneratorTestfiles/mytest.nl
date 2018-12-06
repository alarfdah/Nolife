PROGRAM foo;
  VAR x,y,z:INTEGER;

PROCEDURE decls(a,b :INTEGER);
BEGIN
  WRITE(a);
  WRITE(b)
END;

BEGIN
  x := 1;
  y := 5;
  z := 6;
  decls(x + y, z)
END