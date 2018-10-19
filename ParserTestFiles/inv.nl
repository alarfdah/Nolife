PROGRAM inv;

FUNCTION a(x,y:INTEGER) : INTEGER;

 BEGIN
   RETURN x*y
 END;

PROCEDURE b(x:FLOAT;y:CHARACTER);

  BEGIN
    WRITE(y);
    WRITE(x)
  END;


BEGIN
  b();
  b(1);
  b(1,1,1);
  b(1.0,1);
  b(1,'a');
  WRITE(a());
  WRITE(a(1));
  WRITE(a(1,'a',3));
  WRITE(a(1.0,'a'));
  WRITE(a(1,1))
END
