#!/bin/bash

inputFile=$1
outputFile=`echo $inputFile | replace ".txt" ".filtered.txt"`
INPUTFILE="/Users/shashans/Desktop/birds/splits/attributes.ids"

awk 'BEGIN {

	while (getline < "'"$INPUTFILE"'")
	{
		val[$0]=1;
	}
	close("'"$INPUTFILE"'");
}

{
	for(i=1;i<=NF;i++){
		if(i<=5){
			printf "%s ",$i;
		}
		else{
			if($i in val){
				printf "%s ",$i;
			}
		}
	}
	printf "\n";
}' $inputFile > $outputFile
