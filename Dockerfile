FROM eclipse-temurin:21-jre-alpine

# === Build-time arguments ===
ARG GIT_COMMIT=unknown
ARG GIT_AUTHOR=unknown
ARG BUILD_DATE=unknown
ARG VERSION=unknown
ARG APP_TIMEZONE=Africa/Dar_es_Salaam

# === Set timezone ===
ENV TZ=${APP_TIMEZONE}
RUN apk add --no-cache tzdata && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone && \
    apk del tzdata # cleanup to reduce image size


# === Metadata Labels ===
LABEL org.opencontainers.image.title="Rental Management API" \
      org.opencontainers.image.description="This API provides endpoints for managing rental properties, landlords, tenants, leases, and payments in Tanzania." \
      org.opencontainers.image.version="${VERSION}" \
      org.opencontainers.image.created="${BUILD_DATE}" \
      org.opencontainers.image.authors="${GIT_AUTHOR}" \
      org.opencontainers.image.revision="${GIT_COMMIT}" \
      org.opencontainers.image.timezone="${APP_TIMEZONE}" \


WORKDIR /app

# Create a non-root user to run the application
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup


# Copy the application JAR file
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Expose the application port
EXPOSE 8080
USER appuser
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