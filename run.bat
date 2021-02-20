@ECHO OFF
start java -jar delorean-time-tracker-0.0.1-SNAPSHOT.jar
ECHO Waiting [30s] for server start...
timeout 30
start http://localhost:9889