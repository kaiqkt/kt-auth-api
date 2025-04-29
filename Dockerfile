FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/*.jar ./

RUN rm *-plain.jar && mv *.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
