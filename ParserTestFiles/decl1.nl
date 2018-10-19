{*This file tests the following error:

[1] Variables referenced but not declared

Note: There are multiple ways in which the 
above error is tested in this file *}


 PROGRAM cow;
   
	FUNCTION B(a:INTEGER):CHARACTER;
        VAR 
	    b : CHARACTER;
            c : INTEGER;
	    w : ARRAY[1..100] OF INTEGER;
	BEGIN
	a := c  ;
	b:=B(B(C)); {*C referenced  but not declared *}
	c := c+w[a]; {*okay*}
	c := c+w[b2]+10101; {*b2 not declared*}
	IF dummy THEN c:=0; {*dummy not declared*}
	WHILE dummy2 DO c:=0; {*dummy2 not delcared*}
	CASE dummy3 OF END;   {*dummy3 not delcared*}
	WRITE(dummy4); {*dummy4 not declared*}
	c:= 100*(102+103+c+w[1]*5+d)+4; {*d not declared*}
	b:=B(1+2+3+4+e+5+6); {*e not declared*}
	RETURN z[1] {* error-z referenced but not declared *} 
	END;

BEGIN
b:= c {*b & c referenced but not declared (in this scope) *}

END
