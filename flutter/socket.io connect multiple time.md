# socket.io connect multiple time
앱에서 서버와의 소켓연결을 수행할 때 문제가 발생하였다.<br>
처음에 소켓연결할 때는 이상이 없지만, applifecycle이 변화됨에 따라 서버에서 여러 소켓들이 연결되고 있었다.<br>
```
  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    super.didChangeAppLifecycleState(state);
    print('Current state = $state');
    switch (state) {
      case AppLifecycleState.inactive:
        break;
      case AppLifecycleState.resumed:
        _futureInit();
        break;
      case AppLifecycleState.paused:
        if (isRoomExist) {
          socket.emit("leave", joinedRoom);
          socket.dispose();
        }
        break;
      case AppLifecycleState.detached:
        break;
    }
  }
```
```
  Future _futureInit() async {
    ChatMessageList list;
    if (widget.roomId != null) {
      list = await ChatMessage.getChatMessage(
          int.parse(widget.roomId!), widget.userId);
      _messages = chatMsgListConvert(list.chatMessegeList);
      isRoomExist = true;
    } else {
      list = await getChatInShop(widget.goodsId, widget.userId);
      _messages = chatMsgListConvert(list.chatMessegeList);
    }
    if (isRoomExist) {
      socketInit();
      join(list.chatMessegeList[0].roomId.toString());
    }
    if (this.mounted) {
      setState(() {
        futureInitCompleted = true;
      });
    }
  }
```

필자는 앱의 상태가 pause -> resume으로 변경될 때, pause상태인 동안에 받지 못한 메시지를 받기 위해
서버로 부터 메시지들을 전송받았었다. 또한 pause될때 소켓을 dispose()하고 resume되면 다시 소켓을 생성하려고 하였다.
그런데 소켓연결이 여러개 생기는 것을 보아, 소켓이 dispose()되지 않고 있다는 것을 알 수 있었다.

이를 해결하기 위해 다음과 같은 방법들을 모색했다.
1. futureInit()함수는 너무 많은 기능을 수행하고 있다. socketInit()을 호출하여 수행하고 있다.
이는 "함수는 1개의 기능만 수행해야 한다."라는 것에 위반한다. futureInit()함수를 나눠야 한다.

2. socketInit함수는 다음과 같다.
```
  void socketInit() {
    socket = IO.io(ApiConfig.apiUrl, <String, dynamic>{
      "transports": ["websocket"],
      "autoConnect": true,
    });
    socket.onConnect((data) => print("Connected"));
    socket.on("message", (msg) {
      socket.emit("seen", {"room": joinedRoom, "userId": widget.userId});
      addMsgToList(msg["writerId"], msg["msg"]);
      if (this.mounted) setState(() {});
    });

    socket.on("seen", (data) {
      print("seen");
      changeMsgState(types.Status.sent, null);
      if (this.mounted) setState(() {});
    });

    socket.onReconnect((data) => _futureInit());
    socket.onDisconnect((_) => print('disconnect'));
  }
```
여기서 소켓이 reconnect될때 futureInit()함수를 호출하고 있다. futureInit함수에서는 socketInit함수를 호출하고 있기때문에
중복되는 소켓들이 생겨나고 있다. 이를 해결하기 위해 Reconnect되었을 때는 futureInit()을 호출하지 말고,
읽지 않은 메시지만 불러와서 추가해야 한다.

위 방법들을 위한 과정은 다음과 같다.
1. futureInit함수 분해 (0) -> Getx를 활용하여 UI와 로직을 나눈다.
2. applifecycle이 resume상태일 때의 reconnectHandler함수를 만든다.
```
  Future reConnectHandler() async {
    getChat();
    socket.emit("seen", {});
    if(joinedRoom != null)
      join(joinedRoom!);
    update();
  }
```
