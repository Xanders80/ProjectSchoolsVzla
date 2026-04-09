FROM eclipse-temurin:21-jre-alpine AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests -Pprod

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S sms && adduser -S sms -G sms
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
RUN chown -R sms:sms /app
USER sms
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["java", \
  "-XX:+UseG1GC", \
  "-XX:MaxGCPauseMillis=200", \
  "-Xmx512m", \
  "-Xms256m", \
  "-jar", "app.jar", \
  "--spring.profiles.active=${SPRING_PROFILES_ACTIVE:prod}"]
