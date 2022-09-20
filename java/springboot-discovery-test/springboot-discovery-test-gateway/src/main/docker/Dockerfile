FROM eclipse-temurin:8u345-b01-jdk-alpine
ADD *.jar app.jar
ENTRYPOINT java ${JVM_ARGS:=-Xms512m -Xmx512m} -Djava.security.egd=file:/dev/./urandom -jar /app.jar