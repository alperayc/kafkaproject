# Adım 1: Projeyi derlemek için Java 21 içeren bir Maven imajı kullan
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Adım 2: Uygulamayı çalıştırmak için de Java 21 içeren küçük bir imaj kullan
FROM openjdk:21-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8888
ENTRYPOINT ["java","-jar","app.jar"]