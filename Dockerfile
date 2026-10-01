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

# Tao setenv.sh bang printf de dam bao newline dung
RUN printf '#!/bin/sh\nexport JAVA_OPTS="$JAVA_OPTS -DDB_URL=$DB_URL -DDB_USER=$DB_USER -DDB_PASSWORD=$DB_PASSWORD"\n' \
    > /usr/local/tomcat/bin/setenv.sh \
    && chmod +x /usr/local/tomcat/bin/setenv.sh

EXPOSE 8080

CMD ["catalina.sh", "run"]
