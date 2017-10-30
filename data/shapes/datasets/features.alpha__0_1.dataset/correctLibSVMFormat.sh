#!/bin/bash

tmpFile="/Users/shashans/"$RANDOM".tmp"

awk '{ 
	if(NR==1){
		printf "%s\n", $0;
	}
	else {
		for(i=1;i<=NF;i++){ 
			if($i!~/:0/){
				printf "%s ",$i
			} 
		} 
		printf "\n"
	}  

	}' $1 > $tmpFile;

mv $tmpFile $1;


