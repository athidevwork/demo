#FROM java:8-jdk-alpine

#COPY ./target/demo-0.0.1-SNAPSHOT.jar /usr/app/

#WORKDIR /usr/app

#RUN sh -c 'touch demo-0.0.1-SNAPSHOT.jar'

#ENTRYPOINT ["java","-jar","demo-0.0.1-SNAPSHOT.jar"] 


#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim AS build
COPY src /home/athi/src
COPY pom.xml /home/athi
RUN mvn -f /home/athi/pom.xml clean package

#
# Package stage
#
FROM openjdk:11-jre-slim
#ADD /home/athi/target/demo-0.0.1-SNAPSHOT.jar /target/demo-0.0.1-SNAPSHOT.jar
COPY --from=build /home/athi/target/demo-0.0.1-SNAPSHOT.jar /usr/local/lib/demo.jar
EXPOSE 8090
ENTRYPOINT ["java","-jar","/usr/local/lib/demo.jar"]
