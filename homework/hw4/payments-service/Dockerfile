FROM gradle:8-jdk17 AS build
ENV JAVA_TOOL_OPTIONS="-Djdk.tls.client.protocols=TLSv1.2"
WORKDIR /app
COPY . .
RUN gradle :payments-service:shadowJar --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/payments-service/build/libs/*-all.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]

