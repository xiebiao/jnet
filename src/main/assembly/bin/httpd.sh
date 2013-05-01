#!/bin/sh

CLASSPATH=$CLASSPATH:../libs/*

java -classpath $CLASSPATH com.github.jnet.demo.httpd.HttpServer