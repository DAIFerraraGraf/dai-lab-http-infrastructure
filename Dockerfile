FROM maven:3.9.4-amazoncorretto-21
WORKDIR /app
COPY src/ /app/src/
COPY pom.xml /app/
RUN mvn clean package
CMD ["java", "-jar", "target/dai-lab-https-1.0-SNAPSHOT-jar-with-dependencies.jar"]