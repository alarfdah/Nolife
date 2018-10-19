{*This file tests the following error:

[1] Procedure call invoking a function

[2] A function call that invokes a procedure

Note: There are multiple ways in which the 
above error is tested in this file *}


 PROGRAM cow;
 VAR b:FLOAT;


	PROCEDURE A (b:INTEGER);
	BEGIN		
	   RETURN 1  {* error-A is assigned a value like
		    a function but its been declared	
		    as a procedure *}
	END;	
   
	FUNCTION B(a:ARRAY[1..20] OF INTEGER):INTEGER;
        VAR 
            c : INTEGER;
	BEGIN
	a[A(1)] := c; {* error-A is not a function and hence, does
			 not return a value *}
	B(a)  {* error-B is declared as function but
		      used as a procedure *}
	      {* error, no return statment *}
	END;

BEGIN
b:=200;
A(100)

END
