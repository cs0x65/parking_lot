#!/bin/bash

dir=$(pwd)
echo $dir 
cd parking-lot
mvn clean test
mvn clean package
