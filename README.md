# TaskAgile

Open source task management tool built with Vue.js 2.4.2, Spring Boot 2, and MySQL 5.7+
* 환경설정 부분은 너무 내용이 많아서 별도 문서에 정리함.
 
---
# Spring Initializer로 Back-end 기본 틀 생성하기

## git
1. 먼저 github.com 에 로그이해서 New Repository로 새로운 저장소를 만든다
   - 저장소 명 : skyweb.vuejs.spring-boot.mysql
   - public으로 설정
2. 프로젝트 폴더를 마우스 우클릭하여 git bash here로 git bash 실행하면 bash 창이
   생성되면 다음 명령을 실행해서 저장소에 저장한다.
   - git init
   - git add -A
   - git commit -m "create back-end scaffold"
   - git remote add origin https://github.com/mir2lsky/skyweb.vuejs.spring-boot.mysql.git
   - git push -u origin master
3. 최초 push 후 git bash나 github Destop이 아닌 vsc의 source control 탭에서 다음과 같이 처리 가능
   - add stage
   - commit : 코멘트 입력 가능
   - push : 최초 push할 때는 인증을 했는데 2번째 부터는 인증없이 push 가능
   
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

---
# Vue-Cli로 Front-end 기본 틀 생성하기

## 개발 환경
개발 도구의 버전은 다음과 같다.
node v10.23.1 + vue/cli v4.5.11

* node와 vue/cli 버전은 너무 차이가 나면 문제가 된다.
특히 e2e 테스트를 위한 nightwatch의 경우 webdriver와 연동하고 chromedriver는
최신 크롬 브라우저와 연동하기 때문에 chromedriver 또한 최신으로 사용해야 한다.
왜냐하면 크롬 브라우저는 자동 업데이트 되기 때문이다.
즉, 크롬 브라우저로 e2e 테스트를 하려면 크롬 브라우저 버전이 기준이 되어 나머지
개발도구의 버전이 조합된다.

그리고 개발중에는 크롬 브라우저는 자동 업데이트를 막아 놓아야 한다.
가능하면 가상PC에 개발환경을 구성하는 것도 좋은 방법이다.   

## 설치 명령어

vue-cli 설치
npm install -g @vue/cli

vue-cli로 front-end 폴더에 front-end 뼈대 생성
설치 옵션에서 sass설치시 dart-Sasss와 node-Sass 중에서 고민되었으나 ode-Sass로 선택함.
찾아보니 dart-Sass가 cross-platform에 더 강점이 있다고 하나 검새과정에서 보니 요즘은 node-Sass가 더 많이 사용된다고고 함. 어차피 워낙 빠르게 발전하기 때문에 대세를 따르는 것이 낫다고 보여지기 때문.

vue create front-end


## 실행
front-end 폴더에서 
npm run serve

## Troubleshooting
현상1 : node version 8.12.0, npm version 6.4.1 인 상태에서 vue-cli로 front-end 뼈대를 
       생성했으나 chromdriver 플러그인에서 오류가 발생함.
       err! failed at the chromedriver@87.0.5 install script
처리 : npm 버전을 업그레이드 해도 동일한 오류 발생
       혹시 몰라서 sass 버전을 dart-saas와 node-sass로 번갈아 해 보았으나 동일 오류
       다른 로그 중에 프러그인이 node 10이상을 요구한다는 메시지도 있는 걸로 봐서 
       chromdriver 가 최신인데 node버전이 너무 오래되어 오류가 발생한 것으로 추정
       => 기존 node 8.12.0을 완전히 삭제하고 다시 설치하는 김에 여러 노드 버전을 
          관리할 수 있는 nvm을 설치한 다음 node 10의 최고버전인 10.23.1을 설치
       => yarn 설치
       => vue/cli 설치후 vue create front-end를 실행하여 성공적으로 설치완료
원인 : chromdriver 가 최신인데 node버전이 너무 오래되어 오류가 발생

현상2 : nightwatch의 버전이 올라감에 따라 test코드 작성시 버전이 달라서 문제가 생길까
        우려되어 책의 샘플예제에 따라 노드버전을 8.12, vue/cli를 3.0.1로 맞추었더니 
        e2e test에서 chromedriver 오류가 발생
        중간에 기존 책의 샘플 front-end에 node 10.23.1은 그대로 두고 vue/cli만 3.0.1로
        다운그레이드해서 실행했더니 unit 테스트 오류가 났고, node 8.12로 낮추고 실행해도
        동일 오류가 나서 당황했으나 로그를 보고 package-lock.json을 삭제 후 재설치하니
        오류가 사라짐

처리 : e2e 테스트 오류는 chromedriver가 원인인데 node 8.12와 nitchwatch, chromdriver,
       chrome 브라우저가 연결되어 있고 결정적으로 node 8.12와 조합이 맞는 chromedrvier는
       최신버전인 chrome 브라우저와 조압이 맞지 않는다는 것임
       방법은 크롬 브라우저의 버전을 74로 다운 그레이드 하는 것인데 주 사용 브라우저라
       부담이 됨
       다른 방법은 safari를 사용하도록 하는 것인데 단순히 package.json이나 프로젝트 루트 레벨에서 설정파일을 수정하는 방식이 아닌 node-module내부의 nightwatch와 vue의 cli-plugin-e2e-nightwatch 관련해서 설정이 연결되어 있어서 가능하다 해도 사파리 버전
       도 걸리는 등 번거로운 절차를 거쳐야 하기 때문에 시간도 시간이지만 재설치나 간편성 측면에서 문제가 되기 때문에 기각함.
       결국 처음 시작이 e2e테스트 관련 추후 문제발생 가능성 때문인데 다시 생각하면 버전이
       올라갔다고 해도 기본적인 테스트 처리 메카니즘은 바뀔 일이 없다고 판단되어 
       그냥 원래 e2e테스트를 통과한 node 10.23.1, vue/cli 4.5 조합으로 돌아가기로 함
       만약 문제가 발생하더라고 nightwatch를 공부해서 해결하는 것이 낫다는 생각임.

결론 : node버전과 vue/cli버전, chromedriver버전은 어느 정도는 맞춰야 한다.
       nodel버전이 너무 오래되면 조합에 문제가 발생함.
       
---
# back-end와 front-end 연결하기

## 하나의 명령어로 빌드하기
* Maven을 활용하여 통합 빌드
프런트 엔드를 백앤드의 하위 폴더로 놓았기 때문에 npm을 제어하는데 maven을 활용
또 다른 이유는 메이븐이 전체 빌드 라이프 사이클에서 효과적으로 활용할 수 있는 다양한 내장 페이즈를 지원하기 때문임.

* pom.xml에서 다음의 플러그 인을 사용하여 통합 빌드
Exec Maven Plugin : npm 명령어를 실행
Maven Resource Plugin : 프런트 엔드 리소스를 복사
Spring Boot Maven Plugin : Spring Boot App를 시작하고 종료
Maven Clean Plugin : 빌드에 영향을 줄 리소스르 제거, ex) 기 빌드된 리소스 제거

## 통합 빌드 실행
mvn install

기존 빌드를 깨끗이 삭제하고 빌드(권장)
mvn clean install
