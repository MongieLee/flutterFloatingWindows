import 'package:flutter/services.dart';

class IotEventChannel {
  static final IotEventChannel instance = IotEventChannel._internal();
  static const platform = EventChannel('cn.mgl.common/event_bridge');
  static final Map<String, List<Function>> _eventHandlers = {};

  IotEventChannel._internal() {
    platform.receiveBroadcastStream().listen(onEventMessage, onError: (error) {
      print("receiveBroadcastStream onError : $error");
    });
  }

  void onEventMessage(dynamic event) {
    String eventName = event["messageType"];
    Map params = event["params"];
    if (_eventHandlers.containsKey(eventName)) {
      var methodQueue = _eventHandlers[eventName];
      for (var func in methodQueue!) {
        func.call(params);
      }
    }
  }

  addEventHandler(String eventName, Function func) {
    if (_eventHandlers.containsKey(eventName)) {
      _eventHandlers[eventName]!.add(func);
    } else {
      _eventHandlers[eventName] = [func];
    }
  }

  removeEventHandler(String eventName, Function func) {
    if (_eventHandlers.containsKey(eventName)) {
      _eventHandlers[eventName]!.remove(func);
    }
  }
}
