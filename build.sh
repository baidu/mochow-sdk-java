export PATH=${JAVA_HOME}/bin:${MAVEN_HOME}:$PATH
mvn clean
mvn -U -DskipTests -Dfile.encoding=UTF-8 -Dmaven.javadoc.skip=true package
rm -rf output
mkdir output
cp target/*.jar output/
cp target/*.zip output/
