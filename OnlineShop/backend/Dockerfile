FROM maven:3.6.3-jdk-11-slim AS build
WORKDIR /app
COPY . ./
RUN mvn clean package -DskipTests
 
FROM adoptopenjdk/openjdk11:jre-11.0.9.1_1-alpine
RUN mkdir /app
COPY --from=build /app/target/online-shop-0.0.1-SNAPSHOT.jar /app/application.jar
WORKDIR /app
CMD "java" "-jar" "application.jar"
