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

# nodejs에서 로컬 mysql에 접근할 때 오류
Client does not support authentication protocol requested by server; consider upgrading MySQL client
참조 할 링크 : https://stackoverflow.com/questions/50093144/mysql-8-0-client-does-not-support-authentication-protocol-requested-by-server

해결 방법</br>
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'password';

# 외부IP에서 ec2로 http 통신오류(ERR_CONNECTION_REFUSED)
참고 : https://velog.io/@doodream/Express-%EB%B0%B0%ED%8F%AC%ED%95%98%EA%B8%B0
현재 ec2에서는 80번 포트만 열어놨기 때문에 이쪽으로 연결이 들어오면 Nodejs에서 지정한 포트로 연결시키는
포트포워딩을 해야한다.
그러나 포트포워딩으로 해결되지 않았다.

3000번 포트를 그냥 열었을 때는 통신이 되었다.

방화벽 설정 참고자료 : https://webdir.tistory.com/206

# 무중단 서비스를 위한 pm2
참조링크 : https://engineering.linecorp.com/ko/blog/pm2-nodejs/

# 포트포워딩 확인 명령어 및 생성, 삭제 명령어
포트포워딩 확인 : sudo iptables -t nat -L --line-numbers
포트 삭제 : sudo iptables -t nat -D PREROUTING 1(포트포워딩에 있는 번호)
포트 생성 : 

# 사용자가 글을 읽었는지 유무를 나타내는 방법
참조할 링크 : https://namastae112.blogspot.com/2021/09/socket-io-2-pc.html
참조할 링크 : https://okky.kr/article/855452

