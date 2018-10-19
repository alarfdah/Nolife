{*This file tests the following error:

[1] constant valued subscript outside the range
    of the declaration of an array 

Note: There are multiple ways in which the 
above error is tested in this file *}


 PROGRAM cow;
 VAR b:FLOAT;
     z:ARRAY[5..6] OF FLOAT;

	FUNCTION B(a:ARRAY['a'..'p'] OF INTEGER):INTEGER;
        VAR 
            c : ARRAY[25..100] OF INTEGER;
	BEGIN
	   c[20]:=100; {*error-reference to element of c
			 outside the range of c (low) *}
	   c[25]:=c[101]+c[10000]; {* out of range (high) *}
	   RETURN B(a['A']) {* error-reference to element of a
			    is outside range of a *}
	END;

BEGIN
b:=z[200]  {*out of range (high) *}

END
