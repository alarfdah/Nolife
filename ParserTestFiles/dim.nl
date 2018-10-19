{*This file tests the following error:

[1] Incorrect number of dimensions in a variable reference

Note: There are multiple ways in which the 
above error is tested in this file *}


 PROGRAM cow;
 VAR b:FLOAT;
     e:ARRAY[1..2] OF INTEGER;
   
	FUNCTION B(a:ARRAY[1..20] OF INTEGER):INTEGER;
        VAR 
            c : INTEGER;
	BEGIN
	a[20] := c[1]; {* error-c is not an array *}
	RETURN B(a) 
	END;

BEGIN
b:=e {* error-e is an array *}

END
