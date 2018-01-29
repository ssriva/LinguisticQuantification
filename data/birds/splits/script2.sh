#!/bin/bash

inputFile=$1
outputFile=$1.new
i=$2

awk '{
	for(i=1;i<NF;i++){ 
		if(i!=3){
			printf "%s ",$i
		}   
		else{
			if($3=="'"$i"'")
			{
				printf "POS "
			}
			else{
				printf "NEG "
			}
		}
	}  
	printf "\n"

}' $inputFile > $outputFile
