#!/bin/sh

. ./packaging_env.sh

cd ..
mvn clean
rm -fdr install_files/*.zip
rm -fdr */target
cd ..
zip -r ${OUTPUT_NAME}-source.zip scorm-cloud/
