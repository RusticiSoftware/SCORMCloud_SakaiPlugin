#!/bin/sh

echo "Copying SCORM Cloud plugin files to ${CATALINA_HOME}..."
cp -rv scormcloud-pack ${CATALINA_HOME}/components/
cp -v scormcloud-api-0.8.jar ${CATALINA_HOME}/shared/lib/
cp -v scormcloud-tool.war ${CATALINA_HOME}/webapps/
echo "Copying complete."
