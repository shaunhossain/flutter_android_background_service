import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class HomePage extends StatefulWidget {
  const HomePage({Key? key}) : super(key: key);

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  MethodChannel platform = const MethodChannel('backgroundservice');

  void startService() async {
    try {
      var arguments = {
        'name': "shaun",
        'office': 'barikoi',
      };
      int action = await platform.invokeMethod('start',arguments);
      if (kDebugMode) {
        print(action);
      }
    } on Exception catch (e) {
      print('Error ocurred!!');
    }
  }

  void stopService() async {
    try {
      int action = await platform.invokeMethod('stop');
      if (kDebugMode) {
        print(action);
      }
    } on Exception catch (e) {
      print('Error ocurred!!');
    }
  }

  @override
  Widget build(BuildContext context) {
    return SafeArea(
        child: Scaffold(
      appBar: AppBar(
        title: const Text("Background Trace"),
      ),
      body: Center(
        child: Column(
          children: [
            MaterialButton(
              onPressed: () {
                startService();
              },
              color: Colors.blueAccent,
              child: const Text("Start Service"),
            ),
            const SizedBox(height: 30,),
            MaterialButton(
              onPressed: () {
                stopService();
              },
              color: Colors.blueAccent,
              child: const Text("Stop Service"),
            ),
          ],
        ),
      ),
    ));
  }
}
