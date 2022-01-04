import 'dart:developer';
import 'dart:io';

import 'package:flutter/services.dart';

class OtpListener {
  static final _singleton = OtpListener._();
  factory OtpListener() {
    return _singleton;
  }
  static const _eventChannel = const EventChannel('NAME_EVENT_CHANNEl');
  static const _methodChannel = const MethodChannel('NAME_METHOD_CHANNEl');

  OtpListener._();
  void listener(Function(String data) onData, {String? senderFrom}) {
    if (Platform.isAndroid) {
      _methodChannel.invokeMethod('setSenderPhone', {"phone": senderFrom});
      _eventChannel.receiveBroadcastStream().listen((event) {
        onData(event);
      });
    } else {
      log('OtpListener only support Android');
    }
  }

  void unListener() {
    if (Platform.isAndroid) {
      _methodChannel.invokeMethod('unListener');
    } else {
      log('OtpListener only support Android');
    }
  }
}
