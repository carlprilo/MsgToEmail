package cp.smstomail;

import cp.smstomail.MsgReceiver;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import static java.lang.Thread.sleep;

//import static android.app.NotificationChannel.DEFAULT_CHANNEL_ID;


public class BackService extends Service {

    private static final String TAG = "BackService";
    private static final String  CHANNEL_DEFAULT_IMPORTANCE ="CP_ID";
    private static final String DEFAULT_CHANNEL_NAME = "Apple";
    private static final int ONGOING_NOTIFICATION_ID = 0x888;

    private String email_s;
    private String passwd;
    private String email_r;

    private IntentFilter intentFilter;
    private MsgReceiver msgReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Notification notification =
                    new Notification.Builder(this, CHANNEL_DEFAULT_IMPORTANCE)
                            .setContentTitle("title")
                            .setContentText("start")
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setContentIntent(pendingIntent)
                            .setTicker("ticker")
                            .build();
            startForeground(ONGOING_NOTIFICATION_ID, notification);
        } else{
            Notification notification =
                    new Notification.Builder(this)
                            .setContentTitle("title")
                            .setContentText("start")
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setContentIntent(pendingIntent)
                            .setTicker("ticker")
                            .build();
            startForeground(ONGOING_NOTIFICATION_ID, notification);
        }
//        final Boolean running = true;
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (running){
//                try {
//                    sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                    }
//                }
//            //prepare();
//            }
//        }).run();

    }

    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "-->>onBind");
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public void onDestroy() {
        Log.i(TAG, "-->>onDestroy");
        super.onDestroy();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
                 email_s = intent.getStringExtra("send_account");
                 passwd = intent.getStringExtra("key");
                 email_r = intent.getStringExtra("receive_account");
                 Log.d(TAG, "onStartCommand: "+email_s+" "+email_r);
                 prepare();
                 return super.onStartCommand(intent, flags, startId);
    }

    private void prepare(){
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        Log.d(TAG, "prepare: "+email_s+" "+email_r);
        msgReceiver = new MsgReceiver(email_s,passwd,email_r);
        registerReceiver(msgReceiver,intentFilter);
    }

}
