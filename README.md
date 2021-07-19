# otp_listener

## Description
This is a library that listens for messages sent to Android phones.


#### This plugin only support listens message in Android

## Example

To use this package :

* add the dependency to your [pubspec.yaml] file.

```yaml
  dependencies:
    flutter:
      sdk: flutter
    otp_listener: ^0.0.2
```

### How to use:

```
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

```