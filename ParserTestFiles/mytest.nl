{*This file tests the following error that can occur in
declarations. 

[1] Variable declared multiple times within same scope.

*}

 PROGRAM cow;
VAR 
    a:INTEGER; 
    b:FLOAT;
    d:ARRAY[1..2] OF CHARACTER;


	PROCEDURE c (b:INTEGER); 
	BEGIN		
	   b:=1
	END;
	
	FUNCTION A (a:INTEGER; b:INTEGER): INTEGER;
	BEGIN		
        a:=b;
        RETURN(a)
	END;
	
BEGIN
	A(a, b)
END
