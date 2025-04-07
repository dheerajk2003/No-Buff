FROM openjdk:17
LABEL maintainer="dheerajkhatri2003@gmail.com"
VOLUME /main-app
ADD target/MariaMaven-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar","/app.jar"]