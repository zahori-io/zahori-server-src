FROM adoptopenjdk/maven-openjdk11 as javabuilder

COPY pom.xml /code/
WORKDIR /code
RUN mvn dependency:resolve-plugins
RUN mvn dependency:resolve

COPY . /code/
RUN mvn clean package

FROM adoptopenjdk/openjdk11:x86_64-alpine-jre-11.0.10_9

COPY --from=javabuilder /code/target/app.jar /usr/app/app.jar
WORKDIR /usr/app
CMD ["java", "-jar", "/usr/app/app.jar"]
