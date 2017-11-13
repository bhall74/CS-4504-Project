#!/bin/bash

run=$1
SRC="$(ls src/*.java)"
#Router="$(ls src/TCPServerRouter.java)"
#echo $SRC
for class in "$SRC"; do
  echo "$class"
  if [ "$run" == "main" ]; then
    if [ $class == "src/Main.java" ]; then
      echo "$class" found...
      java -cp bin $class
    fi
  fi

  if [ "$run" == "router" ]; then
    if [ $class == "src/TCPServerRouter.java" ]; then
      echo "$class" found...
      java -cp bin $class
    fi
  fi
done
