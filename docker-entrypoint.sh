#!/bin/sh
set -e

# Extract WAR truoc (neu chua co)
if [ ! -d /usr/local/tomcat/webapps/ROOT ]; then
    mkdir -p /usr/local/tomcat/webapps/ROOT
    cd /usr/local/tomcat/webapps/ROOT
    jar -xf /usr/local/tomcat/webapps/ROOT.war
fi

# Tao META-INF neu chua co
mkdir -p /usr/local/tomcat/webapps/ROOT/META-INF

# Ghi context.xml voi gia tri thuc tu environment variables
cat > /usr/local/tomcat/webapps/ROOT/META-INF/context.xml << CTXEOF
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

echo "==> context.xml written with DB_USER=${DB_USER}"

# Khoi dong Tomcat
exec /usr/local/tomcat/bin/catalina.sh run
