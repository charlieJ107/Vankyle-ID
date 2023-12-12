FROM gradle:8.3.1-jdk17-jammy AS java-builder

COPY . /home/gradle/src
WORKDIR /home/gradle/src

RUN gradle bootJar --no-daemon


FROM openjdk:17-slim AS java-runtime
LABEL authors="Charlie J <charlie_j107@outlook.com>"
COPY --from=java-builder /home/gradle/src/build/libs/*.jar /app/app.jar
WORKDIR /app
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=production
ENTRYPOINT ["java", "-jar", "app.jar"]
