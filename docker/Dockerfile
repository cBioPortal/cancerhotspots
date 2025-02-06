FROM maven:3.9.9-eclipse-temurin-8
COPY $PWD .
RUN mvn clean install -DskipTests
ENTRYPOINT java -jar webapp/target/cancerhotspots.jar