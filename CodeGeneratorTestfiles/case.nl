{*********************************************************

This testcase performs simple test on flow of control.

*********************************************************}

PROGRAM Flow;
 VAR a:INTEGER;

BEGIN

        WRITE('Enter: ');	
	READ(a);
	CASE a OF
	    1,2: WRITE(0);
	    3,4: WRITE(1);
	    5  : WRITE(2) 
	END
END
