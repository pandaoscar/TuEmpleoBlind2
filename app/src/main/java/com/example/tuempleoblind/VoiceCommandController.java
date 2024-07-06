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

    private VoiceCommandController(Context context) {
        this.context = context.getApplicationContext();
        Intent intent = new Intent(context, VoiceService.class);
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);

        LocalBroadcastManager.getInstance(context).registerReceiver(voiceCommandReceiver,
                new IntentFilter("VOICE_COMMAND"));
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

    public void sendResponseToService(String response) {
        if (isBound) {
            try {
                Message msg = Message.obtain(null, 0);
                Bundle bundle = new Bundle();
                bundle.putString("response", response);
                msg.setData(bundle);
                serviceMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void notifyActivities(String command, String predictedCategory) {
        for (ActivityCallback callback : activityCallbacks) {
            callback.onVoiceCommandReceived(command, predictedCategory);
        }
    }

    public void registerActivityCallback(ActivityCallback callback) {
        activityCallbacks.add(callback);
    }

    public void unregisterActivityCallback(ActivityCallback callback) {
        activityCallbacks.remove(callback);
    }

    public void cleanup() {
        if (isBound) {
            context.unbindService(connection);
            isBound = false;
        }
        LocalBroadcastManager.getInstance(context).unregisterReceiver(voiceCommandReceiver);
    }

    public interface ActivityCallback {
        void onVoiceCommandReceived(String command, String predictedCategory);
    }
}
