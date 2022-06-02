FROM adoptopenjdk/openjdk11:alpine
MAINTAINER hermeswaldemarin@gmail.com
COPY target/border-crossings-0.0.1-SNAPSHOT.jar border-crossings-0.0.1.jar
ENTRYPOINT ["java","-jar","/border-crossings-0.0.1.jar"]