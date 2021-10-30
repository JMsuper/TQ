## 채팅 메시지 옆에 상대방이 메시지를 읽었는지 여부 표시하기
메시지 옆에 있는 표시를 영어로 뭐라고 하는지 알지 못해 구글링에서 정보를 찾지 못하고 있다.<br>
delivered, read receipt라고 부른다. 그런데 아쉽게도 필자가 사용중인 chat_ui 라이브러리(flutter)의 깃헙에서 찾아볼 수 없었다.<br>
다른 명칭이 있거나, 혹은 메시지 ui를 커스텀하게 다시 말들어야될지 모른다.
다른 명칭은 status였다. 라이브러리 코드를 확인해보니

```
/// All possible statuses message can have.
enum Status { delivered, error, seen, sending, sent }
```
요게 있었다. 또한, statusBuilder 함수가 있는 것 보니, 메시지의 각각의 상태에 대해서 ui를 다르게 하는 것 같다.<br>

```
class TextMessage extends Message {
  /// Creates a text message.
  const TextMessage({
    required User author,
    int? createdAt,
    required String id,
    Map<String, dynamic>? metadata,
    this.previewData,
    String? remoteId,
    String? roomId,
    Status? status,
    required this.text,
    MessageType? type,
    int? updatedAt,
  }) 
```
```Status? status```에 각 메시지의 상태를 넣으면 될 것 같다.<br>
필자가 원하는 상태는 "읽음","읽지않음"이다. 메시지가 보내지지 않는다면, 애초에 메시지를 보낸 측에 화면에 자신이 보낸 메시지가 뜨지 않을 것이다.<br>
현재 서버에서는 소켓을 통해 메시지가 들어오면 해당 방에 있는 모든 유저에게 메시지를 전송한다.
status는 서버에 저장된 메시지테이블의 "isviewd" 속성을 사용하여 나타낼 수 있다. 
