FROM gradle:8.14-jdk17 AS build

ENV JAVA_TOOL_OPTIONS="-Djdk.tls.client.protocols=TLSv1.2"

WORKDIR /app

COPY . .

RUN gradle :apphost:shadowJar --no-daemon

FROM amazoncorretto:17

WORKDIR /app

COPY --from=build /app/apphost/build/libs/*-all.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]