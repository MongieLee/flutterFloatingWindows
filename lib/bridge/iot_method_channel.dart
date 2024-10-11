import 'package:flutter/services.dart';

class IotMethodChannel {
  static const methodChannel = MethodChannel('cn.mgl.common/method_bridge');
  static final IotMethodChannel instance = IotMethodChannel._internal();

  IotMethodChannel._internal();

  Future<Map<String, Object>> callNativeMethod(
      String methodName, Map<String, Object> params) async {
    try {
      final result = await methodChannel.invokeMethod(methodName, params);
      print("callNativeMethod [${methodName}] result:$result");
      if (result is String) {
        return Map<String, Object>.from({"result": result});
      }
      return Map<String, Object>.from(result);
    } catch (e) {
      print('invoke method $methodName error: $e');
      return <String, Object>{};
    }
  }
}
