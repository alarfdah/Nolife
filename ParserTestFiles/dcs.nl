{ *** program created by Dave Schwartz *** }

PROGRAM Test;
	VAR x,y,temp : INTEGER;
	FUNCTION fact(a:INTEGER):INTEGER;
		BEGIN
			IF (a < 1) 
			THEN
				RETURN 1
			ELSE   
				RETURN (fact(a - 1) * a)
		END;
	BEGIN
		READ(x);
		READ(y);
		temp := y + fact(5) - fact(x);
		WRITE(temp);
		WRITE(x);
		WRITE(y)
	END
