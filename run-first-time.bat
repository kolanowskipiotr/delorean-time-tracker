@ECHO OFF
start java -jar delorean-time-tracker-0.0.2-SNAPSHOT.jar
ECHO Waiting for server start...
timeout 30
start http://localhost:9889