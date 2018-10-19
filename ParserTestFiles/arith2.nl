PROGRAM arith;

VAR a,b : INTEGER;

FUNCTION c(x:INTEGER) : INTEGER;

BEGIN
  RETURN x * 2
END;

BEGIN

  a := b + c(1) * b - c(2)*(a +b)

END
