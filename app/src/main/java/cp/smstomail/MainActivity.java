package cp.smstomail;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

public class MainActivity extends AppCompatActivity {


    String email_s = "";
    String passwd = "";
    String email_r = "";
    Button button;
    EditText send_e;
    EditText passwd_e ;
    EditText recevied_e ;
    Button button_b;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.send_button);
        button_b=findViewById(R.id.start_button);
        send_e = findViewById(R.id.send_email);
        passwd_e = findViewById(R.id.passwd_input);
        recevied_e = findViewById(R.id.recevied_email);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("hit", "yes");
                //prepare();
                beginService();
            }
        });
//        button_b.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                beginService();
//            }
//        });
    }

    private void initString(){
            email_s = send_e.getText().toString();
            passwd = passwd_e.getText().toString();
            email_r = recevied_e.getText().toString();
    }



    private void beginService(){
        initString();
        button.setText("change");
        Intent intent = new Intent(this,BackService.class);
        intent.putExtra("send_account",email_s);
        intent.putExtra("key",passwd);
        intent.putExtra("receive_account",email_r);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            startForegroundService(intent);
        }else{
            startService(intent);
        }
    }

}