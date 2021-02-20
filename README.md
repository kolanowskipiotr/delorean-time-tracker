# DeLorean Time Tracker
1. This app let you track time and export it to JIra.
2. This app is POC with fast-and-dirty code (Aplikacja pisana w stylu paździerzowym).

## How to run
### Windows
Use `run.bat`.
### Linux
1. Use `java -jar delorean-time-tracke-{VERSION}.jar` in your favorite terminal.
1. Then open `http://localhost:9889` in your favorite browser.

## Package
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
https://github.com/kolanowskipiotr/delorean-time-tracker

## How to report a problem
https://github.com/users/kolanowskipiotr/projects/2