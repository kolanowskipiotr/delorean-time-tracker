# DeLorean Time Tracker
This app let you track time and export it to JIra.
This app is POC with fast-and-dirty code (Aplikacja pisana w stylu paździerzowym).

## How to run
### Windows
Use `run.bat`.
### Linux
1. Use `java -jar delorean-time-tracke-{VERSION}.jar` in your favorite terminal.
1. Then open `http://localhost:9889` in your favorite browser.

## Deploy
1. mvn package
1. copy `target\delorean-time-tracke-{VERSION}.jar` to deploy place
1. copy `run.bat` to deploy place
1. copy `src\main\resources\application.properties` to deploy place
1. copy `README.md` to deploy place
1. change `*.jar` file name in `run.bat` file

## Persistence
1. DB console: http://localhost:9889/h2-console/
1. Use props from `application.properties`

## Repository
