# 자바 reflection
참고자료 : https://medium.com/msolo021015/%EC%9E%90%EB%B0%94-reflection%EC%9D%B4%EB%9E%80-ee71caf7eec5<br>
JVM에서 실행되는 애플리케이션의 런타임 동작을 검사하거나 수정할 수 있는 기능이 필요한 프로그램에서 사용된다.<br>
즉, 런타임 중에 클래스의 구조, 메소드, 필드 등을 개발자가 확인할 수 있고, 값을 가져오거나 수정하거나 메소드를<br>
호출할 수 있게하는 기능이다.
EX) getClass() : 어떤 클래스인지 확인. primitive data type은 getClass() 사용불가
```
        Class c = "foo".getClass();
        System.out.println(c);

        byte[] b = new byte[1024];
        Class c1 = b.getClass();
        System.out.println(c1);
```
EX) forName("") : 클래스의 이름을 통해 해당하는 Class 객체를 가져온다.
```
        Class c7 = Class.forName("javatest.TherClass");
```
EX) isInstance() : 해당 클래스가 매개변수로 넘겨받은 클래스의 인스턴스인지 확인한다.
```
            Class c = Class.forName("javatest.TherClass");
            boolean b = c.isInstance(22);
            System.out.println(b);  //false
            boolean b1 = c.isInstance(new TherClass());
            System.out.println(b1); //true
```
EX) getDeclaredMethods() : 클래스에 선언된 메소드 정보를 담은 Method[]를 반환한다.
```
        Class c = Class.forName("javatest.TherClass");
        Method[] m = c.getDeclaredMethods();
```
이외에도 메소드의 파라미터 정보, Exception 정보등을 가져올 수 있으며, 필드값을 수정할 수 있다.
# annotation
참고자료 : https://advenoh.tistory.com/21<br>
Annotation은 자바 소스코드에 추가적인 정보를 제공하는 메타데이터이다. Annotation은 클래스, 메소드, 변수, 인자에<br>
추가할 수 있다. 메타 데이터이기 때문에 비즈니스 로직에 직접적인 영향을 주지는 않지만, 실행 흐름을 변경할 수 있는<br>
코딩이 가능하여 더 깔끔한 코딩이 가능해진다.<br>

#### 어노테이션 타입
- 마커 어노테이션
  @NewAnnotation
- 싱글 값 어노테이션
  @NewAnnotation(id = 10)
- 멀티 값 어노테이션
  @NewAnnotation(id = 10, name="hello")<br><br>

#### 커스텀 어노테이션
MyAnnotation.java
```
package javatest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)

public @interface MyAnnotation {
    String name();
    String value() default "기본 값";
}
```
TherClass.java
```
package javatest;

public class TherClass{
    @MyAnnotation(name = "doThisMethod", value="Hello Wolrd")
    public void doThis(int a) throws InterruptedException{}

    @MyAnnotation(name = "doThatMethod")
    public void doThat(String s) throws IllegalAccessError{}
}
```
MethodAnnotationExecutor.java
```
package javatest;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class MethodAnnotationExecutor {
    public static void main(String[] args) throws NoSuchMethodException {
        Method method = TherClass.class.getMethod("doThis"); //자바 리플렉션 getMethod로 메서드 doThis를 얻어온다
                Annotation[] annotations = method.getDeclaredAnnotations(); //메서드에 선언된 어노테이션 객체를 얻어온다


        for (Annotation annotation : annotations) {
            if (annotation instanceof MyAnnotation) {
                MyAnnotation myAnnotation = (MyAnnotation) annotation;
                System.out.println("name: " + myAnnotation.name()); //어노테이션에 지정한 값을 프린트한다

                System.out.println("value: " + myAnnotation.value());
            }
        }

        Annotation annotation = TherClass.class.getMethod("doThat")
                .getAnnotation(MyAnnotation.class); //메서드 doThat에 선언된 MyAnnotation의 어노테이션 객체를 얻어온다


        if (annotation instanceof MyAnnotation) {
            MyAnnotation myAnnotation = (MyAnnotation) annotation;
            System.out.println("name: " + myAnnotation.name());
            System.out.println("value: " + myAnnotation.value());
        }
    }
}

```

