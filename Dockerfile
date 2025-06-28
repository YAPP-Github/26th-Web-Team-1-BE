FROM eclipse-temurin:21
ARG JAR_FILE=build/libs/time-eat-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} api.jar
