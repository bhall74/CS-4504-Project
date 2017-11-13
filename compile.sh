#!/bin/bash
SRC="$(ls src/*.java)"
#echo "without quotes"
#echo $SRC
#echo "with quotes"
#echo "$SRC"
for class in "$SRC";
  do
    javac $class
    echo $class
done

#ls src/*.java;
