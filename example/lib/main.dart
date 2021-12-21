import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:escposprinter/escposprinter.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  List devices = [];
  bool connected = false;

  @override
  void initState() {
    super.initState();
    initDevices();
  }

  Future<void> initDevices() async {
    try {
      List deviceList = await Escposprinter.usbDeviceList;
      setState(() => devices = deviceList);
    } catch (err) {
      print(err);
    }
  }

  Future<void> connect(int vendorId, int productId) async {
    try {
      bool isSuccess = await Escposprinter.connectPrinter(vendorId, productId);
      setState(() => connected = isSuccess);
    } catch (err) {
      print(err);
    }
  }

  Future<void> testPrint() async {
    try {
      await Escposprinter.printText("Test Print");
    } catch (err) {
      print(err);
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Escposprinter'),
          actions: <Widget>[
            IconButton(
              icon: const Icon(Icons.refresh),
              onPressed: initDevices,
            ),
            connected == true
                ? IconButton(
                    icon: const Icon(Icons.print),
                    onPressed: testPrint,
                  )
                : Container(),
          ],
        ),
        body: devices.isEmpty
            ? const Center(
                child: Text('No devices found'),
              )
            : ListView(
                scrollDirection: Axis.vertical,
                children: devices
                    .map(
                      (device) => ListTile(
                        leading: const Icon(Icons.usb),
                        title: Text(
                            "${device['manufacturer']} ${device['product']}"),
                        subtitle: Text(
                            "${device['vendorId']} ${device['productId']}"),
                        onTap: () => connect(
                          int.parse(device['vendorId']),
                          int.parse(device['productId']),
                        ),
                      ),
                    )
                    .toList(),
              ),
      ),
    );
  }
}
