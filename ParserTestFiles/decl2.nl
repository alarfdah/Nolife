{*This file tests the following error that can occur in
declarations. 

[1] Variable declared multiple times within same scope.

*}

 PROGRAM cow;
VAR 
    a:INTEGER; {*error-variable is declared globally as well
	         as being assigned as the name of a procedure *}
    b:FLOAT; {*error-variable declared as global twice-see
	       declaration below *}
    d:ARRAY[1..2] OF CHARACTER;
    b:INTEGER;


	PROCEDURE a (b:INTEGER); {* error-a declared as procedure
			            as well as global *}
	BEGIN		
	   b:=1
	END;	

	PROCEDURE A (a:INTEGER; b:INTEGER);
	BEGIN		
        a:=b
	END;	

	FUNCTION A:INTEGER; {* error-A declared as function as well
			       as a procedure above *}
	BEGIN
	 b:=200 {*global b is being used here *}
	 	{* error, no return statement *}
	END;

	FUNCTION B(a:INTEGER):CHARACTER; 
        VAR  a : INTEGER; {*error-a declared twice in same scope-
			    as a parameter and as a local variable *}
	BEGIN
	RETURN b                                               
	END;

	FUNCTION C(a:INTEGER; a:FLOAT):CHARACTER;{*error-
		 a declared as parameter twice-two declarations
		 in same scope *}
	BEGIN
	RETURN d[1]
	END;
BEGIN
d[2]:=200
END
