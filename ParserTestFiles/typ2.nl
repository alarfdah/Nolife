{*********************************************************
This file test all types of expressions.
*********************************************************}

PROGRAM Expr;
 VAR i,j,k,l,m:INTEGER;
     a,b,c,d:FLOAT;
     C1,C2:CHARACTER;



BEGIN

     i := C1 + j;
     a := C1 + b;
     a := b + C1;
     i := j + C1;
     j := C1 + C2;
     i := C1 OR i;
     j := C2 OR b;
     i := i OR C1;
     j := b OR C2;
     i := i < C1;
     i := b < C2;
     i := C1 < i;
     i := C1 < b;
     C1 := i;
     C2 := b
END
