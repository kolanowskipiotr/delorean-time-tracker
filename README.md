# unity-time-tracker

## Deploy
1. set password (`spring.mail.password=`) in `application.properties`
1. mvn package
1. copy `target/mail-template-sender-*.jar` to deploy place
1. copy `run.bat` to deploy place
1. copy `src\main\resources\application.properties` to deploy place
1. change `*.jar` file name in `run.bat` file


## persistence
- DB console: http://localhost:8080/h2-console/
- Use props from `application.properties`