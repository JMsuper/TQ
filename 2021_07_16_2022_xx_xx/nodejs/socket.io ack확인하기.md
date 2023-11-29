# socket.io ack확인하기

socket.io를 사용하여 채팅 기능을 개발하던 중,<br>
클라이언트에서 메시지를 잘 받았을 때 서버에게 ack을 보내는데 그 ack을 catch해야 하는 상황이 생겼다.<br>
클라이언트에서 메시지를 읽었으면 DB에서 해당 메시지의 읽음 상태를 업데이트해야하며 상대방 클라이언트에게도<br>
이 사실을 알려야 한다.<br>

그러기 위해 검색하다 socket.io의 Acknowledgements라는 것을 발견하였다.<br>
참고 링크 : https://socket.io/docs/v4/emitting-events/#acknowledgements<br>
socket.io는 기본적으로 TCP프로토콜을 사용한다. 때문에 메시지를 전송하고 이에대한 "ack"을 보내<br>
end-to-end간에 신뢰성 있는 통신을 보장한다.<br>
여기서의 "ack"을 이용하면 채팅 메시지의 읽음여부를 서버에서 확인할 수 있다.<br>
<br>
socket.emit()의 마지막 argument로 콜백함수를 넣을 수 있다. 해당 콜백은 상대방의 ack이 넘어<br>
왔을 때 불려지게 된다.<br>

```
// server-side
io.on("connection", (socket) => {
  socket.on("update item", (arg1, arg2, callback) => {
    console.log(arg1); // 1
    console.log(arg2); // { name: "updated" }
    callback({
      status: "ok"
    });
  });
});

// client-side
socket.emit("update item", "1", { name: "updated" }, (response) => {
  console.log(response.status); // ok
});
```
위 소스코드에서 클라이언트가 서버에게 "update item" 메시지를 보낸다. 이때 콜백함수를 아규먼트에 담아 실행한다.<br>
서버는 정상적으로 메시지를 받았으면 콜백함수를 호출한다. 서버에서 콜백이 호출하면, 클라이트에서 emit()의 인자로 넘겨준<br>
콜백함수가 호출되게된다. 이는 상대방으로 부터 "ack"을 받는 것과 동일한 효과를 준다.<br>
이를 이용하여 클라이언트는 자신이 보낸 메시지가 정상적으로 전송된 것을 알 수 있고,<br>
서버는 클라이언트에게 메시지를 전달했을 때 클라이언트가 해당 메시지를 읽었는지 여부를 확인할 수 있다.

# Room에서 나를 제외한 그룹 전체에게 메시지 보내기
참고 링크 : https://www.zerocho.com/category/NodeJS/post/57edfcf481d46f0015d3f0cd<br>
```
io.to(방의 아이디).emit('이벤트명', 데이터); // 그룹 전체
socket.broadcast.to(방의 아이디).emit('이벤트명', 데이터); // 나를 제외한 그룹 전체
```

bikers앱에서 기존에는 서버에 메시지를 전송하면 서버는 해당 메시지를 Room에 있는 모두에게 "echo"하는 방식이였다.<br>
하지만, 클라이언트에서 네트워크 연결이 불안정할 경우 읽음 표시 문제가 발생함을 확인하였다.<br>
따라서 클라이언트에서 메시지를 전송할 때, 먼저 자신의 앱에서 메시지를 화면에 띄우고 서버에 메시지를 전송해야한다.<br>
이때 메시지는 나를 제외한 그룹 전체에게 보내져야 한다.<br>

### 문제 발생
broadcast에서의 emit함수는 callback함수를 지원하지 않는다. 따라서 ack을 받을 수 없다.<br>
이 경우 ack이 아니라 클라이언트에서 정상적으로 메시지를 받았을 경우 서버에게 "잘 받았어요"라고 메시지를 전송해야 한다.<br>
