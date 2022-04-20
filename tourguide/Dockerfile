FROM openjdk:latest
ADD build/libs/tourguide-0.0.1-SNAPSHOT.jar tourguidems.jar
ENTRYPOINT ["java","-Dspring.profiles.active=docker" ,"-jar","tourguidems.jar"]