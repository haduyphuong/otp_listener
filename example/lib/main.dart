import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:otp_listener/otp_listener.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String message = 'Waiting message...';

  @override
  void initState() {
    super.initState();
    OtpListener.listener((data) {
      log(data);
      setState(() {
        message = data;
      });
    }, senderFrom: '19001900'); // or null, listener message of all sender
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('OTP Listener plugin Flutter'),
        ),
        body: Center(
          child: Text(message),
        ),
      ),
    );
  }
}
