mvn archetype:create-from-project
cd target/generated-sources/archetype/src/main/resources/archetype-resources
rm -rf .idea srcipt .gitlab-ci.yml *.iml
cd ../../../../
mvn install
mvn deploy:deploy-file  -DgroupId=cn.hyperchain -DartifactId=hvmbasic-archetype -Dversion=1.0.2 -Dpackaging=jar -Dfile=./target/hvmbasic-archetype-1.0.0.jar -Durl=http://cn0:8081/repository/maven-releases/ -DrepositoryId=maven-releases --settings ~/.m2/settings.xml