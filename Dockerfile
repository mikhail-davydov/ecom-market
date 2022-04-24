FROM gradle:jdk11-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM openjdk:11.0.14.1-jre-slim
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/ecom-market-0.0.1-SNAPSHOT.jar /app/ecom-market-0.0.1-SNAPSHOT.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/ecom-market-0.0.1-SNAPSHOT.jar"]