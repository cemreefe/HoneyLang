#!/bin/bash
javac Temp.java
java Temp
find . -name '*.class' -exec rm -f {} \;
