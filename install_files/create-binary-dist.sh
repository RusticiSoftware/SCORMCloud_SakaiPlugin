#!/bin/sh

. ./packaging_env.sh

OUTPUT_FOLDER=${OUTPUT_NAME}

mkdir ${OUTPUT_FOLDER}
cp -rv ${CATALINA_HOME}/components/scormcloud-pack ${OUTPUT_FOLDER}/
cp -v  ${CATALINA_HOME}/shared/lib/scormcloud-api-${VERSION}.jar ${OUTPUT_FOLDER}/
cp -v  ${CATALINA_HOME}/webapps/scormcloud-tool.war ${OUTPUT_FOLDER}/
cp -rv ../doc/ ${OUTPUT_FOLDER}/
cp -v ../doc/binary-install.txt ${OUTPUT_FOLDER}/INSTALL.txt
cp install.sh ${OUTPUT_FOLDER}/
cp install.bat ${OUTPUT_FOLDER}/
zip -r ${OUTPUT_NAME}-binary.zip ${OUTPUT_FOLDER}/
rm -fdr ${OUTPUT_FOLDER}
