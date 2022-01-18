# 코틀린 표준함수
- apply<br>
apply함수를 사용하면 수신자를 구성하기 위해 수행되는 모든 함수 호출에 필요한 참조 변수 이름을 생략할 수 있다.<br>
왜냐하면 람다 내부에서 해당 수신자에 대한 모든 함수 호출이 가능하도록 apply 함수가 사용 범위를 설정해 주기 때문이다.<br>
이를 연관 범위 혹은 암시적 호출이라고도 한다.
```
fun main(){
    val menuFile = File("menu-file.txt")
    menuFile.setReadable(true)
    menuFile.setWritable(true)
    menuFile.setExecutable(false)

    val menuFile2 = File("menu-file.txt").apply {
        setReadable(true)
        setWritable(true)
        setExecutable(false)
    }
}
```
- let<br>
let은 함수의 인자로 전달된 람다를 실행한 후 결과를 반환한다. let은 수신자 객체를 람다로 전달한다.<br>
수신자 객체는 `it`을 통해 접근할 수 있으며, 수신자 객체는 읽기 전용변수이다.<br>
람다의 마지막 줄에 있는 코드의 실행결과가 반환된다.
```
fun main(){
    var firstItemSquared = listOf(1,2,3).first().let {
        it * it
    }

    val firstElement = listOf(1,2,3).first()
    val firstItemSquared2 = firstElement * firstElement
}
```
또한 let은 null 복합 연산자(`?.`)와 elvis operator(`?:`)를 함께 사용하여 null 타입에 대한 처리를 진행할 수 있다.
```
fun nullMethod(messeage : String?): String {
    return messeage?.let { 
        "it is not null"
    }?: "it is null"
}
```
- run<br>
run은 apply와 동일하게 수신자 객체에 대한 암시적 호출을 제공하지만, 수신자 객체를 반환하지 않는다는 점에서 다르다.
```
fun main(){
    val menuFile = File("menu-file.txt")
    val content = menuFile.run { 
        readText()
    }
}
```
위와 같은 경우에는 apply처럼 수신자 객체의 메소드를 바로 사용가능하지만, 아래와 같이 chaining을 할 경우 두번째<br>
run의 람다는 전달인자로 File객체가 아닌 readText()의 반환값인 String을 전달받게 된다. 그래서 chaining이 불가능하다.
```
fun main(){
    val menuFile = File("menu-file.txt")
    val content = menuFile.run { 
        readText()
    }.run { 
        readText()
    }
}
```
run함수는 함수 호출이 여러개 중첩되어 있을 때 이를 가독성있게 표현하는데 유용하다.
```
fun nameIsLong(name: String) = name.length >= 20
fun playerCreateMessage(nameTooLong: Boolean):String{
    return if (nameTooLong){
        "Name is Too Long"
    }else{
        "Welcome"
    }
}

fun main(){
    "Polarcubis, Supreme Master of NyetHack"
        .run(::nameIsLong)
        .run(::playerCreateMessage)
        .run(::println)
}
```
만약, run을 사용하지 않고 함수를 중첩해서 사용한다면 다음과 같이 가독성이 떨어질 것이다.
```
println(playerCreateMessage(nameIsLong("Polarcubis, Supreme Master of NyetHack")))
```
- with
with는 수신자 객체를 첫 번쨰 매개변수의 인자로 받는다.
```
val nameTooLong = with("Polarcubis, Supreme Master of NyetHack"){
    length >= 20
}
```
매개변수를 명시적으로 받는 것을 제외하면 run과 동일하기 때문에, run을 사용하는 것을 권하고 있다.
- also<br>
also는 let처럼 자신을 호출한 수신자 객체를 람다의 인자로 전달한다. also는 let과는 다르게 람다의 결과를 반환하지 않고,<br>
수신자 객체를 반환한다.
- takeIf<br>
takeIf는 다른 표준 함수와는 다르게 동작한다. 수신자 객체가 람다의 인자로 전달되며, 람다에 제공된 조건식(predicate)을 실행한 후<br>
그 결과에 따라 ture 또는 false를 반환한다. 만일 조건식의 결과가 true이면 수신자 객체가 반환되며, false면 null이 반환된다.
```
fun main(){
    val fileContents = File("myfile.txt")
        .takeIf{it.canRead() && it.canWrite()}
        ?.readText()
    
//    val file = File("myfile.txt")
//    val fileContents = if(file.canRead() && file.canWrite()){
//        file.readText()
//    }else{
//        null
//    }
}
```













