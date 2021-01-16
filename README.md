# TaskAgile

Open source task management tool built with Vue.js 2.4.2, Spring Boot 2, and MySQL 5.7+

## git
1. 먼저 github.com 에 로그이해서 New Repository로 새로운 저장소를 만든다
   - 저장소 명 : skyweb.vuejs.spring-boot.mysql
   - public으로 설정
2. 프로젝트 폴더를 마우스 우클릭하여 git bash here로 git bash 실행하면 bash 창이
   생성되면 다음 명령을 실행해서 저장소에 저장한다.
   git init
   git add -A
   git commit -m "create back-end scaffold"
   git remote add origin https://github.com/mir2lsky/skyweb.vuejs.spring-boot.mysql.git
   git push -u origin master

## Local development

Create `src/main/resources/application-dev.properties` with the following settings to override the settings in `application.properties`.

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/task_agile?useSSL=false
spring.datasource.username=<your username>
spring.datasource.password=<your password>
```
windows의 경우 활성 프로파일을 application-dev.properties로 설정하기 위해 시스템 환경변수에 아래 항목 추가 필요
spring_profiles_active : dev
unix/rinux의 경우 배시 프로파일에 export spring_profiles_active=dev를 기입

## Commands

- Use `mvn install` to build both the front-end and the back-end
- Use `mvn test` to run the tests of the back-end and the front-end
- Use `mvn spring-boot:run` to start the back-end
- Use `npm run serve` inside the `front-end` directory to start the front-end
- Use `java -jar target/app-0.0.1-SNAPSHOT.jar` to start the bundled application

## Troubleshooting

- Spring Initializer로 생성 시 java 11로 생성했더니 오류 발생
  => pom.xml에서 Java Version을 8로 바꾸니 정상적으로 실행되는 것을 보니 현재 환경변수
     설정을 java 8로 설정해놔서 그런 것 같음

- java.sql.SQLException: The server time zone value '占쏙옙占싼민깍옙 표占쌔쏙옙' is unrecognized or represents more than one time zone. You must configure either the server or JDBC driver (via the 'serverTimezone' configuration property) to use a more specific time zone value if you want to utilize time zone support.
  => mysql용 database 연결 정보에 serverTimezone=UTC 를 아래와 같이 추가한다.
  spring.datasource.url=jdbc:mysql://localhost:3306/task_agile?useSSL=false&useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC

