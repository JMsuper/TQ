# mysql5.7 에서 한글 가능하게 하는 방법
참고링크 : https://velog.io/@jch9537/Database-MySQL-%ED%95%9C%EA%B8%80%EC%9E%85%EB%A0%A5-vvk5uv7cg0<br>
참고링크 : https://kingle1024.tistory.com/51<br>
참고링크 : https://bae9086.tistory.com/174<br>
<br>
한글을 포함한 문자열을 테이블에 INSERT 했을 때 다음과 같은 오류를 발견했다.<br>
```
ERROR 1366 (HY000): Incorrect string value: '\xEC\x95\x88\xEB\x85\x95' for column 'contents' at row 1
```
이를 해결하기 위해 한글을 사용할 수 있도록 mysql의 설정을 변경해주어야 한다.<br>
`show variables like 'c%'`명령어를 통해 설정을 확인해보니 다음과 같은 결과를 얻었다.<br>
이때 `variables`는 mysql의 변수를 말하며 변수의 종류로는 사용자 변수, 지역 변수, 시스템 변수가 있다.<br>
아래에 출력된 변수들은 시스템 변수이다. 한글로 바꾸기 위해 latin1을 utf8로 바꿔야 한다.<br>
utf8 : 유니코드를 위한 가변 길이 문자 인코딩 방식 중 하나<br>
유니코드 : 세계 모든 언어를 컴퓨터가 인식할 수 있도록 하는 코드
```
| character_set_client     | utf8                       |
| character_set_connection | utf8                       |
| character_set_database   | latin1                     |
| character_set_filesystem | binary                     |
| character_set_results    | utf8                       |
| character_set_server     | latin1                     |
| character_set_system     | utf8    
```
해당 변수를 바꾸기 위해서 my.cnf에 매뉴얼하게 변수 입력 후 재기동해야 한다.<br>
명령어를 통해 변수를 바꿀 경우 mysql을 재기동했을 때 초기 설정값으로 다시 돌아오기 때문에<br>
my.cnf파일의 내용을 수정해주어야 한다.<br>
`sudo vi /etc/mysql/my.cnf` 이 명령어를 통해 파일을 수정한다.

```
[client]

default-character-set=utf8



[mysql]

default-character-set=utf8





[mysqld]

collation-server = utf8_unicode_ci

init-connect='SET NAMES utf8'


character-set-server = utf8
```
수정 이후 mysql을 재기동한다.<br>
`service mysql restart`
```
| character_set_client     | utf8                       |
| character_set_connection | utf8                       |
| character_set_database   | utf8                       |
| character_set_filesystem | binary                     |
| character_set_results    | utf8                       |
| character_set_server     | utf8                       |
| character_set_system     | utf8                       |
```

추가적으로 기존에 존재하던 테이블들의 스키마를 utf8에 맞게 변경해줘야 한다.<br>
`ALTER TABLE table_name convert to charset utf8;`<br>
`alter database [DB명] default character set = utf8;`<br>
db의 스키마도 변경해줘야 한다.

# mysql5.7 에서 이모지 가능하게 하는 방법
참고링크 : https://devhyun.com/blog/post/2<br>
참고링크 : https://artiiicy.tistory.com/31<br>
이모지는 utf8에서 인식되지 않는다. 왜냐하면 mysql에서는 utf8을 3바이트 가변 자료형으로 설계하였기 때문에 4바이트의 데이터가 입력되면</br>
데이터가 손실되기 때문이다. 이러한 문제점을 해결하기 위해 utf8mb4가 추가되었다.</br>
이모지를 사용하려면 utf8mb4로 수정해야한다.</br>
my.cnf 파일을 다음과 같이 수정한다.
```
[client]
default-character-set = utf8mb4

[mysql]
default-character-set = utf8mb4

[mysqld]
character-set-client-handshake = FALSE
character-set-server = utf8mb4
collation-server = utf8mb4_unicode_ci
```
이후 데이터베이스 스키마를 변경한다.
`ALTER DATABASE database_name CHARACTER SET=utf8mb4 COLLATE=utf8mb4_unicode_ci;`<br>
테이블의 스키마도 변경해준다. 그런데 문제가 생겼다.<br>
`ERROR 1833 (HY000): Cannot change column '__': used in a foreign key constraint '______' of table '____'`<br>
외래키문제로 인하여 에러가 발생했다. 낯이 있다. 다행이도 `mysql/210828`글에서 작성한 적이 있다. 해당 글을 통해 수정하면된다.<br>
<br>
수정 이후의 character_set은 다음과 같다.
```
Server characterset:	utf8mb4
Db     characterset:	utf8mb4
Client characterset:	utf8mb4
Conn.  characterset:	utf8mb4
```

