#!/bin/sh

VERSION=0.8
OUTPUT_FOLDER=scormcloud-sakai-plugin-${VERSION}

mkdir ${OUTPUT_FOLDER}
cp -rv ${CATALINA_HOME}/components/scormcloud-pack ${OUTPUT_FOLDER}/
cp -v  ${CATALINA_HOME}/shared/lib/scormcloud-api-${VERSION}.jar ${OUTPUT_FOLDER}/
cp -v  ${CATALINA_HOME}/webapps/scormcloud-tool.war ${OUTPUT_FOLDER}/
cp -rv ../doc/ ${OUTPUT_FOLDER}/
cp -v ../doc/binary-install.txt ${OUTPUT_FOLDER}/INSTALL.txt
cp install.sh ${OUTPUT_FOLDER}/
cp install.bat ${OUTPUT_FOLDER}/
zip -r scormcloud-sakai-plugin-${VERSION}.zip ${OUTPUT_FOLDER}/
