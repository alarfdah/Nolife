{*This file tests the following error:

[1] Illegal mixed type expressions

Note: There are multiple ways in which the 
above error is tested in this file *}


 PROGRAM cow;
  VAR b: ARRAY[1..2] OF CHARACTER;
   
	FUNCTION B(a:INTEGER;e:CHARACTER):CHARACTER;
        VAR 
            c : INTEGER;
	    d : FLOAT;
	
	BEGIN
		a:= a + (c*d)*e; {*error-character used in right
				   hand side of assignment *}
		B:=c +c*d; {* error-character is being assigned
			       a float value *}
                e:= c<d ; {* character being assigned an integer*}
		c:= NOT d; {*NOT only valid for integers*}
		c:= NOT e; {*NOT only valid for integers*}
		c:= a AND d; {*AND&OR only defined for ints*}
		c:= e AND e; {*AND&OR only defined for ints*}
		c:= e < B(a,e); {*okay*}
		c:= c <> d; {*okay*}
		c:= c >= e; {*cannot mix char type with float|int*}
		c:= B(a,e) = d; {*cannot mix char type with float|int*}
		e:= NOT c;  {*error as NOT returns an integer *}
		e:= a OR a; {* error- character being  assigned an 
				integer *}
		RETURN e
	END;

	PROCEDURE complex;
	 VAR a,b,c,d,e,f,g:FLOAT;
	     i,j,k,l,m,n: INTEGER;
	     w,x,y,z: ARRAY[1..5] OF FLOAT;
	     C1,C2,C3 : CHARACTER;
	BEGIN
	{* a bunch of complex but correct expressions *}
		a:=a+b+c+d+e;  
		i:=i*j*k+l*m; 
		C1:=B(i,C2);   
		a:=a*i+b*j+c*k;
		i:=a*b*c*i*j*k*e*f*l+m*a+d*n; 
		a:=B(i+j+k*l,C2) <> C3; 
		a:=w[B(i,C1)>C2]; 
		a:=1;  
		i:=1.2e5; 
		C1:='Q'; 
		C2:=B(i,B(j,B(k,C1))) 
	END;


	PROCEDURE complex2;
	 VAR a,b,c,d,e,f,g:FLOAT;
	     i,j,k,l,m,n: INTEGER;
	     w,x,y,z: ARRAY[1..5] OF FLOAT;
	     C1,C2,C3 : CHARACTER;
	BEGIN
	{* complex and incorrect expressions *}
		C1:=B(i+j+k*l,C2)+C3; {*cannot add chars*}
		a:=b*c+d*C1+e+f;  {*char type in arith expr*}
		C2 := a*b*c+j+i+k; {* char type on lhs *}
		i:= (B(i,C2)>B(j,C2)) + (B(l,C3)>j) {*compare w/ j is bad*}
	END;

BEGIN
complex();
complex2();
b[1]:=100 {*b declared as character array and assigned an integer *}

END
