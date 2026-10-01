#!/bin/sh
set -e

WAR=/tmp/ROOT.war
WEBROOT=/usr/local/tomcat/webapps/ROOT

# Extract WAR vao /tmp truoc, sau do copy vao webapps
mkdir -p "$WEBROOT"
cp /usr/local/tomcat/webapps/ROOT.war "$WAR"
cd "$WEBROOT"
jar -xf "$WAR"
rm -f "$WAR"

# Xoa WAR goc de Tomcat khong tu deploy lai
rm -f /usr/local/tomcat/webapps/ROOT.war

# Ghi context.xml voi gia tri thuc tu environment variables
mkdir -p "$WEBROOT/META-INF"
cat > "$WEBROOT/META-INF/context.xml" << CTXEOF
<?xml version="1.0" encoding="UTF-8"?>
<Context path="/">
  <Resource
    name="jdbc/thanhtoanDB"
    auth="Container"
    type="javax.sql.DataSource"
    driverClassName="com.mysql.cj.jdbc.Driver"
    url="${DB_URL}"
    username="${DB_USER}"
    password="${DB_PASSWORD}"
    maxTotal="10"
    maxIdle="5"
    minIdle="1"
    maxWaitMillis="10000"
    validationQuery="SELECT 1"
    testOnBorrow="true"
  />
</Context>
CTXEOF

echo "==> WAR extracted, context.xml written for DB_USER=${DB_USER}"

# Khoi dong Tomcat
exec /usr/local/tomcat/bin/catalina.sh run
