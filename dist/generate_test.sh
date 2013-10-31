#!/bin/bash
echo "Please provide a set name:"
read setname
echo "How many files must $setname have:"
read filecount
mkdir testsets/$setname
for (( i=1; i<=$filecount; i++ ))
do
    cp mario.gif testsets/$setname/mario$i.gif
done
