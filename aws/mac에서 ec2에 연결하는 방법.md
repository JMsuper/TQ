# mac에서 ec2에 연결하는 방법
참조 :https://zzang9ha.tistory.com/338
위 링크를 통해 실행하고
EC2 > 인스턴스 > * >인스턴스에 연결 안에서
해당 인스턴스에 들어가는 코드를 터미널에 입력한다.

# nodejs 서버 깃에 올리고 ec2에서 깃클론하여 다운받기
참조 : https://ookm1020.tistory.com/4

# local에 있는 db ec2로 옮기기
1. mysqldump 명령어를 mac 터미널에서 사용하여 로컬에 있는 db를 복사한다.
2. 복사한 db는 .sql로 저장된다.
3. 이를 깃허브에 올리고, ec2에서 git pull 한다.
4. ec2 서버에 db이름에 해당하는 db를 생성한다. : mysqladmin -uroot -p create bikers
5. sql파일을 해당 db에 붙여넣기 한다. : mysql -uroot -p bikers < bikers.sql
