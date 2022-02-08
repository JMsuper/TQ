# nginx-uwsgi-flask 연동

- flask에서 nginx를 사용하는 이유
    
    참고자료 : [https://vsupalov.com/flask-web-server-in-production/](https://vsupalov.com/flask-web-server-in-production/)
    
    플라스크는 내장 웹서버를 제공한다. 그러나 해당 웹 서버를 배포용으로 사용해서는 안된다. 플라스크 내장 웹 서버는 한번에 한 요청만 접근하기에 적합하기 때문이다.
    
    또한 내장 웹 서버에서 정적 파일을 제공하지만 다른 툴과 비교했을 때 매~~~~우 느리다고 한다.
    
- uwsgi를 사용하는 이유
    
    웹 서버는 웹 어플리케이션에게 요청을 전달하기 위해 CGI라는 인터페이스를 사용한다. 이때 웹 서버는 특정 CGI를 사용하게 된다. 예를 들어 CGI, FaskCGI, mod_python등이 있다. 
    
    이때 python web framework와 동작하기 위해서는 중간 다리역할을 할 대상이 필요하다. 이때 파이썬을 위해 만들어진 cgi가 wsgi이다. uwsgi는 wsgi의 종류 중 많이 쓰이는 플러그인이다. 이외 Gunicon이라는 플러그인도 존재한다.
    
- nginx와 uwsgi의 관계
    
    nginx는 웹 서버이며, uwsgi는 nginx와 flask의 중간 다리 역할을 하는 미들웨어이다. nginx는 웹 어플리케이션이 어떤 언어로 작성되어 있는 지 신경쓰지 않는다. 따라서 nginx와 python으로 만들어진 flask를 연결시키려면 중간 다리인 uwsgi가 있어야 하는 것이다.
    
    nginx와 uwsgi는 소켓을 통해 통신한다. 이러한 이유는 http 통신으로도 통신할 수 있지만, 동일한 서버에서 동작하는 프로세스이기 때문에 소켓 통신을 활용하려 overhead를 줄일 수 있기 떄문이다.
    
- 플라스크의 production stack
    
    production stack에 포함되는 component들은 HTTP 통신을 통해 상호작용한다. 하나의 요청이 처음 웹 서버에 도착했을 경우, 정적 파일 요청이라면 디스크에서 해당 파일을 가져와 response할 것이다. 만약, 정적 파일 요청이 아니라면 해당 요청은 다음 stack에 있는 application server에게 넘어간다.
    
    application server는 요청을 받아들이고 요청에 담긴 정보들을 python 객체로 변환한다. 이러한 과정은 WSGI에 의해 수행된다.
    
    사실 flask app은 흔히 생각하는 서버처럼 동작하지 않는다. 다만, 요청을 기다리며 그에 대해 응답한다. 이는 application server에 의해 불려지는 함수처럼 생각할 수 있다.
    
    수행이 종료되었을 때, 수행 결과는 application server에 의해 HTTP response로 포장된다. 그리고 웹 서버에게 전달되고 웹 서버는 클라이언트에게 response를 전달할 것이다.
    
    즉, 플라스크를 배포하고자 한다면 NginX와 같은 웹 서버가 준비되어야 하며, WSGI application server(ex. Gunicorn)에 의해 플라스크 앱이 다뤄지게 해야한다.
    

- uwsgi 설치 및 설정 파일
    
    uwsgi는 `conda install uwsgi`를  통해 다운로드 한다. pip 명령어를 사용하여 다운로드도 가능하지만, 필자의 경우 에러가 발생하여 그냥 conda 가상환경에 들어가서 다운로드 하는 것이 훨씬 간편하다. 
    
    설치 이후 `uwsgi.ini` 와 `[wsgi.py](http://wsgi.py)` 파일을 생성해야 한다. 해당 파일들은 flask 프로젝트가 있는 폴더에 생성한다.
    
    - uwsgi.ini
        
        ```
        [uwsgi]
        chdir = /home/jm/PycharmProjects/transfer-file-api-flask/PlatformServer/
        callable = app
        process = 2
        module = wsgi:app
        master = true
        http = :5000
        socket = /tmp/uwsgi.sock
        chmod-socket = 666
        die-on-term = true
        ignore-sigpipe=true
        socket-timeout = 3000
        http-timeout = 3000
        ignore-write-errors=true
        disable-write-exception=true
        ```
        
        위 파일은 확장자에서도 들어 나듯이 uwsgi 미들웨어의 초기화를 담당하는 파일이다. 직접 실행할 때 위 사항들을 옵션 값으로 집어넣어 세팅할 수도 있지만, 매번 그렇게 하기에는 번거롭기 때문에 설정 파일을 생성한다.
        
        chdir : 플라스크 프로젝트가 존재하는 폴더경로
        
        callable : 플라스크 앱 객체의 변수명. module 옵션과 함께 사용된다.
        
        process : worker 프로세스의 개수
        
        module : wsgi 모듈을 불러온다
        
        http : http 서버의 주소를 지정. 
        
        socket : 소켓의 저장 경로
        
        chmod-socket : 소켓의 권한 설정 (666 : 모든 사용자가 쓸 수 있게 한다.)
        
        die-on-term : SIGTERM(Ctrl + C 입력)발생 시 재 실행되지 않고 종료된다.
        
        ignore-sigpipe = SIGPIPE(비정상 종료된 클라이언트의 소켓에 send할 때 발생)발생 시 오류를 report 하지 않는다
        
        socket-timeout : 내부 socket의 timeout을 지정
        
        http-timeout : 내부 http 소켓의 timeout을 지정
        
        ignore-write-errors : write()에서 오류가 발생하더라도 보고하지 않는다
        
        disable-write-exception : write()에서 발생하는 exception을 무시한다. 
        
    - wsgi.py
        
        ```python
        from app import app
        
        if __name__ == "__main__":
        		app.run("0.0.0.0")
        ```
        
        from 다음의 `app`은 `Flask` 객체를 생성하는 플라스크 프로젝트 명칭을 의미한다. import 다음의 app은 `Flask` 객체를 담은 변수이다.
        
        위 코드를 통해서 wsgi를 통한 플라스크 앱 실행을 구현할 수 있다.
        
    - uwsgi 실행방법
        
        `uwsgi -i uwsgi.ini &` : &를 넣으면 백그라운드에서 동작한다.
        

- nginx 설치 및 설정 파일
    
    참고자료 : [https://hgko1207.github.io/2020/11/16/linux-9/](https://hgko1207.github.io/2020/11/16/linux-9/)
    
    우분투의 경우 “apt-get” 명령어로 쉽게 다운로드 할 수 있지만, centOS의 경우 조금 복잡한 과정을 거친다.
    
    yum에는 nginx라이브러리가 없기 때문에 nginx를 다운로드 할 수 있는 경로를 추가해야 한다.
    
    `/etc/yum.repos.d/`의 경로에 아래의 nginx.repo 파일을 추가한다.
    
    ```jsx
    [nginx]
    name=nginx repo
    baseurl=http://nginx.org/packages/centos/7/$basearch/
    gpgcheck=0
    enabled=1 
    ```
    
    이후 “yum install -y nginx”를 통해 다운로드 한다.
    
    설정 파일은 `default.conf`와 `nginx.conf`가 있다.
    
    참고자료 : [https://gigas-blog.tistory.com/233](https://gigas-blog.tistory.com/233)
    
    - default.conf
        
        ```
        server {
            listen       8080;
            server_name  localhost;
        
            location / {
                try_files $uri @app;
            }
        
            location @app {
                include uwsgi_params;
                uwsgi_pass unix:/tmp/uwsgi.sock;
            }
            
            error_page   500 502 503 504  /50x.html;
            location = /50x.html {
                root   /usr/share/nginx/html;
            }
        }
        ```
        
        default.conf는 서버의 속성을 나타낸다.
        
        location 태그를 통해 “/”로 들어오는 모든 요청은 @app 으로 전달되며 이는 uwsgi 미들웨어로 전달되는 것이다. 또한, 요청에 대한 결과값도 uwsgi 미들웨어에 의해 받게 된다.
        
    - nginx.conf
        
        ```
        user  nginx;
        worker_processes  auto;
        
        error_log  /var/log/nginx/error.log notice;
        pid        /var/run/nginx.pid;
        
        events {
            worker_connections  1024;
        }
        
        http {
            include       /etc/nginx/mime.types;
            default_type  application/octet-stream;
        
            log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                              '$status $body_bytes_sent "$http_referer" '
                              '"$http_user_agent" "$http_x_forwarded_for"';
        
            access_log  /var/log/nginx/access.log  main;
        
            sendfile        on;
            #tcp_nopush     on;
        
            keepalive_timeout  65;
        
            #gzip  on;
        
            include /etc/nginx/conf.d/*.conf;
        }
        ```
        
        nginx.conf 파일은 http와 관련된 설정과 프로세스에 관련된 설정을 지정한다.
        
    
- nginx 실행 방법
    
    참고 자료 : [https://it-serial.tistory.com/entry/Linux-systemctl-명령어-프로세스-상태-확인](https://it-serial.tistory.com/entry/Linux-systemctl-%EB%AA%85%EB%A0%B9%EC%96%B4-%ED%94%84%EB%A1%9C%EC%84%B8%EC%8A%A4-%EC%83%81%ED%83%9C-%ED%99%95%EC%9D%B8)
    
    systemctl : 서비스를 제어하는 명령어
    
    systemctl enable : 시스템이 재부팅되면 자동으로 실행시키는 명령어
    
    systemctl start : 서비스 실행
    
    systemctl restart : 서비스 재부팅
    
    systemctl reload : 서비스를 재부팅하지 않고 변경된 설정을 적용
    
    systemctl stop : 서비스 중지
    
    systemctl kill : 서비스 종료
    
    systemctl status : 서비스 상태 확인
