- nginx란?
    
    참고자료 : [https://kscory.com/dev/nginx/install](https://kscory.com/dev/nginx/install)
    참고자료 : [https://www.nginx.com/blog/inside-nginx-how-we-designed-for-performance-scale/](https://www.nginx.com/blog/inside-nginx-how-we-designed-for-performance-scale/)
    
    비동기 Event Driven에 의한 Non Blocking 처리를 하는 웹 서버이다. 물리 메모리가 증가하는 프로세스 기반의 apache에 비해 소비 메모리량이 적어지면서 동시 처리수를 늘릴 수 있다.
    single thread 기반으로 마스터 / worker 프로세스 구동 방식을 채택하여 context switching이 이러나지 않아 cpu 사용률을 감소시킬 수 있다.
    그 외에도 캐시 제공, 리버스 프록시 서버, 로드 밸런서 등의 역할을 주로 담당한다.
    
    nginx의 master process는 configuration을 읽고 포트를 바인딩하는 특권 명령을 수행한다. 그리고 다음의 세 가지 타입의 자식 프로세스들을 생성한다. 
    Cache Loader Process는 디스크 기반 캐시를 메모리에 로드할 때 처음 실행된다. 그리고 사라지지 않고 계속 살아있는다.  
    이 프로세스는 보수적으로(낮은 우선순위로) 스케줄링되기 때문에 적은 리소스를 사용한다.
    Cache Manager Process는 주기적으로 실행되며 디스크 캐시에서 캐싱된 항목을 제거하여 설정되었던 크기만큼으로 유지시킨다.
    Worker Process는 모든 일을 수행한다. 네트워크 연결, 디스크 읽기 및 쓰기, 상위 서버와 소통하기 등을 수행한다. Nginx에서는 CPU의 한 코어당 하나의 Worker Process를 할당하도록 추천한다.
    이게 가장 효율적이라고 한다. 그냥 auto로 세팅하면 알아서 설정해준다. 각각의 Worker Process는 여러 connection들을 non-blocking으로 처리하여
    context switch를 줄인다. 각 Worker Process는 단일 스레드이며 독립적으로 실행된다. 또한 새로운 연결을 잡아 처리한다. 프로세스는 공유 캐시 데이터등 공유 리소스를 통해 통신한다.
    nginx가 효율적인 이유는 non blocking이기 때문이다. 만약 어떤 이벤트에 대해 블락된다면 다른 일을 처리하지 못하고 기다려야 한다.
    즉, 리소스가 놀게 된다. 블락되지 않고 non-block으로 처리하면 기다리지 않고 다른 일들을 처리할 수 있다. 만약 요청이 들어오면 요청을 줄세워놓고
    하나씩 처리하면 된다. 그렇게 되면 리소스가 놀지않고 계속 일하게 할 수 있다.
    
    worker 프로세스는 listen 소켓을 가지고 있다. listen 소켓에서 event가 발생하면 worker 프로세스는 connection socket을 생성한다.
    
    참고링크 : [https://www.aosabook.org/en/nginx.html](https://www.aosabook.org/en/nginx.html)
    
    nginx는 multiplexing 방식을 사용한다. 그리고 특정한 task들을 분리된 프로세스들에게  넘긴다.
    새롭게 생성되는 connection들을 어떤 worker 프로세스가 처리할 지는 nginx에서 정해진 게 없다. 이는 OS 커널 매커니즘에 의해 처리된다.
    nginx의 asynchronous operations는 모듈화, 이벤트 알림, 콜백 함수의 광범위한 사용 및 미세 조정된 타이머를 통해 구현된다?
