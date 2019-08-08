FROM maven as maven
COPY ./pom.xml ./pom.xml

RUN mvn dependency:go-offline -B

COPY ./src ./src


RUN mvn -Dmaven.test.skip=true package

FROM openjdk:8u171-jre-alpine

EXPOSE 8080

WORKDIR /app

COPY --from=maven target/*.jar ./

CMD ["java", "-jar", "./buylow-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=prod"]

#WORKDIR /app
##COPY pom.xml .
##RUN mvn -B -e -C -T 1C org.apache.maven.plugins:maven-dependency-plugin:3.0.2:go-offline
#COPY . .
#RUN mvn clean package
##RUN mvn -B -e -o -T 1C verify
#
#FROM openjdk:8-jdk-alpine
#WORKDIR /app
#COPY --from=0 /app/*.jar ./
#CMD ["java", "-jar", "./buylow-0.0.1-SNAPSHOT.jar"]