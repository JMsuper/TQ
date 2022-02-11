# spring Dispatcherservlet ClassNotFoundException
pom.xml에 spring mvc dependency를 추가했음에도 불구하고 tomcat 서버 실행 시 다음과 같은 에러를 직면했다.
코드를 작성할 때는 에러가 발생하지 않았다. 라이브러리가 연결된 것은 맞는데 deploy할 때 라이브러리가 연결되지 않는 것 같다.

```
심각: 웹 애플리케이션 [/mvcexam2] 내의 서블릿 [mvc]이(가) load() 예외를 발생시켰습니다.
java.lang.ClassNotFoundException: >org.springframework.web.servlet.DispatcherServlet
	at org.apache.catalina.loader.WebappClassLoaderBase.loadClass(WebappClassLoaderBase.java:1415)
	at org.apache.catalina.loader.WebappClassLoaderBase.loadClass(WebappClassLoaderBase.java:1223)
	at org.apache.catalina.core.DefaultInstanceManager.loadClass(DefaultInstanceManager.java:537)
	at org.apache.catalina.core.DefaultInstanceManager.loadClassMaybePrivileged(DefaultInstanceManager.java:518)
	at org.apache.catalina.core.DefaultInstanceManager.newInstance(DefaultInstanceManager.java:149)
	at org.apache.catalina.core.StandardWrapper.loadServlet(StandardWrapper.java:1071)
	at org.apache.catalina.core.StandardWrapper.load(StandardWrapper.java:1011)
	at org.apache.catalina.core.StandardContext.loadOnStartup(StandardContext.java:4952)
	at org.apache.catalina.core.StandardContext.startInternal(StandardContext.java:5266)
	at org.apache.catalina.util.LifecycleBase.start(LifecycleBase.java:183)
	at org.apache.catalina.core.ContainerBase$StartChild.call(ContainerBase.java:1427)
	at org.apache.catalina.core.ContainerBase$StartChild.call(ContainerBase.java:1417)
	at java.util.concurrent.FutureTask.run(Unknown Source)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(Unknown Source)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(Unknown Source)
	at java.lang.Thread.run(Unknown Source)
```
구글링을 통해 알아낸 해결 방법을 적용해보고 문제의 본질적인 원인이 무엇인지 찾아보자.

## 해결 시도
### pom.xml 수정
실패

### Properties -> Deployment Assembly -> Add Click -> Java Build Path Entries -> Next -> Maven Dependencies -> Finish
next를 클릭해도 Maven dependencies가 없다. 진작에 deployment Assembly에는 Maven dependencies가 추가되어 있다.
실패

### web.xml
톰캣 버전 3.1에 해당하도록 web.xml을 수정한다.
실패
