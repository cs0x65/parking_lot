#!/bin/bash

commits=$(git log --format=%h)
for c in $commits 
do 
	git archive --format=zip -o $c.zip $c | cp $c.zip $1
done
echo "Finished archiving all commits."
