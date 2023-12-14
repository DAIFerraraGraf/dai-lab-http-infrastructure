FROM maven:3.9.4-amazoncorretto-21
WORKDIR /app
#COPY target/*.jar /app/app.jar
COPY .. .
RUN mvn clean package
CMD ["java", "-jar", "target/dai-lab-https-1.0-SNAPSHOT-jar-with-dependencies.jar"]