FROM openjdk:17-slim

# Install FFmpeg
RUN apt-get update && apt-get install -y ffmpeg && rm -rf /var/lib/apt/lists/*

# Create necessary directories
RUN mkdir -p /uploads/uploaded /uploads/images /uploads/resized/360p /uploads/resized/720p /uploads/resized/1080p

LABEL maintainer="dheerajkhatri2003@gmail.com"
VOLUME /main-app
ADD target/MariaMaven-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar","/app.jar"]