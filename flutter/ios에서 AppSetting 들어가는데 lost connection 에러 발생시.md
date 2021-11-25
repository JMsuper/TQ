# ios에서 AppSetting 들어가는데 lost connection 에러 발생시
`permission_handler`라이브러리의 openAppSetting()함수를 사용하여 앱 세팅 페이지로 가고 싶었다.<br>
하지만 `lost connection`을 마주하였다. 처음에는 snackbar를 통해 앱 세팅으로 넘어가는 것이기 때문에<br>
snackbar의 context가 소멸되어 에러가 발생해 연결이 끊기는 것으로 생각했다.<br>
하지만, 문제의 원인은 그게 아니였다.<br>
원인은 info.plist에 올바른 옵션값을 넣지 않았기 때문에 발생한 일이였다.<br>
분명 camera와 photo에 대한 옵션값을 넣어 info.plist는 문제가 아니라고 생각했다.<br>
<br>
그러나 해당 라이브러리의 readme를 확인해보니 다음과 같은 설명이 있었다.
```
IMPORTANT: You will have to include all permission options when you want to submit your App.<br>
This is because the permission_handler plugin touches all different SDKs and <br>
because the static code analyser (run by Apple upon App submission) detects this and <br>
will assert if it cannot find a matching permission option <br>
in the Info.plist. More information about this can be found here.
```
결론은 매칭되어야 할 옵션값들이 존재하지 않아서 ios os에서 강제종료 시킨 것이였다.<br>
info.plist에 아래 url에 있는 사항을 넣으면 해결된다.<br>
이때 모든 사항을 넣어야 한다. 일부만 넣으면 앱 세팅으로 넘어가지 못한다.<br>
https://github.com/Baseflow/flutter-permission-handler/blob/master/permission_handler/example/ios/Runner/Info.plist<br>
https://github.com/Baseflow/flutter-permission-handler/issues/26<br>
