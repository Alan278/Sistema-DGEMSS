#!/bin/bash

declare date=$(date --date='7 days ago' +%Y-%m-%d)
declare directory="/home/Respaldos"

for file_name in $(ls $directory)
do
	declare file_date=$(echo $file_name | cut -d'_' -f 3)

	if [[ "$file_date" < "$date" ]] ;
	then
		rm /home/Respaldos/$file_name
	fi
done

