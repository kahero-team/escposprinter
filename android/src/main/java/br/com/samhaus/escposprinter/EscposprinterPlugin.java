package br.com.samhaus.escposprinter;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import br.com.samhaus.escposprinter.adapter.USBPrinterAdapter;

import android.app.Activity;
import android.hardware.usb.UsbDevice;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class EscposprinterPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
  private Activity activity;
  private MethodChannel channel;
  private USBPrinterAdapter adapter;
  private ActivityPluginBinding activityPluginBinding;

  public static void registerWith(Registrar registrar) {
    EscposprinterPlugin instance = new EscposprinterPlugin();
    instance.createChannel(registrar.messenger());
  }

  private void createChannel(BinaryMessenger binaryMessenger) {
    channel = new MethodChannel(binaryMessenger, "escposprinter");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    createChannel(flutterPluginBinding.getBinaryMessenger());
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    activityPluginBinding = binding;
    activity = activityPluginBinding.getActivity();
    adapter = USBPrinterAdapter.getInstance();
    adapter.init(activity);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getUsbDeviceList")) {
      getUsbDeviceList(result);
    } else if (call.method.equals("connectPrinter")) { 
      Integer vendorId = call.argument("vendorId");
      Integer productId = call.argument("productId");
      connectPrinter(vendorId, productId, result);
    } else if (call.method.equals("closeConn")) {
      closeConn(result);
    } else if (call.method.equals("printText")) {
      String text = call.argument("text");
      printText(text, result);
    } else if (call.method.equals("printRawData")) {
      String raw = call.argument("raw");
      printRawData(raw, result);
    } else if (call.method.equals("printBytes")) {
      ArrayList<Integer> bytes = call.argument("bytes");
      printBytes(bytes, result);
    } else {
      result.notImplemented();
    }
  }

  public void getUsbDeviceList(Result result) {
    List<UsbDevice> devices = adapter.getDeviceList();
    ArrayList<HashMap> list = new ArrayList<HashMap>();
    for (UsbDevice device : devices) {
      HashMap<String, String> deviceMap = new HashMap<String, String>();
      deviceMap.put("name", device.getDeviceName());
      deviceMap.put("manufacturer", device.getManufacturerName());
      deviceMap.put("product", device.getProductName());
      deviceMap.put("deviceId", Integer.toString(device.getDeviceId()));
      deviceMap.put("vendorId", Integer.toString(device.getVendorId()));
      deviceMap.put("productId", Integer.toString(device.getProductId()));
      list.add(deviceMap);
    }
    result.success(list);
  }

  public void connectPrinter(Integer vendorId, Integer productId, Result result) {
    if (!adapter.selectDevice(vendorId, productId)) {
      result.success(false);
    } else {
      result.success(true);
    }
  }

  public void closeConn(Result result) {
    adapter.closeConnectionIfExists();
    result.success(true);
  }

  public void printText(String text, Result result) {
    adapter.printText(text);
    result.success(true);
  }

  public void printRawData(String base64Data, Result result) {
    adapter.printRawData(base64Data);
    result.success(true);
  }

  public void printBytes(ArrayList<Integer> bytes, Result result) {
    adapter.printBytes(bytes);
    result.success(true);
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    onAttachedToActivity(binding);
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    onDetachedFromActivity();
  }

  @Override
  public void onDetachedFromActivity() {
    if (activityPluginBinding != null) {
      activityPluginBinding = null;
    }
  }
}
