# 순수 자바를 사용한 IoC 예제
참고자료 : 토비의 스프링 3.1
일반적으로 프로그램의 흐름은 main() 메소드와 같이 프로그램이 시작되는 지점에서 다음에 사용할 오브젝트를 결정하고,<br>
만들어진 오브젝트의 메소드를 호출하는 과정들을 반복한다. 즉, 자신이 사용할 오브젝트를 자신이 직접 결정하고 생성한다.<br>
그러나 IoC(제어의 역전)에서는 이 제어의 흐름이 거꾸로 뒤집힌다.<br>
<br>
제어의 역전에서는 오브젝트가 자신이 사용할 오브젝트를 스스로 선택하지 않는다. 모든 제어 권한을 자신이 아닌 다른 대상에게 위임한다.<br>
main()을 제외하고 모든 오브젝트에서는 이렇게 위임받은 제어 권한을 갖는 특별한 오브젝트에 의해 결정된다.<br>
<br>
IoC의 예시로는 서블릿, 템플릿 메소드 패턴, 프레임워크 등이 있다.
##### 라이브러리와 프레임워크의 차이
라이브러리를 사용하는 애플리케이션 코드는 애플리케이션 흐름을 직접 제어한다. 단지 동작하는 중에 필요한 기능이 있을 때<br>
능동적으로 라이브러리를 사용할 뿐이다. 반면에 프레임워크는 거꾸로 애플리케이션 코드가 프레임워크에 의해 사용된다.<br>
보통 프레임워크 위에 개발한 클래스를 등록해두고, 프레임워크가 흐름을 주도하는 중에 개발자가 만든 애플리케이션 코드를<br>
사용하도록 한다는 방식이다.<br>


## 예제 코드 설명
<img align="center" src="https://github.com/JMsuper/TIL/blob/main/Spring/img/IoC_ex.JPG" width=700><br>
`UserDao` : DB에 접근하여 `User`모델을 다루는 클래스<br>
`ConnectionMaker` : DB연결을 처리하는 클래스를 만드는 인터페이스<br>
`DConnectionMaker` : `ConnectionMaker`의 구현체로, DB와 관련한 실제 정보를 담고있는 클래스<br>
`DaoFactory` : ConnectionMaker의 구현체를 생성하여 이를 Dao에 주입하여 객체 생성후 Dao를 제공하는 클래스<br>
`UserDaoTest` : UserDao의 인스턴스를 사용하여 실행 테스트하는 클래스<br>
<br>
위 코드들은 IoC 구조를 가지고 있다. UserDao클래스는 `데이터 접근`에만 관심을 두며 `DB접속`에는 관심을 두지 않는다.<br>
DB접속을 담당하는 Connection의 객체를 필요로 하지만, 직접 생성하지 않고 `ConnectionMaker` 인터페이스에게 위임한다.<br>
권한을 위임함으로써 DB접근 코드 수정 시 UserDao의 코드는 수정되지 않는다. 다만, ConnectionMaker를 구현한 구현체의<br>
코드만 달라질 뿐이다.
```
Connection c = connectionMaker.makeConnection();
```
또한, main()함수를 실행시키는 `UserDaoTest` 클래스는 직접 UserDao의 객체를 생성하지 않는다. UserDao의 객체를 생성하려면,<br>
UserDao의 생성자에 ConnectionMaker의 구현체를 주입시켜야 한다. 만약 직접 주입한다면, UserDao의 기능을 테스트하기 위한<br>
목적으로 설계된 UserDaoTest의 기능이 흘들리게 된다. 또 다른 책임까지 떠맡고 있는 것은 문제가 있다.<br>
따라서 `DaoFactory`클래스를 통해 UserDao와 ConnectionMaker 구현 클래스의 오브젝트를 만드는 것과,<br>
그렇게 만들어진 두 개의 오브젝트가 연결돼서 사용될 수 있도록 관계를 맺게 한다.<br>
