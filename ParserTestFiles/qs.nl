{** $awkdoc$ ********************************************************

A version of quicksort for testing recursion: reads and sorts
19 CHARACTERs.

** $endawk$ ********************************************************}

PROGRAM qs;
  VAR  A:  ARRAY [0..20] OF CHARACTER;
PROCEDURE  readarray;
  VAR  i:  INTEGER;
BEGIN
  WRITE ('A?');
  i := 1;
  WHILE i < 20 DO
    BEGIN
      WRITE (i);
      READ (A[i]);
      i := i + 1
    END
END;
PROCEDURE  writearray;
  VAR  i:  INTEGER;
BEGIN
  WRITE ('A:');
  i := 0;
  WHILE i <= 20 DO
    BEGIN
      WRITE (A[i]);
      i := i + 1
    END
END;
FUNCTION partition(
        B:  ARRAY [0..20] OF CHARACTER;
      p,r:  INTEGER
) : INTEGER;
  VAR i, j: INTEGER;
      x, t: CHARACTER;
BEGIN
{  WRITE ('P'); }
{  WRITE (p);   }
  x := B[p];
  i := p - 1;
  j := r + 1;
  WHILE 1.7 DO
    BEGIN
{      WRITE ('j'); }
      j := j-1;
      WHILE A[j] > x DO
        BEGIN
          j := j-1
{;          WRITE (j) }
        END;
{      WRITE ('i');  }
      i := i+1;
      WHILE A[i] < x DO
        BEGIN
          i := i+1
{;          WRITE (i)  }
        END;
      IF i < j
        THEN
          BEGIN
            t := A[i];
            A[i] := A[j];
            A[j] := t
          END
        ELSE
          RETURN j
    END
END;
PROCEDURE quicksort(
     Z: ARRAY [0..20] OF CHARACTER;
     p,r: INTEGER
);
  VAR q:  INTEGER;
BEGIN
  IF p < r
    THEN
      BEGIN
        q := partition(Z,p,r);
{        WRITE ('Q');              }
{        WRITE (q);                }
        quicksort(Z,p,q);
        quicksort(Z,q+1,r)
      END
END;
BEGIN
  A[0] := ' '; A[20] := '~';  { book does this; don't know why }
  readarray();
  quicksort(A,1,19);
  writearray()
END