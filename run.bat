@ECHO OFF
start java -jar mail-template-sender-0.0.1-SNAPSHOT.jar
ECHO Oczekiwanie na start serwera...
timeout 30
start http://localhost:8080