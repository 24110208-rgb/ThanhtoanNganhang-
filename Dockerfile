# ── Stage 1: Build WAR ───────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-11 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests --no-transfer-progress

# ── Stage 2: Chay tren Tomcat 10 ─────────────────────────────
FROM tomcat:10.1-jdk11

RUN rm -rf /usr/local/tomcat/webapps/*

COPY --from=build /app/target/ThanhtoanNganhang.war /usr/local/tomcat/webapps/ROOT.war

# Script startup: ghi context.xml voi gia tri thuc tu env var
COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh

EXPOSE 8080

ENTRYPOINT ["/docker-entrypoint.sh"]
