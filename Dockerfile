# ── Stage 1: Build WAR ───────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-11 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests --no-transfer-progress

# ── Stage 2: Chay tren Tomcat 10 ─────────────────────────────
FROM tomcat:10.1-jdk11

# Xoa app mac dinh
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy WAR
COPY --from=build /app/target/ThanhtoanNganhang.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

# Tomcat doc JAVA_OPTS khi khoi dong — Render inject DB_URL, DB_USER, DB_PASSWORD
CMD ["/bin/sh", "-c", "export JAVA_OPTS=\"-DDB_URL=${DB_URL} -DDB_USER=${DB_USER} -DDB_PASSWORD=${DB_PASSWORD}\" && catalina.sh run"]
