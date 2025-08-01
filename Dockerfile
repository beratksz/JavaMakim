FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY target/merkezi-1.0.0.jar merkezi.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "merkezi.jar"]
