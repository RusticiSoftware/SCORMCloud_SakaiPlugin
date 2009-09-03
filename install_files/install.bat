echo "Copying SCORM Cloud plugin files to %CATALINA_HOME%..."
xcopy /S /Y scormcloud-pack %CATALINA_HOME%\components\
xcopy /S /Y scormcloud-api-0.8.jar %CATALINA_HOME%\shared\lib\
xcopy /S /Y scormcloud-tool.war %CATALINA_HOME%\webapps\
echo "Copying complete."
