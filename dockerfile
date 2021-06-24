FROM gradle:jdk11 AS build
COPY src /usr/src/app/src
COPY build.gradle /usr/src/app
COPY gradle.properties /usr/src/app
RUN gradle build --build-file /usr/src/app/build.gradle

FROM openjdk:11-jdk-slim
COPY --from=build /usr/src/app/build/libs/*-all.jar /usr/app/keymanager-grpc.jar
EXPOSE 50051
ENTRYPOINT ["java", "-jar", "/usr/app/keymanager-grpc.jar"]