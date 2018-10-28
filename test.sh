#!/bin/bash
SRCS=`ls ParserTestFiles/*.nl`
for f in $SRCS;
do
  java -jar nlc.jar $f
done
