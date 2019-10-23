#!/bin/bash

esUrl="http://172.30.125.231:9200"
path="/iot/dataDump"
filePath="$path/mysqlData.txt"

if [[ ! -n "$1" ]]; then
  echo "Please enter the mysql host !"
elif [[ ! -n "$2" ]]; then
    echo "Please enter the mysql userName !"
elif [[ ! -n "$3" ]]; then
    echo "Please enter the mysql password !"
else
    if [[ ! -d $path ]]; then
    	mkdir -p $path
      chown -R mysql:mysql $path
    else
    	rm -rf $path/*
    fi
    
    mysql -h "$1" -u$2 -p$3 -P 3306 -e "SELECT A.ID, A.ROUTE_ID, A.FIELD1, A.FIELD2, A.FIELD3, A.FIELD4, A.FIELD5, A.FIELD6, A.FIELD7, A.FIELD8, A.FIELD9, A.FIELD10, A.FIELD11, A.FIELD12, A.FIELD13, A.FIELD14, A.FIELD15, A.FIELD16, A.FIELD17, A.FIELD18, date_format( FIELD19, \"%Y-%m-%d %H:%i:%s\" ) AS FIELD19,
    date_format( FIELD20, \"%Y-%m-%d %H:%i:%s\" ) AS FIELD20, A.FIELD21, A.FIELD22, A.FIELD23, date_format( FIELD24, \"%Y-%m-%d %H:%i:%s\" ) AS FIELD24, A.FIELD25, A.FIELD26, A.FIELD27, A.FIELD28, A.FIELD33, date_format( FIELD29, \"%Y-%m-%d %H:%i:%s\" ) AS FIELD29, A.FIELD30, A.FIELD31, A.FIELD32, A.FIELD34, A.FIELD35, A.FIELD36, A.FIELD37, A.FIELD38, A.FIELD39, date_format( FIELD40, \"%Y-%m-%d %H:%i:%s\" ) AS FIELD40, A.FIELD41, A.FIELD42, A.FIELD43, A.FIELD44, A.FIELD45,
    A.FIELD46, A.FIELD47, A.FIELD48, A.FIELD49, date_format( FIELD50, \"%Y-%m-%d %H:%i:%s\" ) AS FIELD50, A.FIELD51, A.FIELD52, C.INFO1, C.INFO2, C.INFO3, C.INFO4, C.INFO5, C.INFO6, C.INFO7, C.INFO8, C.INFO9, C.INFO10, C.INFO11, C.INFO12, C.INFO13, C.INFO14, C.INFO15
    into outfile '$filePath'
    fields terminated by ','
    lines terminated by '\n'
    FROM infodb.TAB_MAIN A, infodb.TAB_BACKUP C WHERE A.ROUTE_ID = C.ROUTE_ID;
    \r"
    
    java -cp data-upload-1.0-SNAPSHOT.jar com.asiainfo.splitData.SplitData $filePath $path start
    
    while ((1)) ; do
    	pid=`ps -ef | grep data-upload-1.0-SNAPSHOT | grep -v grep | awk "{print \$2}"`
    	if [[ "x$pid" -eq "x" ]]; then
    		break
    	fi
    
    	sleep 5s
    done

    rm -rf $filePath
    
    if [[ ! -d $path/temp ]]; then
    	mkdir -p $path/temp
    else
    	rm -rf $path/temp/*
    fi
    
    split -l 100000 -d -a 10 $path/mysqlData.json $path/temp/t
    a=`curl -XGET $esUrl/route | grep 'error' | wc -l`
    if [[ $a -eq 1 ]]; then    
	curl -XPUT $esUrl/route
    fi
    count=`ls $path/temp | wc -l`
    echo "Total files : "  $count
    index=1
    BULK_FILES=$path/temp/t*
    for f in $BULK_FILES; do
        curl -s -XPOST $esUrl/route/info/_bulk -H Content-Type:application/json --data-binary @$f >> /dev/null &
    
        if [ $index == 1 ]; then
        	a=`curl -XGET $esUrl/_settings | cut -d \" -f 18`
          echo $a
        	if [[ $a -ne 0 ]]; then	
               curl -XPUT $esUrl/_settings -H Content-Type:application/json -d ' { "index" : { "number_of_replicas" : 0 } }'
        	fi
        fi
    
        echo $f >> $path/import.log
 
	sleep 5s
 
   done

fi

echo "Operate finished !"
