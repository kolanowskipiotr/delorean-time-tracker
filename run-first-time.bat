@ECHO OFF
start java -jar delorean-time-tracker-1.1.1.jar
ECHO Waiting for server start...
timeout 30
start http://localhost:9889