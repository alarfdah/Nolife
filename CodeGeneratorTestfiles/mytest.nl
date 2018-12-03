
PROGRAM Flow;
 VAR a, b:INTEGER;

BEGIN
	a := 1;
	b := 5;
    WRITE('Enter: ');	
	READ(a);
	CASE a OF
	    1,2: WRITE(0);
	    3,4: WRITE(1);
	    5  : WRITE(2) 
	END;
	
	CASE b OF
	    1,2: WRITE(0);
	    3,4: WRITE(1);
	    5  : WRITE(2) 
	END
END
