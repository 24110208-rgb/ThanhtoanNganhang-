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

# Khai bao bien moi truong (Render se inject gia tri thuc)
ENV DB_URL=""
ENV DB_USER=""
ENV DB_PASSWORD=""

# Truyen bien vao Tomcat qua CATALINA_OPTS
ENV CATALINA_OPTS="-DDB_URL=${DB_URL} -DDB_USER=${DB_USER} -DDB_PASSWORD=${DB_PASSWORD}"

EXPOSE 8080

# Script khoi dong: cap nhat CATALINA_OPTS truoc khi chay Tomcat
CMD export CATALINA_OPTS="-DDB_URL=${DB_URL} -DDB_USER=${DB_USER} -DDB_PASSWORD=${DB_PASSWORD}" && catalina.sh run
