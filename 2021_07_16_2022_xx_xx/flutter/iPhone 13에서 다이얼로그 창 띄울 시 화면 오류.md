# iPhone 13에서 다이얼로그 창 띄울 시 화면 오류
flutter_datetime_picker 라이브러리를 iPhone 13에서 사용할 때 화면에 오류가 발생하는 것을 발견했다.<br>
<img src="https://github.com/JMsuper/TIL/blob/main/flutter/img/Simulator%20Screen%20Shot%20-%20iPhone%2012%20-%202021-11-25%20at%2015.06.24.png" width=200px>
<img src="https://github.com/JMsuper/TIL/blob/main/flutter/img/Simulator%20Screen%20Shot%20-%20iPhone%2013%20-%202021-11-25%20at%2015.06.32.png" width=200px><br>
왼쪽은 iPhone12이고, 오른쪽은 iPhone13이다. 확인해보니 일반적인 showModalBottomSheet는 다른 기기 처럼 정상적으로 화면에 출력되었다.<br>
추측하는 원인은 다음과 같다.<br>
1. 해당화면에 있는 지도 webview는 AbsorbPointer로 감싸져 있다. AbsorbPointer는 웹뷰에 터치 기능이 작동하지 않게하기 위한 것이다.<br>
왜냐하면, 현재 지도는 웹사이트를 띄어서 해당 웹사이트를 플러터에 가져온 것인데, 정적 지도가 아닌 동적 지도이기 때문이다.<br>
여기서 어떤 충돌이 발생한 것 같다.
2. iPhone 13에서의 특성일지 모른다.

1번을 테스트하기 위해 해당 페이지가 아닌 다른 페이지에서도 flutter_datetime_picker의 화면이 동일한지 테스트 해보았다.<br>
<img src="https://github.com/JMsuper/TIL/blob/main/flutter/img/Simulator%20Screen%20Shot%20-%20iPhone%2013%20-%202021-11-25%20at%2015.22.04.png" width=200px>
<br>
확인 결과 flutter_datetime_picker에도 문제가 있고, 웹뷰를 보여주는 페이지에서도 문제가 있었다.<br>
iPhone13에서 flutter_datetime_picker는 지정된 날짜가 아닌 날짜들을 표출하지 않았다.<br>

