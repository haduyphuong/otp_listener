/*
 * Plugin Flutter create by Ha Duy Phuong
 * Date create: 07/19/2021
 * Address: Hanoi, Vietnam
 * Email: haduyphuong1996@gmail.com
 * */

package com.ngovang.otp_listener;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;

import static android.app.Activity.RESULT_OK;

/**
 * ListenerSmsOtpPlugin
 */
public class OtpListenerPlugin implements FlutterPlugin, EventChannel.StreamHandler, MethodChannel.MethodCallHandler, PluginRegistry.ActivityResultListener, ActivityAware {
    private static final int SMS_CONSENT_REQUEST = 2;
    private final String NAME_METHOD_CHANNEl = "NAME_METHOD_CHANNEl";
    private final String NAME_EVENT_CHANNEl = "NAME_EVENT_CHANNEl";
    private Context mContext;
    private Activity mActivity;
    private BroadcastReceiver receiver;
    private MethodChannel methodChannel;
    private EventChannel eventChannel;
    private EventChannel.EventSink events;
    private ActivityPluginBinding activityPluginBinding;
    private SmsRetrieverClient smsRetrieverClient;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        mContext = flutterPluginBinding.getApplicationContext();
        smsRetrieverClient = SmsRetriever.getClient(mContext);
        methodChannel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), NAME_METHOD_CHANNEl);
        eventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), NAME_EVENT_CHANNEl);
        methodChannel.setMethodCallHandler(this);
        eventChannel.setStreamHandler(this);
    }


    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull final Result result) {
        if (call.method.equals("setSenderPhone")) {
            String sender = call.argument("phone");
            smsRetrieverClient.startSmsUserConsent(sender);
            result.success(null);
        } else if (call.method.equals("unListener")) {
            if (receiver != null) {
                mContext.unregisterReceiver(receiver);
                receiver = null;
            }
        } else {
            result.notImplemented();
        }
    }


    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        methodChannel.setMethodCallHandler(null);
    }

    private BroadcastReceiver createSmsVerificationReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
                    Bundle extras = intent.getExtras();
                    Status smsRetrieverStatus = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

                    switch (smsRetrieverStatus.getStatusCode()) {
                        case CommonStatusCodes.SUCCESS:
                            Intent consentIntent = extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT);
                            mActivity.startActivityForResult(consentIntent, SMS_CONSENT_REQUEST);
                            break;
                        case CommonStatusCodes.TIMEOUT:
                            // Time out occurred, handle the error.
                            break;
                    }
                }
            }
        };
    }

    @Override
    public void onListen(Object arguments, EventChannel.EventSink events) {
        this.events = events;
        receiver = createSmsVerificationReceiver();
        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        mContext.registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onCancel(Object arguments) {
        if (receiver != null) {
            mContext.unregisterReceiver(receiver);
            receiver = null;
        }
    }


    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SMS_CONSENT_REQUEST:
                if (resultCode == RESULT_OK) {
                    // Get SMS message content
                    String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                    events.success(message);
                }
                break;
        }
        return false;
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        this.mActivity = binding.getActivity();
        activityPluginBinding = binding;
        activityPluginBinding.addActivityResultListener(this);
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {

    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {

    }

    @Override
    public void onDetachedFromActivity() {
        activityPluginBinding.removeActivityResultListener(this);
    }
}
