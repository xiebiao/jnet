#!/bin/sh

CLASSPATH=$CLASSPATH:../lib/*

java -classpath $CLASSPATH com.github.jnet.Bootstrap $*