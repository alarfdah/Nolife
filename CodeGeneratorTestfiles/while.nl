{*********************************************************

This testcase performs simple test on flow of control.

*********************************************************}

PROGRAM Flow;
 VAR a:INTEGER;
     b:INTEGER;

BEGIN




	a:=1;
	WHILE a<=10 DO BEGIN
		WRITE(a);
		a:=a+1
	END;
	WRITE(a);

	b:=1;
	WHILE b<=10 DO BEGIN
	  a:=1;
	  WHILE a <= 10 DO BEGIN
	     WRITE(a*b);
             a:= a+1
          END;
	  b:=b+1
	END 


END
