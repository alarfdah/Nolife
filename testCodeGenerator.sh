#!/bin/bash
SRCS=`ls CodeGeneratorTestfiles/*.nl`
for f in $SRCS;
do
  java -jar nlcCG.jar $f
done
