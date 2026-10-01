# ── Stage 1: Build WAR bằng Maven ────────────────────────────
FROM maven:3.9.6-eclipse-temurin-11 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests --no-transfer-progress

# ── Stage 2: Chạy trên Tomcat 10 ─────────────────────────────
FROM tomcat:10.1-jdk11

# Xóa app mặc định của Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy WAR vào Tomcat
COPY --from=build /app/target/ThanhtoanNganhang.war /usr/local/tomcat/webapps/ROOT.war

# Expose cổng 8080
EXPOSE 8080

CMD ["catalina.sh", "run"]
