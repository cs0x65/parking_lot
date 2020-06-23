#!/bin/bash

if [ "$#" -ne 1 ] 
then
	echo "Missing commands file to read from!"
	echo "Usage: parking_lot filename"
fi

java -jar parking-lot/target/parking-lot-1.0-SNAPSHOT-shaded.jar $1
