#!/bin/sh
set -e

# 1. Kiem tra env var bat buoc
[ -z "$DB_URL" ]      && echo "ERROR: DB_URL is not set"      && exit 1
[ -z "$DB_USER" ]     && echo "ERROR: DB_USER is not set"     && exit 1
[ -z "$DB_PASSWORD" ] && echo "ERROR: DB_PASSWORD is not set" && exit 1

WAR=/tmp/ROOT.war
WEBROOT=/usr/local/tomcat/webapps/ROOT

# 2. Copy WAR ra ngoai TRUOC, xoa WAR goc ngay sau do
#    (tranh Tomcat thay ca ROOT/ + ROOT.war roi redeploy tu WAR, de mat context.xml)
cp /usr/local/tomcat/webapps/ROOT.war "$WAR"
rm -f /usr/local/tomcat/webapps/ROOT.war

# 3. Extract vao webapps/ROOT/
mkdir -p "$WEBROOT"
cd "$WEBROOT"
jar -xf "$WAR"
rm -f "$WAR"

# 4. Ghi context.xml vao conf/Catalina/localhost/ROOT.xml
#    (Tomcat 10 doc file nay uu tien hon META-INF/context.xml)
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

echo "==> OK: WAR extracted, ROOT.xml written for DB_USER=${DB_USER}"

# 5. Khoi dong Tomcat
exec /usr/local/tomcat/bin/catalina.sh run
