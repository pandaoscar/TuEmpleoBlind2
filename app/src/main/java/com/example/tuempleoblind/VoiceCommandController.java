package com.example.tuempleoblind;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;

public class VoiceCommandController {

    private static VoiceCommandController instance;
    private Messenger serviceMessenger;
    private boolean isBound;
    private Context context;
    private List<ActivityCallback> activityCallbacks = new ArrayList<>();
    private List<BooleanCallback> booleanCallbacks = new ArrayList<>();

    private VoiceCommandController(Context context) {
        this.context = context.getApplicationContext();
        Intent intent = new Intent(context, VoiceService.class);
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);

        LocalBroadcastManager.getInstance(context).registerReceiver(voiceCommandReceiver,
                new IntentFilter("VOICE_COMMAND"));
        LocalBroadcastManager.getInstance(context).registerReceiver(booleanCommandReceiver,
                new IntentFilter("BOOLEAN_COMMAND"));
    }

    public static VoiceCommandController getInstance(Context context) {
        if (instance == null) {
            instance = new VoiceCommandController(context);
        }
        return instance;
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            serviceMessenger = new Messenger(service);
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            serviceMessenger = null;
            isBound = false;
        }
    };

    private BroadcastReceiver voiceCommandReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String command = intent.getStringExtra("command");
            String predictedCategory = intent.getStringExtra("predictedCategory");
            notifyActivities(command, predictedCategory);
        }
    };

    private BroadcastReceiver booleanCommandReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean booleanValue = intent.getBooleanExtra("booleanValue", false);
            notifyBooleanCallbacks(booleanValue);
        }
    };

    public void sendResponseToService(String response) {
        sendMessageToService("response", response);
    }

    public void sendGoogleAlert(String alert) {
        sendMessageToService("google", alert);
    }

    public void sendRoleUser(String role) {
        sendMessageToService("role", role);
    }

    private void sendMessageToService(String key, String value) {
        if (isBound) {
            try {
                Message msg = Message.obtain(null, 0);
                Bundle bundle = new Bundle();
                bundle.putString(key, value);
                msg.setData(bundle);
                serviceMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("estoy en controller");
        }
    }

    private void notifyActivities(String command, String predictedCategory) {
        for (ActivityCallback callback : activityCallbacks) {
            callback.onVoiceCommandReceived(command, predictedCategory);
        }
    }

    private void notifyBooleanCallbacks(boolean booleanValue) {
        for (BooleanCallback callback : booleanCallbacks) {
            callback.onBooleanCommandReceived(booleanValue);
        }
    }

    public void registerActivityCallback(ActivityCallback callback) {
        activityCallbacks.add(callback);
    }

    public void unregisterActivityCallback(ActivityCallback callback) {
        activityCallbacks.remove(callback);
    }

    public void registerBooleanCallback(BooleanCallback callback) {
        booleanCallbacks.add(callback);
    }

    public void unregisterBooleanCallback(BooleanCallback callback) {
        booleanCallbacks.remove(callback);
    }

    public void cleanup() {
        if (isBound) {
            context.unbindService(connection);
            isBound = false;
        }
        LocalBroadcastManager.getInstance(context).unregisterReceiver(voiceCommandReceiver);
        LocalBroadcastManager.getInstance(context).unregisterReceiver(booleanCommandReceiver);
    }

    public interface ActivityCallback {
        void onVoiceCommandReceived(String command, String predictedCategory);
    }

    public interface BooleanCallback {
        void onBooleanCommandReceived(boolean booleanValue);
    }
}
