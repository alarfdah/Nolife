{ Pseudo-code for the swap routine }

PROGRAM swapper;
VAR i,j:INTEGER;

PROCEDURE swap(x,y:INTEGER);
VAR temp:INTEGER;

BEGIN
        temp:=x;
        x:=y;
        y:=temp;
        WRITE(y);
        WRITE(x)
END;

BEGIN
        READ(i);
        READ(j);
        swap(i,j);
        WRITE(i)
END
