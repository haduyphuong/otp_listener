import 'package:flutter/services.dart';

class OtpListener {
  static const _eventChannel = const EventChannel('NAME_EVENT_CHANNEl');
  static const _methodChannel = const MethodChannel('NAME_METHOD_CHANNEl');

  static void listener(Function(String data) onData, {String? senderFrom}) {
    _methodChannel.invokeMethod('setSenderPhone', {"phone": senderFrom});
    _eventChannel.receiveBroadcastStream().listen((event) {
      onData(event);
    });
  }
}
