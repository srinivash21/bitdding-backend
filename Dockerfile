
# ---------- STAGE 1 : Build ----------
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# pom.xml copy
COPY pom.xml .

# download dependencies
RUN mvn dependency:go-offline

# source code copy
COPY src ./src

# build jar
RUN mvn clean package -DskipTests


# ---------- STAGE 2 : Run ----------
FROM eclipse-temurin:17-jre

WORKDIR /app

# jar copy from build stage
COPY target/bid-backend-0.0.1-SNAPSHOT.jar app.jarEXPOSE 8080
EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]