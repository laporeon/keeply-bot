FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -B

FROM eclipse-temurin:21-jre-alpine-3.21
WORKDIR /app

RUN apk upgrade --no-cache \
    && addgroup -S keeplybot \
    && adduser -S keeplybot -G keeplybot

COPY --from=build --chown=keeplybot:keeplybot /app/target/keeplybot.jar app.jar

USER keeplybot

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
