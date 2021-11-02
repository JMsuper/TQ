# bikers 채팅방 로직
조건1 : 채팅방에 첫 시작은 상품 상세화면에서 '거래하기' 버튼 클릭<br>
조건2 : 만약 user가 이미 해당 상품에 관하여 판매자와 채팅을 했었으면 이미 존재하는 해당 채팅방으로 이동<br>
조건3 : 그렇지 않다면, 새로운 채팅방을 만든다.<br>
채팅방은 서버에서 db에 먼저 채팅방을 만든다. -> 생성된 채팅방의 고유ID를 받는다. -> 고유ID를 Room 이름으로 삼는다.<br>
문제 : 채팅창에 들어간 이후 사용자가 어떠한 말도 하지 않은 채 채팅방을 나가게 되면 해당 채팅방을 없애야 한다.<br>
so, 채팅메시지를 처음 보내게 된 순간 db에 채팅방데이터가 생성되어야 한다.<br>
<br>
이를 가능케 하기 위해, 메시지를 보내는 함수가 실행될 때,<br>
1) db에 채팅룸을 만든다. <br>
2) 해당 채팅룸의 ID를 소켓 Room 이름으로 삼고 채팅방에 들어간다. <br>
3) 해당 채팅룸에서 메시지를 전달하고, 해당 메시지를 db에 저장한다.<br>


### 문제발견
- 상대방이 참석했는지, 혹은 내가 방에 참석했는지 상대방이 참석중인 방에 들어간건지 판단할 수 없음
join할 경우에는 확실하게 roomId를 받아 join하면 방이 생성되는 것을 보장한다. 그 이후에 해당 룸에 접근하여
length of clients를 가져올 수 있다.
leave할 경우에는 room이 삭제된 이후에 leave가 호출될 수 있기 때문에, 직접 length를 알려주지 않고,
client에서 자체적으로 leave요청을 받았을 경우 memCount를 -1하도록 한다.

- 상대방이 없는 방에서 내가 참석한 후, 채팅했을 때, -> 이후 상대방이 들어왔을때 대화가 읽음표시로 바껴야 한다.(flutter setState문제)
message.copyWith()함수가 정상작동하지 않는다. 파라미터로 {status: types.Status.seen}를 넘겨줬는데 이게 아닌 것 같다.
문제원인 : copyWith()함수는 해당 데이터를 수정하는 것이 아닌 해당 데이터를 수정한 객체를 리턴한다.
```_messages[i]=_messages[i].copyWith(status: seen) as types.TextMessage;```이와 같이 받야줘야 한다.
폰의 hold버튼을 눌렀을 때 화면이 꺼지고 hold를 풀었을 때 다시 화면이 켜지는 상태를 detect해야한다.
이 상태를 detect해서 hold되었을 때는 socket을 끊고 room에서 leave하며, hold가 풀렸을 때는 socket을 연결하고, room에 join해야 한다.

### app의 상태를 알기 위한 방법
참고 : https://stackoverflow.com/questions/50131598/how-to-handle-app-lifecycle-with-flutter-on-android-and-ios<br>
https://api.flutter.dev/flutter/dart-ui/AppLifecycleState-class.html
