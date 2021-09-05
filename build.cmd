rem build source
mvn clean package -DskipTests
rem build docker
docker build -t knote-java .
rem tag build
docker tag knote-java luanldt/knote-java:1.0.0
rem push to hub
docker push luanldt/knote-java:1.0.0