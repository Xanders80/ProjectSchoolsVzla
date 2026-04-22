# Stage 1: Builder
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

COPY pom.xml ./
RUN mvn dependency:go-offline -B

COPY src/ ./src/
COPY .mvn/ .mvn/
COPY mvnw .mvnw
RUN mvn package -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

COPY --from=builder /build/target/*.jar app.jar

RUN chown appuser:appgroup /app/app.jar && \
    chmod 644 /app/app.jar && \
    mkdir -p /app/logs && \
    chown appuser:appgroup /app/logs

USER appuser

HEALTHCHECK --interval=30s --timeout=10s --start-period=120s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:9000/actuator/health || exit 1

ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseContainerSupport -Djava.security.egd=file:/dev/./urandom"
ENV SERVER_PORT=9000

EXPOSE 9000

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]