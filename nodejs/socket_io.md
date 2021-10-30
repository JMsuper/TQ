# socket.io
### disconnect 와 disconnecting의 차이점
disconnecting : 클라이언트가 이제 disconnected되려고 할 때 불린다.(그러나 아직 룸에서 나가지는 않은 시점이다.)<br>
Fired when the client is going to be disconnected (but hasn't left its rooms yet).<br>
disconnect : Fired upon disconnection.<br>
