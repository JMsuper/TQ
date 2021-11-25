# snackbar에서 다른 페이지로 이동 시 오류
참고링크 : https://developpaper.com/how-to-use-willpopscope-in-flutter/<br>
참고링크 : https://stackoverflow.com/questions/68549920/when-snackbar-is-being-displayed-on-screen-and-at-the-same-time-if-back-button-i<br>
<br>
Error : <br>
At this point the state of the widget's element tree is no longer stable.<br>
To safely refer to a widget's ancestor in its dispose() method,<br>
save a reference to the ancestor by calling dependOnInheritedWidgetOfExactType() in the widget's didChangeDependencies() method.<br>
<br>
해당 에러는 왜 발새할까?<br>
snackbar의 특성 때문에 발생한다. snackbar에서 다른 페이지로 이동할 때 WillPopScope로 context가 넘어가지 않아서 발생하는 문제이다.<br>
snackbar에서 다른 페이지로 넘어가고, 해당 페이지에서 돌아올 떄는 더이상 snackbar가 존재하지 않는다. 따라서 snackbar는 dispose되기 때문에<br>
snackbar가 가지고 있는 context를 이동할 페이지에게 전달해줘야 한다.<br>
```
  final GlobalKey<ScaffoldState> _scaffoldKey = GlobalKey<ScaffoldState>();
```
Scaffold의 상태값을 글로벌키로 선언한다.
```
    return Scaffold(
      key: _scaffoldKey,
```
이를 Scaffold의 key값으로 가지고 있으면, 다른페이지에서 back하더라도 문제가 발생하지 않는다.
