{*This file tests the following error:

[1] Mismatch between actual parameters at a call site
    and the definition of the called procedure 

Note: There are multiple ways in which the 
above error is tested in this file *}


 PROGRAM cow;
 VAR b:ARRAY[1..2] OF CHARACTER;
   
	PROCEDURE A(a:INTEGER);
	BEGIN
	   A(B(100)) {* Function B is of type Character
			 but parameter to A is of type 
		         integer *}
	END;

	FUNCTION B(a:INTEGER):CHARACTER;
        VAR 
	    d:INTEGER;
	BEGIN
	C(1,1,1,1,1,1,1); {*last 3 args wrong type *}
	RETURN B(B(d)) {* B(d) returns a character-but parameter
		       is of type integer *}
	END;

	PROCEDURE C(a,b,c,d:INTEGER;e,f,g:FLOAT);
	BEGIN
	 a:=b+c+d;
	 e:=f+g;
	 C(1,1,1,1,1.1,1.1,1.1) {*okay*}
	END;

BEGIN
  C(1,2,3,4.0,5.0,6.0); {*calling with 6 args instead of 7*}
  C(1,2,3,4,5.0,6.0,7.0,'a'); {*calling with 8 args instead of 7*}
  C(1,2,3,4,5,6,7); {*last 3 args of wrong type*}
  C(1,2,3,4,5.0,6.0,b[1]); {*last arg of wrong type*}
  b[1]:=B(b[2]) {* error-Parameter to B is of type integer
		    but b[2] is of type character *}
END
