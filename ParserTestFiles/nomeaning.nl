{ a sample Oard program. }


PROGRAM Test1;
	VAR 
		x, y : INTEGER;
		a : ARRAY[10..1] OF FLOAT;


	FUNCTION junk(a,b:INTEGER) :INTEGER ;
		BEGIN
			a:=b;
			IF a THEN READ(a) ELSE READ(a);
			WHILE b DO WRITE(a);
			RETURN b
		END;

	PROCEDURE stuff (a:INTEGER; b:CHARACTER);
		VAR temp : INTEGER;
		BEGIN
			temp := a;
			a := b;
			b := temp { error, cannot assign a char an int value}
		END;


	BEGIN
		READ(x);
		WRITE('HELLO_comp412');
		BEGIN
			y := 1+2+3*4
		END;
		{ I hope you're still with me }
		stuff(junk(x,y),'Q')
	END
