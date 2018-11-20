{*********************************************************

This testcase performs simple test on flow of control.

*********************************************************}

PROGRAM Flow;
 VAR a,b:INTEGER;

BEGIN

     
        a := 0;
	b := a -1;
	IF a THEN
		WRITE(1)
	ELSE
		WRITE(0);

	IF b THEN
		IF a THEN
			WRITE(0)
		ELSE
			WRITE(1)
	ELSE
		WRITE(0)
END
