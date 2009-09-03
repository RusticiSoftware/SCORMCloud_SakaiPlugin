cd ..
mvn clean
rm -fdr install_files/scormcloud-sakai-plugin*
rm -fdr pack/target
rm -fdr impl/target
rm -fdr tool/target
rm -fdr api/target
cd ..
zip -r scormcloud-sakai-plugin-source.zip scorm-cloud/
