PROGRAM foo;
  VAR a:INTEGER;

PROCEDURE decls(a:INTEGER);
BEGIN
  WRITE(a);
  IF (a > 0) THEN
    decls(a - 1)
  ELSE
    a := 0
END;

BEGIN
  a := 5;
  decls(a)
END