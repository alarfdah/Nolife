PROGRAM case1;
VAR a:INTEGER;
BEGIN
   CASE a OF 
      1,2,3,4:
         WRITE(a);
      5.5:
         WRITE('error')
   END
END

