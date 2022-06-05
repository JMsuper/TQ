# ServletContext와 ApplicationContext

ServletContext와 ApplicationContext, WebApplicationContext에 대해서 혼동이 있어 문서로 정리한다.
먼저 각 정의에 대해서 설명한다.

### Servlet
서블릿이란 동적 웹 페이지를 만들 때 사용되는 자바(Java EE) 기반의 웹 애플리케이션
프로그래밍 기술이다. 서블릿은 웹(http) 요청과 응답의 흐름을 간단한 메서드 호출만으로
체계적으로 다룰 수 있게 해준다.

### ServletContext
서블릿 컨텍스트는 하나의 웹 어플리케이션 마다 그리고 JVM마다 오직 하나만 존재한다.
서블릿이 서블릿 컨테이너(ex. 톰캣)과 commucation할 수 있도록 하는 인터페이스들의 집합이다.
또한, 서블릿들이 공유하는 attribute를 저장 및 추출하는 메소드도 지원해준다.
추가적으로 만약 웹 어플리케이션이 여러 JVM에서 분산되어 실행된다면 ServletContext도 여러개가 
존재할 것이다. 따라서 해당 context들은 정보들을 공유할 수 없게 된다.
이 경우 데이터베이스로 이를 대체할 수 있다.

### ApplicationContext
어플리케이션 컨텍스트는 스프링에서 빈들을 관리하는 context이다. bean factory를 상속받으며,
WebApplicationContext에게 상속된다. 어플리케이션 컨텍스트는 두 가지로 나눠진다.

### WebApplicationContext
ApplicationContext를 상속받아 getServletContext()를 추가한 WebApplicationContext 인터페이스의 구현체

### Root Application Context
ContextLoaderListener 클래스에 의해 명시적으로 생성되는 context이다. 여러 dispatcherContext에서 공유되는
빈을 등록하기 위해 사용된다. Child ApplicationContext의 부모이다. 따라서 Child ApplicationContext의 빈에는
접근할 수 없다. 그리고 오직 하나의 root application context만 존재할 수 있다.

### Child Application Context
dispatcherServlet에 의해 생성되는 context이다. dispatherServlet이 여러 개 있을 경우, 각각의 dispatcherServlet마다
child ApplicationContext가 존재할 수 있다. Root Application Context의 자식이며 해당 context의 Bean에 접근할 수 있다.

그런데 최근에는 DispatcherServlet을 하나만 생성해서 사용하기 때문에 굳이 Root ApplicationContext를 생성하지 않고,
하나의 Child WebApplicationContext만 사용한다고 한다.

<img src="https://github.com/JMsuper/TIL/blob/main/img/%EC%8A%A4%ED%94%84%EB%A7%81%20context%20%EA%B5%AC%EC%A1%B0.PNG">
