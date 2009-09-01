#!/bin/sh

VERSION=0.8
OUTPUT_FOLDER=scormcloud-sakai-plugin-${VERSION}

mkdir ${OUTPUT_FOLDER}
cp -rv ${CATALINA_HOME}/components/scormcloud-pack ${OUTPUT_FOLDER}/
cp -v  ${CATALINA_HOME}/shared/lib/scormcloud-api-${VERSION}.jar ${OUTPUT_FOLDER}/
cp -v  ${CATALINA_HOME}/webapps/scormcloud-tool.war ${OUTPUT_FOLDER}/
cp binary-install.txt ${OUTPUT_FOLDER}/README.txt
cp adding_cloud_tool_icon.txt ${OUTPUT_FOLDER}/
cp install.sh ${OUTPUT_FOLDER}/
cp install.bat ${OUTPUT_FOLDER}/
zip -r scormcloud-sakai-plugin-${VERSION}.zip ${OUTPUT_FOLDER}/
