FROM bellsoft/liberica-openjdk-alpine:17.0.6 as javabuilder

WORKDIR /code
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline package -B

COPY . ./
RUN ./mvnw clean package

FROM eclipse-temurin:17.0.6_10-jre-alpine

COPY --from=javabuilder /code/target/app.jar /usr/app/app.jar
WORKDIR /usr/app
CMD ["java", "-jar", "/usr/app/app.jar"]
