import 'package:flutter/material.dart';
import 'package:fuck/bridge/iot_event_channel.dart';
import 'package:fuck/bridge/iot_method_channel.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Fuck',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      home: const MyHomePage(title: 'Fuck'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  int i = 1;

  @override
  void initState() {
    super.initState();

    /// 监听信息流方式
    IotEventChannel.instance.addEventHandler("testEvent", (params) {
      print("testEvent result:$params");
    });

    IotEventChannel.instance.addEventHandler("clickFloatingWindowsEvent",
        (params) {
      print("clickFloatingWindowsEvent result:$params");
      setState(() {
        i += 1;
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            ElevatedButton(
              onPressed: () async {
                /// 调用NativeMethods类中的方法
                Map result = await IotMethodChannel.instance.callNativeMethod(
                    "checkAlertWindowsPermission", {"isOnlyCheck": false});
                print("result:${result}");
                if (result["hasPermission"] == true) {
                  Map result = await IotMethodChannel.instance
                      .callNativeMethod("startUpOverlayWindows", {});
                }
              },
              child: Text('Fuck:$i'),
            ),
          ],
        ),
      ),
    );
  }
}
