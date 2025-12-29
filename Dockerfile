# ---------- Build stage ----------
FROM maven:3-amazoncorretto-21 AS build
WORKDIR /db-writer

# Copy pom.xml first for dependency caching
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Copy source and build
COPY src ./src
RUN mvn -B clean package -DskipTests

# ---------- Runtime stage ----------
FROM amazoncorretto:21.0.9
WORKDIR /db-writer

# Copy only the built jar
COPY --from=build /db-writer/target/*.jar db-writer.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "db-writer.jar", "--spring.profiles.active=k8s"]
