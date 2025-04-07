#!/bin/bash

file=${1:-jars/uniapr-plugin-1.0-SNAPSHOT.jar}
echo -n "Installing ${file}..."
mvn install:install-file -Dfile=${file} -DgroupId=org.uniapr -DartifactId=uniapr-plugin -Dversion=1.0-SNAPSHOT -Dpackaging=jar > /dev/null 2>&1 && echo " SUCCEEDED" || echo "FAILED"
