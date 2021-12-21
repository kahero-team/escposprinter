import 'dart:async';

import 'package:flutter/services.dart';

class Escposprinter {
  static const MethodChannel _channel = MethodChannel('escposprinter');

  static Future<List> get usbDeviceList async {
    final List devices = await _channel.invokeMethod("getUsbDeviceList");
    return devices;
  }

  static Future<bool> connectPrinter(int vendorId, int productId) async {
    final bool isSuccess = await _channel.invokeMethod(
      "connectPrinter",
      {"vendorId": vendorId, "productId": productId},
    );
    return isSuccess;
  }

  static Future<bool> get closeConn async {
    final bool isSuccess = await _channel.invokeMethod("closeConn");
    return isSuccess;
  }

  static Future<bool> printText(String text) async {
    final bool isSuccess = await _channel.invokeMethod(
      "printText",
      {"text": text},
    );
    return isSuccess;
  }

  static Future<bool> printRawData(String raw) async {
    final bool isSuccess = await _channel.invokeMethod(
      "printRawData",
      {"raw": raw},
    );
    return isSuccess;
  }

  static Future<bool> printBytes(List<int> bytes) async {
    final bool isSuccess = await _channel.invokeMethod(
      "printBytes",
      {"bytes": bytes},
    );
    return isSuccess;
  }
}
