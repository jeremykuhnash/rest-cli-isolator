FROM openjdk:8-jdk-alpine
RUN addgroup -S spring && adduser -S spring -G spring
RUN apk add curl expect
USER root:root
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /app.jar
RUN curl -L --http1.1 https://cnfl.io/ccloud-cli | sh -s -- -b /usr/bin
ENTRYPOINT ["java","-jar","/app.jar"]
