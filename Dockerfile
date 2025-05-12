FROM amazoncorretto:21

WORKDIR /app

COPY target/photoarchival-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 5000

CMD ["java", "-jar", "app.jar"]