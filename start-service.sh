#!/bin/sh

A=AcsSearch
V=0.0.1-SNAPSHOT
JARFILE=$A-$V.jar
ENV_MODE=DEV

pkill -f $A

nohup java -jar $JARFILE &

#tail -f nohup.out
