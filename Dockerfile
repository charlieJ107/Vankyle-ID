FROM node:latest AS node-builder

COPY web /src
WORKDIR /src
RUN npm install
RUN npm run build


FROM gradle:7.6.1-jdk17-jammy AS java-builder

COPY . /home/gradle/src
COPY --from=node-builder /src/build /home/gradle/src/src/main/resources/public
COPY --from=node-builder /src/build/index.html /home/gradle/src/src/main/resources/templates/
WORKDIR /home/gradle/src

RUN gradle bootJar --no-daemon


FROM openjdk:17-slim AS java-runtime
LABEL authors="Charlie J <charlie_j107@outlook.com>"
COPY --from=java-builder /home/gradle/src/build/libs/*.jar /app/app.jar
WORKDIR /app
ENTRYPOINT ["java", "-jar", "app.jar"]
