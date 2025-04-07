#!/usr/bin/env bash
if [ -z $1 ]; then
	echo "Usage: $0 {{output.jar}}"
	exit 1
fi

files="$(git diff --name-only bb17ac8 -- src/)"
for f in $files; do
	if ! [[ "${f}" =~ src/org/uniapr/.* ]]; then
		printf "unexpected changed file ${f}\n"
		exit 1
	fi
done

classdir=classes
mkdir -p ${classdir} && rm -rf ${classdir}/*
# assuming method declarations don't change, this should be fine
javac -cp jars/uniapr-plugin-1.0-SNAPSHOT.jar ${files} -d ${classdir}
if [ $? != 0 ]; then
	echo "compilation failed"
	exit 1
fi

tmpdir=jar_unzipped
mkdir -p ${tmpdir} && rm -rf ${tmpdir}/*
unzip -q jars/uniapr-plugin-1.0-SNAPSHOT.jar -d ${tmpdir}
\cp -rf ${classdir}/* ${tmpdir}
cd ${tmpdir}
zip -rq ../${1} ./*

echo DONE
