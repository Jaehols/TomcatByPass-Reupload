#!/bin/bash

# Run at root of the project directory on a UNIX machine to restart the Tomcat
#  server with changes to your code. Avoids using Intellij for running code...

# NOTE: This will take several minutes to run the first time, since it
#  has to download a bunch of dependencies from Maven central for the mvn 
#  package command.

rm -rf target/

PORT="8080"
APP_NAME="tomcatbypass-1.0-SNAPSHOT"

clear

echo "================="
echo "BUILDING WAR FILE"
echo "================="
mvn clean package -Dmaven.test.skip -DskipTests

if [[ $? -eq 1 ]]; then
    # Build failed.
    exit 1
fi

# While there are still running tomcat processes, end them.
echo "==============="
echo "STOPPING TOMCAT"
while ! [[ -z $(ps -ef | awk '/[t]omcat/{print $2}') ]]
do
    $CATALINA_HOME/bin/shutdown.sh &> /dev/null
    #$CATALINA_HOME/bin/shutdown.sh
    sleep 0.4

    # If there's something still listening on $PORT it's probably
    #  not related to this project: it's probably springboot...
    if ! [[ -z $(netstat -ltn | grep $PORT) ]]
    then
        echo "SOMETHING ALREADY ON PORT $PORT: ABORTING"
        exit 1
    else
        echo "NOTHING ON PORT $PORT"
        break
    fi
done

echo "REMOVING OLD WAR FROM TOMCAT"
rm -rf $CATALINA_HOME/webapps/$APP_NAME

echo "COPYING WAR TO TOMCAT"
cp target/$APP_NAME.war $CATALINA_HOME/webapps/

echo "STARTING TOMCAT"
$CATALINA_HOME/bin/startup.sh &> /dev/null
#$CATALINA_HOME/bin/startup.sh

echo "============================================================="
echo "SERVER UP AT: http://localhost:8080/$APP_NAME"
echo "============================================================="

sleep 2
firefox http://localhost:8080/tomcatbypass-1.0-SNAPSHOT

tail -f $CATALINA_HOME/logs/catalina.out

