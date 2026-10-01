#!/bin/sh
set -e

# 1. Kiem tra env var bat buoc
[ -z "$DB_URL" ]      && echo "ERROR: DB_URL is not set"      && exit 1
[ -z "$DB_USER" ]     && echo "ERROR: DB_USER is not set"     && exit 1
[ -z "$DB_PASSWORD" ] && echo "ERROR: DB_PASSWORD is not set" && exit 1

WAR=/usr/local/tomcat/webapps/ROOT.war
WEBROOT=/usr/local/tomcat/webapps/ROOT

# 2. Xoa WAR goc TRUOC roi extract bang unzip
#    (Tomcat khong the redeploy tu WAR neu da bi xoa)
mkdir -p "$WEBROOT"
unzip -o "$WAR" -d "$WEBROOT"
rm -f "$WAR"

# 3. Ghi context.xml vao conf/Catalina/localhost/ROOT.xml
mkdir -p /usr/local/tomcat/conf/Catalina/localhost
cat > /usr/local/tomcat/conf/Catalina/localhost/ROOT.xml << CTXEOF
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
    testWhileIdle="true"
    timeBetweenEvictionRunsMillis="30000"
  />
</Context>
CTXEOF

echo "==> OK: WAR extracted to ROOT/, ROOT.xml written for DB_USER=${DB_USER}"

# 4. Khoi dong Tomcat
exec /usr/local/tomcat/bin/catalina.sh run
