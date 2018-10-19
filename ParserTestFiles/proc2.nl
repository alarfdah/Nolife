PROGRAM proc;

VAR c :INTEGER;

PROCEDURE b(x,y:INTEGER;z:FLOAT);

  BEGIN
    z := x+y
  END;

BEGIN
  b(1,1,2.0)
END
