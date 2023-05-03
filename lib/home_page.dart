import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:permission_handler/permission_handler.dart';

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
        'name': "sakib",
        'email': 'barikoi@gmail.com',
        'phone': '01676529696'
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

  Future<void> requestLocationPermission() async {

    final serviceStatusLocation = await Permission.locationWhenInUse.isGranted ;

    bool isLocation = serviceStatusLocation == ServiceStatus.enabled;

    final status = await Permission.locationWhenInUse.request();

    if (status == PermissionStatus.granted) {
      startService();
    } else if (status == PermissionStatus.denied) {
      await openAppSettings();
    } else if (status == PermissionStatus.permanentlyDenied) {
      print('Permission Permanently Denied');
      await openAppSettings();
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
              requestLocationPermission();
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
