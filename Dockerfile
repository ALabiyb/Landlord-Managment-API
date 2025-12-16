FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]


# Multi-stage Dockerfile – Java 21 + Spring Boot 3.3+ / Spring 7 compatible
#FROM maven:3.9.9-eclipse-temurin-21 AS build
#WORKDIR /app
#
## Optional but recommended: cache dependencies
#COPY pom.xml .
#RUN mvn -B dependency:go-offline
#
#COPY src ./src
#RUN mvn -B -DskipTests package
#
## Runtime stage – you can keep Java 17 here if you want smaller image
#FROM eclipse-temurin:21-jre-alpine
#WORKDIR /app
#COPY --from=build /app/target/*.jar app.jar
#EXPOSE 8080
#ENTRYPOINT ["java", "-jar", "/app/app.jar"]