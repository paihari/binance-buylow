FROM maven as builder
WORKDIR /app
#COPY pom.xml .
#RUN mvn -B -e -C -T 1C org.apache.maven.plugins:maven-dependency-plugin:3.0.2:go-offline
COPY . .
RUN mvn clean package
#RUN mvn -B -e -o -T 1C verify

FROM openjdk:8-jdk-alpine
COPY --from=builder /app/target/*.jar ./
CMD ["java", "-jar", "./buylow-0.0.1-SNAPSHOT.jar"]
