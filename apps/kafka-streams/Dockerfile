FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /build

COPY . /build/kafka-streams
WORKDIR /build/kafka-streams
RUN mvn clean install && mvn clean package

FROM openjdk:21-slim
LABEL authors="fernandoareias"

COPY --from=build /build/kafka-streams/target/kafka-stream.jar /app/kafka-streams.jar
WORKDIR /app

ENV JOB_NAME=""

ENTRYPOINT ["java", "-jar", "kafka-streams.jar"]