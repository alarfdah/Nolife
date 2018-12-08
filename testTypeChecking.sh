#!/bin/bash
SRCS=`ls ParserTestFiles/*.nl`
for f in $SRCS;
do
  java -jar nlcTC.jar $f
done
