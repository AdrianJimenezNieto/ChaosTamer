# ===============================
# FASE 1: BUILD (Construction)
# ===============================
# Use an base image that have Maven and Java 17 installed
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Stablish the work directory inside the container
WORKDIR /app

# Copy the pom.xml
COPY pom.xml .

# Download the dependecies based on the pom
RUN mvn dependency:go-offline

# Copy the source code from the project to the container
COPY src ./src

# Compile the project and create the .jar (skipping test for now)
RUN mvn clean package -DskipTests -Dmaven.test.complie.skip=true

# ===============================
# FASE 2: RUN
# ===============================
# Use a lighter image (only the necesary to run java)
FROM eclipse-temurin:17-jre-alpine

# Work directory of the final image
WORKDIR /app

# Copy only the .jar generated on the fase 1 (build)
# Rename it also to 'app.jar' to be easy to invoke
COPY --from=build /app/target/*.jar app.jar

# Tell docker that our app listen on the port 8080
EXPOSE 8080

# The command will execute when the container runs
ENTRYPOINT [ "java", "-jar", "app.jar" ]