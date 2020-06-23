#!/bin/bash

dir=$(pwd)
echo $dir 
cd parking-lot && ./mvnw clean package
