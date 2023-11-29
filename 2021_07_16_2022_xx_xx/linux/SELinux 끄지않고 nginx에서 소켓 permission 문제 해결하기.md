# SELinux 끄지않고 nginx에서 소켓 permission 문제 해결하기
참고자료 : [https://serverfault.com/questions/703422/uwsgi-is-ignoring-uid-gid-and-chown-socket](https://serverfault.com/questions/703422/uwsgi-is-ignoring-uid-gid-and-chown-socket)

## 문제상황
nginx와 uwsgi의 소켓을 연결하는 과정에서 nginx가 uwsgi의 소켓에 접근할 수 없는 문제가 발생하였다.

`[errno 13]permission denied`가 주된 에러 정보였다.

### 해결시도

0. `/tmp`이던 소켓파일경로를 flask프로젝트 경로로 수정한다.

    centOS에서 소켓파일을 tmp 폴더에 넣으면 안된다고 한다. tmp는 각 데몬마다 다르게 해당 디렉토리를 인식하기 떄문이다.

    그러나 소켓파일경로를 수정하여도 해결되지 않았다.

1. chmod 777을 통해 파일에 대한 모든 권한을 허용한다.
    
    `srwxrwxrwx` 이와 같은 권한이 uwsgi.sock 파일에 적용된 것을 확인하였다. 그런데 nginx에서 해당 파일에 대해 permission denied가 발생하였다. 파일 권한 문제는 아닌 것 같다. 그렇다면 뭘까
    
2. SELinux와 관련된 사항을 수정해보다
    
    참고자료 : [https://www.nginx.com/blog/using-nginx-plus-with-selinux/](https://www.nginx.com/blog/using-nginx-plus-with-selinux/)
    
    In **permissive** mode, security exceptions are logged to the default Linux audit log, **/var/log/audit/audit.log**. If you encounter a problem that occurs only when NGINX is running in **enforcing** mode, review the exceptions that are logged in **permissive** mode and update the security policy to permit them.
    
    문제의 원인을 찾았다! 위 참고자료에서 나의 에러와 일치하는 문구를 찾았다.
    
    **Issue 2: File Access is Forbidden**
    
    - type : AVC
        
        ```
        Was caused by:
        Missing type enforcement (TE) allow rule.
        	
        You can use audit2allow to generate a loadable module to allow this access.
        ```
        

## Nginx 공식사이트에서 제안한 방법

해결방법은 아래와 같다. 해결방법2에서 성공하였다.

- 해결방법1 : Modify the File Label

- 해결방법2: Extend the `httpd_t` Domain Permissions
    
    httpd_t에 대한 정책을 확장하여 추가 파일 위치에 대한 액세스를 허용하는 방법이다.
    
## nginx SELinux 공식문서 정리

```bash
[root@localhost nginx]# ps -eo euser,ruser,suser,fuser,f,comm,label | grep nginx
root     root     root     root     1 nginx           system_u:system_r:httpd_t:s0
nginx    nginx    nginx    nginx    5 nginx           system_u:system_r:httpd_t:s0
```

nginx는 `httpd_t` 로 라벨링 되어있다. 이는 SELinux에서 nginx가 http통신에 사용되는 포트를 사용하는 것에 대한 권한을 부여한다.

만약 일시적으로 `httpd_t` context에 대한 SELinux 규약을 정지시키려면 다음 명령어를 사용한다.

```bash
// permissive list에 httpd_t를 추가한다는 의미
semanage permissive -a httpd_t
```

```bash
// permissive list에서 httpd_t를 제거한다는 의미
semanage permissive -d httpd_t
```

만약 전역적으로 permissive 모드를 사용하고 싶다면 다음 명령어를 사용한다.

```bash
setenforce 0 // set permissive
setenforce 1 // set enforcing
```

### Issue 2: File Access is Forbidden

기본적으로, SELinux는 Nginx에게 허용된 구역 이외에 파일들에 접근하는 것을 허락하지 않는다. 이 경우 audit log message는 다음과 같다.

audit log는 SELinux가 실행되는 동안의 로그들이 기록되는 파일이다.

```bash
type=AVC msg=audit(1415715270.766:31): avc:  denied  { getattr } for  pid=1380 \
  comm="nginx" path="/www/t.txt" dev=vda1 ino=1084 \
  scontext=unconfined_u:system_r:httpd_t:s0 \
  tcontext=unconfined_u:object_r:default_t:s0 tclass=file
```

위 log message의 의미를 알아보기 위해 `audit2why` 명령어를 사용한다. `audit2why` 는 해당 메시지의 코드를 해석한다.

```bash
# grep 1415715270.766:31 /var/log/audit/audit.log | audit2why
type=AVC msg=audit(1415715270.766:31): avc:  denied  { getattr } for  pid=1380 \
  comm="nginx" path="/www/t.txt" dev=vda1 ino=1084 \
  scontext=unconfined_u:system_r:httpd_t:s0 \
  tcontext=unconfined_u:object_r:default_t:s0 tclass=file
 
    Was caused by:
        Missing type enforcement (TE) allow rule.
 
        You can use audit2allow to generate a loadable module to allow this access.
```

### 해결방법 : Extend the httpd_t Domain Permissions(httpd_t의 권한을 확장한다.)

`audit2allow` 명령어를 사용한다. `audit2allow` 는  권한을 부여하거나 해제하는 기능을 수행한다.

```bash
# grep nginx /var/log/audit/audit.log | audit2allow -m nginx > nginx.te
# cat nginx.te
 
module nginx 1.0;
 
require {
        type httpd_t;
        type default_t;
        type http_cache_port_t;
        class tcp_socket name_connect;
        class file { read getattr open };
}
 
#============= httpd_t ==============
allow httpd_t default_t:file { read getattr open };
 
#!!!! This avc can be allowed using one of these booleans:
#     httpd_can_network_relay, httpd_can_network_connect
allow httpd_t http_cache_port_t:tcp_socket name_connect;
```

`-m` 옵션은 필요로 하는 모듈들을 작성한다. `nginx > nginx.te` 를 통해 결과값을 nginx.te에 저장한다. 

```bash
# grep nginx /var/log/audit/audit.log | audit2allow -M nginx
```

```bash
# semodule -i nginx.pp
# semodule -l | grep nginx
nginx 1.0
```

`-M` 옵션은 불러올 수 있는 모듈 패키지들을 가져온다. 가져온 결과값은 `*.pp`로 저장된다.

`semodule -i` 는 모듈 패키지들을 다운로드한다. `*.pp` 타입의 파일을 이용해 다운로드한다.
