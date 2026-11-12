FROM docker.io/library/maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests package

FROM docker.io/library/eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/library-api.jar app.jar
COPY docker-entrypoint.sh /app/docker-entrypoint.sh
RUN useradd --system --uid 10001 --create-home library \
    && chmod 755 /app/docker-entrypoint.sh \
    && chown library:library /app/app.jar /app/docker-entrypoint.sh
USER library
EXPOSE 8080
ENTRYPOINT ["/app/docker-entrypoint.sh"]
